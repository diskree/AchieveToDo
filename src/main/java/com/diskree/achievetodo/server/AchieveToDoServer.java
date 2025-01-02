package com.diskree.achievetodo.server;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.ability.generation.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import com.diskree.achievetodo.injection.extension.main.LevelInfoExtension;
import com.diskree.achievetodo.networking.c2s.DemystifyAbilityPayload;
import com.diskree.achievetodo.networking.s2c.*;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import com.diskree.achievetodo.tracking.TrackedStatType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AchieveToDoServer implements ServerModInitializer {

    private Map<AbilityType, Integer> abilitiesConfiguration = new HashMap<>();
    private final Map<UUID, Integer> advancementsCountByPlayers = new Object2IntOpenHashMap<>();
    private final EnumMap<TrackedScoreType, Map<UUID, Integer>> trackedScores =
        new EnumMap<>(TrackedScoreType.class);
    private final EnumMap<TrackedStatType, Map<UUID, Integer>> trackedStats =
        new EnumMap<>(TrackedStatType.class);

    private final Map<ChunkPos, Map<LandmarkType, List<BlockBox>>> landmarksByChunks = new HashMap<>();
    private final Map<UUID, List<LandmarkType>> lockedLandmarkTypesByPlayers = new HashMap<>();

    public AdvancementsMode currentAdvancementsMode;
    public ScoreboardObjective currentScoreboardObjective;
    public ScoreboardDisplaySlot currentScoreboardDisplaySlot;

    public void prepareScoreboard(ServerScoreboard scoreboard) {
        AdvancementsMode oldAdvancementsMode = currentAdvancementsMode;
        ScoreboardObjective oldScoreboardObjective = currentScoreboardObjective;
        ScoreboardDisplaySlot oldScoreboardDisplaySlot = currentScoreboardDisplaySlot;

        currentAdvancementsMode = null;
        currentScoreboardObjective = null;
        currentScoreboardDisplaySlot = null;

        ScoreboardDisplaySlot[] slotPriority = {
            ScoreboardDisplaySlot.SIDEBAR, ScoreboardDisplaySlot.LIST, ScoreboardDisplaySlot.BELOW_NAME
        };
        for (ScoreboardDisplaySlot displaySlot : slotPriority) {
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(displaySlot);
            if (objective != null) {
                AdvancementsMode advancementsMode = AdvancementsMode.findByObjectiveName(objective.getName());
                if (advancementsMode != null) {
                    currentAdvancementsMode = advancementsMode;
                    currentScoreboardObjective = objective;
                    currentScoreboardDisplaySlot = displaySlot;
                    break;
                }
            }
        }
        if (currentAdvancementsMode == null ||
            currentScoreboardObjective == null ||
            currentScoreboardDisplaySlot == null
        ) {
            AchieveToDoMod.logger.error("Can't find advancements counter in scoreboard! " +
                "Please check that BACAP datapack is installed " +
                "or enable advancements counter in the sidebar, tab list or below player names.");
            return;
        }
        if (currentAdvancementsMode != oldAdvancementsMode ||
            currentScoreboardObjective != oldScoreboardObjective ||
            currentScoreboardDisplaySlot != oldScoreboardDisplaySlot
        ) {
            for (ServerPlayerEntity serverPlayer : scoreboard.server.getPlayerManager().getPlayerList()) {
                updateObtainedAdvancementsCount(scoreboard, serverPlayer);
            }
        }
    }

    public void setObtainedAdvancementsCount(@NotNull ServerPlayerEntity player, int count) {
        if (abilitiesConfiguration == null) {
            return;
        }
        UUID playerUuid = player.getUuid();
        int oldCount = advancementsCountByPlayers.getOrDefault(playerUuid, 0);
        advancementsCountByPlayers.put(playerUuid, count);
        for (AbilityType ability : AbilityType.values()) {
            int requiredAdvancementsCount = abilitiesConfiguration.get(ability);
            if (requiredAdvancementsCount <= 0) {
                continue;
            }
            boolean isLock;
            if (count >= requiredAdvancementsCount && oldCount < requiredAdvancementsCount) {
                isLock = false;
            } else if (oldCount == 0 || count < requiredAdvancementsCount && oldCount >= requiredAdvancementsCount) {
                isLock = true;
            } else {
                continue;
            }
            if (oldCount != 0) {
                setAbilityLocked(player, ability, isLock);
            }
            LandmarkType abilityLandmark = ability.getLandmark();
            if (abilityLandmark != null) {
                if (isLock) {
                    lockedLandmarkTypesByPlayers.computeIfAbsent(playerUuid, k -> new ArrayList<>())
                        .add(abilityLandmark);
                } else {
                    List<LandmarkType> landmarkTypes = lockedLandmarkTypesByPlayers.get(playerUuid);
                    if (landmarkTypes != null) {
                        landmarkTypes.remove(abilityLandmark);
                        if (landmarkTypes.isEmpty()) {
                            lockedLandmarkTypesByPlayers.remove(playerUuid);
                        }
                    }
                }
                for (Map<LandmarkType, List<BlockBox>> landmarks : landmarksByChunks.values()) {
                    for (LandmarkType landmarkType : landmarks.keySet()) {
                        if (abilityLandmark == landmarkType) {
                            for (BlockBox blockBox : landmarks.get(landmarkType)) {
                                ServerPlayNetworking.send(player, new SyncLockedLandmarkBoxPayload(
                                    landmarkType,
                                    blockBox,
                                    isLock
                                ));
                            }
                            abilityLandmark = null;
                            break;
                        }
                    }
                    if (abilityLandmark == null) {
                        break;
                    }
                }
            }
        }
        ServerPlayNetworking.send(player, new SyncAdvancementsCountPayload(count));
    }

    public void setScore(@NotNull ServerPlayerEntity player, @NotNull TrackedScoreType progressType, int progress) {
        if (progressType.isPercentage()) {
            progress = Math.max(0, Math.min(100, (int) ((progress * 100.0) / progressType.getFinalValue())));
        }
        Map<UUID, Integer> progressByPlayers = trackedScores.computeIfAbsent(progressType, k -> new HashMap<>());
        Integer currentProgress = progressByPlayers.get(player.getUuid());
        if (currentProgress == null || !currentProgress.equals(progress)) {
            progressByPlayers.put(player.getUuid(), progress);
            ServerPlayNetworking.send(player, new SyncScorePayload(progressType, progress));
        }
    }

    public void setStat(@NotNull ServerPlayerEntity player, @NotNull TrackedStatType statType, int progress) {
        if (statType.isPercentage()) {
            progress = Math.max(0, Math.min(100, (int) ((progress * 100.0) / statType.getFinalValue())));
        }
        Map<UUID, Integer> progressByPlayers = trackedStats.computeIfAbsent(statType, k -> new HashMap<>());
        Integer currentProgress = progressByPlayers.get(player.getUuid());
        if (currentProgress == null || !currentProgress.equals(progress)) {
            progressByPlayers.put(player.getUuid(), progress);
            ServerPlayNetworking.send(player, new SyncStatPayload(statType, progress));
        }
    }

    public boolean isNotReady() {
        return abilitiesConfiguration.isEmpty() ||
            currentAdvancementsMode == null ||
            currentScoreboardObjective == null ||
            currentScoreboardDisplaySlot == null;
    }

    public boolean isAbilityLocked(@NotNull ServerPlayerEntity player, AbilityType ability) {
        return isAbilityLocked(player, ability, false);
    }

    public boolean isAbilityLocked(@NotNull ServerPlayerEntity player, AbilityType ability, boolean checkOnly) {
        if (ability == null || player.isCreative() || player.isSpectator()) {
            return false;
        }
        if (isNotReady()) {
            AchieveToDoMod.logger.error("Advancements count hasn’t been loaded yet");
            return true;
        }
        int obtainedAdvancementsCount = getObtainedAdvancementsCount(player);
        int requiredAdvancementsCount = abilitiesConfiguration.get(ability);
        if (requiredAdvancementsCount == 0 ||
            requiredAdvancementsCount > 0 && obtainedAdvancementsCount >= requiredAdvancementsCount
        ) {
            return false;
        }
        if (!checkOnly) {
            Text lockedMessageText;
            if (requiredAdvancementsCount == -1) {
                lockedMessageText = ability.buildPermanentlyLockedMessage();
            } else {
                int leftAdvancementsCount = requiredAdvancementsCount - obtainedAdvancementsCount;
                lockedMessageText = ability.buildUnlockProgressMessage(leftAdvancementsCount);
            }
            player.sendMessage(lockedMessageText, true);
            demystifyAbility(player, ability);
        }
        return true;
    }

    public void addDungeon(
        @NotNull ServerWorld world,
        @NotNull ChunkPos chunkPos,
        LandmarkType type,
        BlockBox blockBox,
        boolean add
    ) {
        if (add) {
            Map<LandmarkType, List<BlockBox>> landmarks =
                landmarksByChunks.computeIfAbsent(chunkPos, k -> new HashMap<>());
            List<BlockBox> blockBoxes = landmarks.computeIfAbsent(type, k -> new ArrayList<>());
            blockBoxes.add(blockBox);
        } else {
            Map<LandmarkType, List<BlockBox>> landmarksMap = landmarksByChunks.get(chunkPos);
            if (landmarksMap != null) {
                List<BlockBox> boxes = landmarksMap.get(type);
                if (boxes != null) {
                    boxes.remove(blockBox);
                    if (boxes.isEmpty()) {
                        landmarksMap.remove(type);
                        if (landmarksMap.isEmpty()) {
                            landmarksByChunks.remove(chunkPos);
                        }
                    }
                }
            }
        }
        for (Map.Entry<UUID, List<LandmarkType>> entry : lockedLandmarkTypesByPlayers.entrySet()) {
            if (entry.getValue().contains(type)) {
                ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(entry.getKey());
                if (player != null) {
                    ServerPlayNetworking.send(player, new SyncLockedLandmarkBoxPayload(type, blockBox, add));
                }
            }
        }
    }

    @Override
    public void onInitializeServer() {
        ServerPlayNetworking.registerGlobalReceiver(DemystifyAbilityPayload.ID, (payload, context) ->
            context.player().server.execute(() -> demystifyAbility(context.player(), payload.ability()))
        );
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            if (server.getSaveProperties().getLevelInfo() instanceof LevelInfoExtension levelInfoExtension) {
                abilitiesConfiguration = levelInfoExtension.achievetodo$getAbilitiesConfiguration(
                    server.getSaveProperties().getGeneratorOptions().getSeed()
                );
            }
            advancementsCountByPlayers.clear();
            trackedScores.clear();
            trackedStats.clear();
            prepareScoreboard(server.getScoreboard());
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            ServerPlayNetworking.send(player, new SyncAbilitiesConfigurationPayload(abilitiesConfiguration));
            updateObtainedAdvancementsCount(server.getScoreboard(), player);
            ScoreHolder scoreHolder = ScoreHolder.fromName(player.getNameForScoreboard());
            Scoreboard scoreboard = player.getScoreboard();
            for (Map.Entry<String, List<TrackedScoreType>> scoreTypeEntry : TrackedScoreType.SCORES.entrySet()) {
                ReadableScoreboardScore scoreboardScore = scoreboard.getScore(
                    scoreHolder, scoreboard.getNullableObjective(scoreTypeEntry.getKey())
                );
                if (scoreboardScore != null) {
                    int score = scoreboardScore.getScore();
                    for (TrackedScoreType type : scoreTypeEntry.getValue()) {
                        setScore(player, type, type.fixScore(scoreboard, scoreHolder, score));
                    }
                }
            }
            ServerStatHandler serverStatHandler = player.getStatHandler();
            for (Map.Entry<Stat<?>, List<TrackedStatType>> statTypeEntry : TrackedStatType.STATS.entrySet()) {
                int statValue = serverStatHandler.getStat(statTypeEntry.getKey());
                for (TrackedStatType trackedStatType : statTypeEntry.getValue()) {
                    setStat(player, trackedStatType, statValue);
                }
            }
        });
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> onChunkLoadedOrUnloaded(world, chunk, true));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> onChunkLoadedOrUnloaded(world, chunk, false));
    }

    private void onChunkLoadedOrUnloaded(@NotNull ServerWorld world, @NotNull Chunk chunk, boolean loaded) {
        Registry<Structure> structureRegistry = null;
        for (Map.Entry<Structure, StructureStart> entry : chunk.getStructureStarts().entrySet()) {
            StructureStart structureStart = entry.getValue();
            if (structureStart.hasChildren()) {
                if (structureRegistry == null) {
                    structureRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
                }
                LandmarkType landmark = LandmarkType.findByStructure(
                    structureRegistry.getKey(entry.getKey()).orElse(null)
                );
                if (landmark != null) {
                    addDungeon(world, chunk.getPos(), landmark, structureStart.getBoundingBox(), loaded);
                }
            }
        }
        if (chunk instanceof ChunkExtension chunkExtension) {
            Map<Feature<?>, List<BlockBox>> featureBlockBoxes = chunkExtension.achievetodo$getFeatureBlockBoxes();
            if (featureBlockBoxes != null) {
                for (Map.Entry<Feature<?>, List<BlockBox>> entry : featureBlockBoxes.entrySet()) {
                    LandmarkType landmark = LandmarkType.findByFeature(entry.getKey());
                    if (landmark != null) {
                        for (BlockBox blockBox : entry.getValue()) {
                            addDungeon(world, chunk.getPos(), landmark, blockBox, loaded);
                        }
                    }
                }
            }
        }
    }

    private void updateObtainedAdvancementsCount(ServerScoreboard scoreboard, @NotNull ServerPlayerEntity player) {
        if (isNotReady()) {
            return;
        }
        int count = 0;
        String playerName = player.getNameForScoreboard();
        if (currentAdvancementsMode.isTeamsMode()) {
            Team team = scoreboard.getScoreHolderTeam(playerName);
            if (team == null) {
                AchieveToDoMod.logger.warn("Player [{}] is not a member of any team!", playerName);
                return;
            }
            for (String teamMemberName : team.getPlayerList()) {
                ReadableScoreboardScore teamMemberScore = scoreboard.getScore(
                    ScoreHolder.fromName(teamMemberName),
                    currentScoreboardObjective
                );
                if (teamMemberScore != null) {
                    count += teamMemberScore.getScore();
                }
            }
        } else {
            ReadableScoreboardScore playerScore = scoreboard.getScore(
                ScoreHolder.fromName(playerName),
                currentScoreboardObjective
            );
            if (playerScore != null) {
                count = playerScore.getScore();
            }
        }
        setObtainedAdvancementsCount(player, count);
    }

    private void demystifyAbility(@NotNull ServerPlayerEntity player, @NotNull AbilityType ability) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AbilityAdvancementsGenerator.buildAdvancementId(ability));
        player.getAdvancementTracker().grantCriterion(
            advancement,
            AbilityAdvancementsGenerator.DEMYSTIFIED_CRITERION_PREFIX + ability.getName()
        );
    }

    private void setAbilityLocked(@NotNull ServerPlayerEntity player, @NotNull AbilityType ability, boolean lock) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AbilityAdvancementsGenerator.buildAdvancementId(ability));
        PlayerAdvancementTracker advancementTracker = player.getAdvancementTracker();
        if (lock) {
            advancementTracker.revokeCriterion(advancement, AbilityAdvancementsGenerator.UNLOCKED_CRITERION);
        } else {
            for (String criterion : advancementTracker.getProgress(advancement).getUnobtainedCriteria()) {
                advancementTracker.grantCriterion(advancement, criterion);
            }
        }
    }

    private int getObtainedAdvancementsCount(@NotNull ServerPlayerEntity player) {
        return advancementsCountByPlayers.get(player.getUuid());
    }
}
