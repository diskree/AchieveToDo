package com.diskree.achievetodo;

import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.injection.LevelInfoImpl;
import com.diskree.achievetodo.networking.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AchieveToDo implements ModInitializer {

    public static Logger logger = LoggerFactory.getLogger(BuildConfig.MOD_NAME);

    private static Map<AbilityType, Integer> abilitiesConfiguration = new HashMap<>();
    private static final Map<UUID, Integer> advancementsCounts = new Object2IntOpenHashMap<>();
    private static final EnumMap<TrackedScoreType, Map<UUID, Integer>> trackedScores =
        new EnumMap<>(TrackedScoreType.class);
    private static final EnumMap<TrackedStatType, Map<UUID, Integer>> trackedStats =
        new EnumMap<>(TrackedStatType.class);

    public static AdvancementsMode currentAdvancementsMode;
    public static ScoreboardObjective currentScoreboardObjective;
    public static ScoreboardDisplaySlot currentScoreboardDisplaySlot;

    public static void prepareScoreboard(ServerScoreboard scoreboard) {
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
            logger.error("Can't find advancements counter in scoreboard! " +
                "Please check that BACAP datapack is installed " +
                "or enable advancements counter in the sidebar, tab list or below player names.");
            return;
        }
        if (currentAdvancementsMode != oldAdvancementsMode ||
            currentScoreboardObjective != oldScoreboardObjective ||
            currentScoreboardDisplaySlot != oldScoreboardDisplaySlot
        ) {
            for (ServerPlayerEntity serverPlayerEntity : scoreboard.server.getPlayerManager().getPlayerList()) {
                updateObtainedAdvancementsCount(scoreboard, serverPlayerEntity);
            }
        }
    }

    public static void setObtainedAdvancementsCount(@NotNull ServerPlayerEntity player, int count) {
        UUID playerUuid = player.getUuid();
        int oldCount = advancementsCounts.getOrDefault(playerUuid, 0);
        advancementsCounts.put(playerUuid, count);
        if (oldCount != 0) {
            for (AbilityType ability : AbilityType.values()) {
                int requiredAdvancementsCount = abilitiesConfiguration.get(ability);
                if (count >= requiredAdvancementsCount && oldCount < requiredAdvancementsCount) {
                    unlockAbility(player, ability);
                }
            }
        }
        ServerPlayNetworking.send(player, new SyncAdvancementsCountPayload(count));
    }

    public static void setScore(
        @NotNull ServerPlayerEntity player,
        @NotNull TrackedScoreType progressType,
        int progress
    ) {
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

    public static void setStat(
        @NotNull ServerPlayerEntity player,
        @NotNull TrackedStatType statType,
        int progress
    ) {
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

    public static boolean isAbilityLocked(@NotNull PlayerEntity player, AbilityType ability) {
        return isAbilityLocked(player, ability, false);
    }

    public static boolean isAbilityLocked(@NotNull PlayerEntity player, AbilityType ability, boolean checkOnly) {
        if (ability == null || player.isCreative() || player.isSpectator()) {
            return false;
        }
        if (AchieveToDoClient.isNotReady()) {
            if (ability != AbilityType.VISION) {
                player.sendMessage(
                    Text.translatable("achievetodo.error.not_ready_yet")
                        .formatted(Formatting.RED),
                    true
                );
            }
            return true;
        }
        int requiredAdvancementsCount = abilitiesConfiguration.get(ability);
        if (getObtainedAdvancementsCount(player) >= requiredAdvancementsCount) {
            return false;
        }
        if (checkOnly) {
            return true;
        }
        player.sendMessage(
            ability.getLockedMessage(requiredAdvancementsCount - getObtainedAdvancementsCount(player)),
            true
        );
        if (player.getWorld().isClient) {
            ClientPlayNetworking.send(new DemystifyAbilityPayload(ability));
        } else if (player instanceof ServerPlayerEntity serverPlayer) {
            demystifyAbility(serverPlayer, ability);
        }
        return true;
    }

    public static @Nullable List<List<AbilityType>> buildAbilitiesTree() {
        if (abilitiesConfiguration.isEmpty()) {
            return null;
        }
        Map<AbilitiesBranchType, List<AbilityType>> abilitiesByBranches = Arrays.stream(AbilityType.values())
            .collect(Collectors.groupingBy(AbilityType::getBranchType));
        List<List<AbilityType>> tree = new ArrayList<>();
        for (AbilitiesBranchType category : AbilitiesBranchType.values()) {
            List<AbilityType> abilities = abilitiesByBranches.getOrDefault(category, Collections.emptyList()).stream()
                .sorted(Comparator.comparingInt((AbilityType ability) -> {
                        int requiredAdvancementsCount = abilitiesConfiguration.getOrDefault(ability, 0);
                        if (requiredAdvancementsCount == 0) {
                            return 0;
                        }
                        if (requiredAdvancementsCount > 0) {
                            return 1;
                        }
                        return 2;
                    })
                    .thenComparingInt(abilitiesConfiguration::get)
                    .thenComparing(Enum::ordinal))
                .toList();
            if (category == AbilitiesBranchType.MAIN) {
                tree.add(abilities);
            } else {
                int rowsCount = category.getRowsCount();
                if (abilities.size() % rowsCount != 0) {
                    throw new IllegalStateException("Abilities in category " + category +
                        " cannot be evenly distributed across " + rowsCount + " rows."
                    );
                }
                int rowSize = abilities.size() / rowsCount;
                List<List<AbilityType>> branches = new ArrayList<>(rowsCount);
                for (int i = 0; i < rowsCount; i++) {
                    branches.add(new ArrayList<>(rowSize));
                }
                for (int i = 0; i < abilities.size(); i++) {
                    int branchIndex = i / rowSize;
                    branches.get(branchIndex).add(abilities.get(i));
                }
                int half = rowsCount / 2;
                tree.addAll(0, branches.subList(0, half));
                tree.addAll(branches.subList(half, rowsCount));
            }
        }
        System.out.println(tree);
        return tree;
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(DemystifyAbilityPayload.ID, DemystifyAbilityPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DemystifyAbilityPayload.ID, (payload, context) ->
            context.player().server.execute(() -> demystifyAbility(context.player(), payload.ability()))
        );
        PayloadTypeRegistry.playS2C().register(
            SyncAbilitiesConfigurationPayload.ID, SyncAbilitiesConfigurationPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(SyncAdvancementsCountPayload.ID, SyncAdvancementsCountPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncScorePayload.ID, SyncScorePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncStatPayload.ID, SyncStatPayload.CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (server.getSaveProperties().getLevelInfo() instanceof LevelInfoImpl levelInfoImpl) {
                abilitiesConfiguration = levelInfoImpl.achievetodo$getAbilitiesConfiguration();
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
    }

    private static void updateObtainedAdvancementsCount(
        ServerScoreboard scoreboard,
        @NotNull ServerPlayerEntity player
    ) {
        if (currentAdvancementsMode == null) {
            return;
        }
        int count = 0;
        String playerName = player.getNameForScoreboard();
        if (currentAdvancementsMode.isTeamsMode()) {
            Team team = scoreboard.getScoreHolderTeam(playerName);
            if (team == null) {
                logger.warn("Player [{}] is not a member of any team!", playerName);
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

    private static void demystifyAbility(@NotNull ServerPlayerEntity player, @NotNull AbilityType ability) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AbilityAdvancementsGenerator.buildAdvancementId(ability));
        player.getAdvancementTracker().grantCriterion(
            advancement,
            AbilityAdvancementsGenerator.DEMYSTIFIED_CRITERION_PREFIX + ability.getLowerCaseName()
        );
    }

    private static void unlockAbility(@NotNull ServerPlayerEntity player, @NotNull AbilityType ability) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AbilityAdvancementsGenerator.buildAdvancementId(ability));
        for (String criterion : player.getAdvancementTracker().getProgress(advancement).getUnobtainedCriteria()) {
            player.getAdvancementTracker().grantCriterion(advancement, criterion);
        }
    }

    private static int getObtainedAdvancementsCount(@NotNull PlayerEntity player) {
        if (player.getWorld().isClient && player instanceof ClientPlayerEntity) {
            return AchieveToDoClient.getObtainedAdvancementsCount();
        }
        if (player instanceof ServerPlayerEntity) {
            return advancementsCounts.get(player.getUuid());
        }
        return 0;
    }
}
