package com.diskree.achievetodo.server;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.DungeonType;
import com.diskree.achievetodo.ability.generation.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.client.AchieveToDoClient;
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
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AchieveToDoServer implements ServerModInitializer {

    private Map<AbilityType, Integer> abilitiesConfiguration = new HashMap<>();
    private final Map<UUID, Integer> advancementsCounts = new Object2IntOpenHashMap<>();
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
        UUID playerUuid = player.getUuid();
        int oldCount = advancementsCounts.getOrDefault(playerUuid, 0);
        advancementsCounts.put(playerUuid, count);
        if (oldCount != 0 && abilitiesConfiguration != null) {
            for (AbilityType ability : AbilityType.values()) {
                int requiredAdvancementsCount = abilitiesConfiguration.get(ability);
                if (requiredAdvancementsCount > 0 &&
                    count >= requiredAdvancementsCount &&
                    oldCount < requiredAdvancementsCount
                ) {
                    unlockAbility(player, ability);
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
            player.sendMessage(
                AchieveToDoClient.translateModKey("error.not_ready_yet")
                    .formatted(Formatting.RED),
                true
            );
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

    public void addDungeon(@NotNull ServerWorld world, DungeonType dungeon, BlockBox blockBox) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, new SyncDungeonBoundingBoxPayload(dungeon, blockBox));
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
            advancementsCounts.clear();
            trackedScores.clear();
            trackedStats.clear();
            prepareScoreboard(server.getScoreboard());
        });
        ServerPlayConnectionEvents.JOIN.register(
            (handler, sender, server) -> {
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
            }
        );
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            Registry<Structure> structureRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
            for (Map.Entry<Structure, StructureStart> entry : chunk.getStructureStarts().entrySet()) {
                StructureStart structureStart = entry.getValue();
                if (structureStart.hasChildren()) {
                    RegistryKey<Structure> structure = structureRegistry.getKey(entry.getKey()).orElse(null);
                    DungeonType dungeon = DungeonType.findByStructure(structure);
                    if (dungeon != null) {
                        addDungeon(world, dungeon, structureStart.getBoundingBox());
                    }
                }
            }
        });
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

    private void unlockAbility(@NotNull ServerPlayerEntity player, @NotNull AbilityType ability) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AbilityAdvancementsGenerator.buildAdvancementId(ability));
        for (String criterion : player.getAdvancementTracker().getProgress(advancement).getUnobtainedCriteria()) {
            player.getAdvancementTracker().grantCriterion(advancement, criterion);
        }
    }

    private int getObtainedAdvancementsCount(@NotNull ServerPlayerEntity player) {
        return advancementsCounts.get(player.getUuid());
    }
}
