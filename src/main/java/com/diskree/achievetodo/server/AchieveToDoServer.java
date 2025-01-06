package com.diskree.achievetodo.server;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.ability.DimensionalBlockBox;
import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.ability.generation.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.client.Utils;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import com.diskree.achievetodo.injection.extension.main.LevelInfoExtension;
import com.diskree.achievetodo.injection.extension.main.StructureStartExtension;
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
import net.minecraft.scoreboard.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AchieveToDoServer implements ServerModInitializer {

    private Map<AbilityType, Integer> abilitiesConfiguration = new Object2IntOpenHashMap<>();
    private final Map<UUID, Integer> advancementsCountByPlayers = new Object2IntOpenHashMap<>();

    private final Map<ChunkPos, Map<LandmarkType, List<DimensionalBlockBox>>> landmarksByChunks = new HashMap<>();
    private final Map<LandmarkType, List<UUID>> playersByLockedLandmarkTypes = new HashMap<>();

    private final EnumMap<TrackedScoreType, Map<UUID, Integer>> trackedScores =
        new EnumMap<>(TrackedScoreType.class);
    private final EnumMap<TrackedStatType, Map<UUID, Integer>> trackedStats =
        new EnumMap<>(TrackedStatType.class);

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
                "or enable advancements counter in the sidebar, tab list or below player names."
            );
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
        List<LandmarkType> unlockedLandmarks = null;
        Map<LandmarkType, List<DimensionalBlockBox>> lockedLandmarks = null;

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
            setAbilityLocked(player, ability, isLock);
            LandmarkType landmarkType = ability.getLandmarkType();
            if (landmarkType != null) {
                if (isLock) {
                    playersByLockedLandmarkTypes
                        .computeIfAbsent(landmarkType, k -> new ArrayList<>())
                        .add(playerUuid);
                    for (Map<LandmarkType, List<DimensionalBlockBox>> landmarks : landmarksByChunks.values()) {
                        List<DimensionalBlockBox> dimensionalBlockBoxes = landmarks.get(landmarkType);
                        if (dimensionalBlockBoxes != null) {
                            if (lockedLandmarks == null) {
                                lockedLandmarks = new HashMap<>();
                            }
                            lockedLandmarks.put(landmarkType, dimensionalBlockBoxes);
                        }
                    }
                } else {
                    List<UUID> players = playersByLockedLandmarkTypes.get(landmarkType);
                    if (players != null && players.remove(playerUuid) && players.isEmpty()) {
                        playersByLockedLandmarkTypes.remove(landmarkType);
                    }
                    if (unlockedLandmarks == null) {
                        unlockedLandmarks = new ArrayList<>();
                    }
                    unlockedLandmarks.add(landmarkType);
                }
            }
        }
        ServerPlayNetworking.send(player, new SyncAdvancementsCountPayload(count));
        if (unlockedLandmarks != null) {
            ServerPlayNetworking.send(player, new SyncLandmarkTypesUnlockedPayload(unlockedLandmarks));
        }
        if (lockedLandmarks != null) {
            ServerPlayNetworking.send(player, new SyncLockedLandmarksPayload(lockedLandmarks, true));
        }
    }

    public void setScore(@NotNull ServerPlayerEntity player, @NotNull TrackedScoreType progressType, int progress) {
        if (progressType.isPercentage()) {
            progress = Math.max(0, Math.min(100, (int) ((progress * 100.0) / progressType.getFinalValue())));
        }
        var progressByPlayers = trackedScores.computeIfAbsent(progressType, k -> new HashMap<>());
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
        var progressByPlayers = trackedStats.computeIfAbsent(statType, k -> new HashMap<>());
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
        int obtainedAdvancementsCount = advancementsCountByPlayers.get(player.getUuid());
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

    public boolean isInLockedLandmark(@NotNull ServerPlayerEntity player, DimensionType dimensionType, Box box) {
        BlockBox blockBox = Utils.toBlockBox(box);
        for (var entry : playersByLockedLandmarkTypes.entrySet()) {
            if (entry.getValue().contains(player.getUuid())) {
                LandmarkType landmarkType = entry.getKey();
                for (Map<LandmarkType, List<DimensionalBlockBox>> value : landmarksByChunks.values()) {
                    List<DimensionalBlockBox> dimensionalBlockBoxes = value.get(landmarkType);
                    if (dimensionalBlockBoxes != null) {
                        for (DimensionalBlockBox dimensionalBlockBox : dimensionalBlockBoxes) {
                            if (dimensionalBlockBox.dimensionType() == dimensionType &&
                                dimensionalBlockBox.blockBox().intersects(blockBox)
                            ) {
                                return isAbilityLocked(player, AbilityType.findByLandmarkType(landmarkType));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onLandmarksLoadedStatusChanged(
        @NotNull ServerWorld world,
        @NotNull ChunkPos chunkPos,
        Map<LandmarkType, List<DimensionalBlockBox>> landmarks,
        boolean isLoaded
    ) {
        boolean isChanged = false;
        if (isLoaded) {
            var chunkMap = landmarksByChunks.computeIfAbsent(chunkPos, k -> new HashMap<>());
            for (var entry : landmarks.entrySet()) {
                if (chunkMap
                    .computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .addAll(entry.getValue())
                ) {
                    isChanged = true;
                }
            }
        } else {
            Map<LandmarkType, List<DimensionalBlockBox>> chunkMap = landmarksByChunks.get(chunkPos);
            if (chunkMap != null) {
                for (var entry : landmarks.entrySet()) {
                    List<DimensionalBlockBox> dimensionalBlockBoxes = chunkMap.get(entry.getKey());
                    if (dimensionalBlockBoxes != null && dimensionalBlockBoxes.removeAll(entry.getValue())) {
                        isChanged = true;
                        if (dimensionalBlockBoxes.isEmpty()) {
                            chunkMap.remove(entry.getKey());
                            if (chunkMap.isEmpty()) {
                                landmarksByChunks.remove(chunkPos);
                            }
                        }
                    }
                }
            }
        }
        if (!isChanged) {
            return;
        }
        HashMap<UUID, List<LandmarkType>> landmarkTypesByPlayers = new HashMap<>();
        for (LandmarkType type : landmarks.keySet()) {
            List<UUID> playerUuids = playersByLockedLandmarkTypes.get(type);
            if (playerUuids == null) {
                continue;
            }
            for (UUID playerUuid : playerUuids) {
                landmarkTypesByPlayers
                    .computeIfAbsent(playerUuid, k -> new ArrayList<>())
                    .add(type);
            }
        }
        PlayerManager playerManager = world.getServer().getPlayerManager();
        for (UUID playerUuid : landmarkTypesByPlayers.keySet()) {
            ServerPlayerEntity player = playerManager.getPlayer(playerUuid);
            if (player == null) {
                continue;
            }
            List<LandmarkType> landmarkTypes = landmarkTypesByPlayers.get(playerUuid);
            HashMap<LandmarkType, List<DimensionalBlockBox>> landmarksToSync = new HashMap<>();
            for (LandmarkType landmarkType : landmarkTypes) {
                landmarksToSync
                    .computeIfAbsent(landmarkType, k -> new ArrayList<>())
                    .addAll(landmarks.get(landmarkType));
            }
            ServerPlayNetworking.send(player, new SyncLockedLandmarksPayload(landmarksToSync, isLoaded));
        }
    }

    public void onLandmarkResized(
        @NotNull ServerWorld world,
        @NotNull ChunkPos chunkPos,
        LandmarkType landmarkType,
        DimensionalBlockBox oldDimensionalBlockBox,
        DimensionalBlockBox newDimensionalBlockBox
    ) {
        Map<LandmarkType, List<DimensionalBlockBox>> chunkMap = landmarksByChunks.get(chunkPos);
        if (chunkMap == null) {
            return;
        }
        List<DimensionalBlockBox> dimensionalBlockBoxes = chunkMap.get(landmarkType);
        if (dimensionalBlockBoxes == null) {
            return;
        }
        if (!dimensionalBlockBoxes.remove(oldDimensionalBlockBox)) {
            return;
        }
        dimensionalBlockBoxes.add(newDimensionalBlockBox);
        List<UUID> playerUuids = playersByLockedLandmarkTypes.get(landmarkType);
        if (playerUuids == null) {
            return;
        }
        PlayerManager playerManager = world.getServer().getPlayerManager();
        for (UUID playerUuid : playerUuids) {
            ServerPlayerEntity player = playerManager.getPlayer(playerUuid);
            if (player == null) {
                continue;
            }
            ServerPlayNetworking.send(player, new SyncResizedLandmarkPayload(
                landmarkType,
                oldDimensionalBlockBox,
                newDimensionalBlockBox
            ));
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
            for (var entry : TrackedScoreType.SCORES.entrySet()) {
                ReadableScoreboardScore scoreboardScore = scoreboard.getScore(
                    scoreHolder, scoreboard.getNullableObjective(entry.getKey())
                );
                if (scoreboardScore != null) {
                    int score = scoreboardScore.getScore();
                    for (TrackedScoreType type : entry.getValue()) {
                        setScore(player, type, type.fixScore(scoreboard, scoreHolder, score));
                    }
                }
            }
            ServerStatHandler serverStatHandler = player.getStatHandler();
            for (var entry : TrackedStatType.STATS.entrySet()) {
                int statValue = serverStatHandler.getStat(entry.getKey());
                for (TrackedStatType trackedStatType : entry.getValue()) {
                    setStat(player, trackedStatType, statValue);
                }
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID playerUuid = handler.player.getUuid();
            advancementsCountByPlayers.remove(playerUuid);
            for (Map<UUID, Integer> players : trackedScores.values()) {
                players.remove(playerUuid);
            }
            for (Map<UUID, Integer> players : trackedStats.values()) {
                players.remove(playerUuid);
            }
            for (List<UUID> players : playersByLockedLandmarkTypes.values()) {
                players.remove(playerUuid);
            }
        });
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> onChunkLoadedStatusChanged(world, chunk, true));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> onChunkLoadedStatusChanged(world, chunk, false));
    }

    private void onChunkLoadedStatusChanged(@NotNull ServerWorld world, @NotNull Chunk chunk, boolean isLoaded) {
        DimensionType dimensionType = DimensionType.findByWorld(world.getRegistryKey());
        if (dimensionType == null) {
            return;
        }
        Map<LandmarkType, List<DimensionalBlockBox>> landmarks = null;
        for (StructureStart structureStart : chunk.getStructureStarts().values()) {
            if (structureStart instanceof StructureStartExtension structureStartExtension) {
                LandmarkType landmarkType = structureStartExtension.achievetodo$getLandmarkType();
                BlockBox landmarkBlockBox = structureStartExtension.achievetodo$getLandmarkBlockBox();
                if (landmarkType != null && landmarkBlockBox != null) {
                    if (landmarks == null) {
                        landmarks = new HashMap<>();
                    }
                    landmarks
                        .computeIfAbsent(landmarkType, k -> new ArrayList<>())
                        .add(new DimensionalBlockBox(dimensionType, landmarkBlockBox));
                }
            }
        }
        if (chunk instanceof ChunkExtension chunkExtension) {
            Map<LandmarkType, List<DimensionalBlockBox>> featureLandmarks =
                chunkExtension.achievetodo$getFeatureLandmarks();
            if (featureLandmarks != null) {
                if (landmarks == null) {
                    landmarks = new HashMap<>();
                }
                landmarks.putAll(featureLandmarks);
            }
        }
        if (landmarks != null) {
            onLandmarksLoadedStatusChanged(world, chunk.getPos(), landmarks, isLoaded);
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

    private void setAbilityLocked(@NotNull ServerPlayerEntity player, @NotNull AbilityType ability, boolean isLocked) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AbilityAdvancementsGenerator.buildAdvancementId(ability));
        PlayerAdvancementTracker advancementTracker = player.getAdvancementTracker();
        if (isLocked) {
            advancementTracker.revokeCriterion(advancement, AbilityAdvancementsGenerator.UNLOCKED_CRITERION);
        } else {
            for (String criterion : advancementTracker.getProgress(advancement).getUnobtainedCriteria()) {
                advancementTracker.grantCriterion(advancement, criterion);
            }
        }
    }
}
