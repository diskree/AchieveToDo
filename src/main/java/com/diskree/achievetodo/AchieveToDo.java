package com.diskree.achievetodo;

import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.networking.DemystifyAbilityPayload;
import com.diskree.achievetodo.networking.SyncAdvancementsCountPayload;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class AchieveToDo implements ModInitializer {

    public static Logger logger = LoggerFactory.getLogger(BuildConfig.MOD_NAME);

    public static AdvancementsMode currentAdvancementsMode;
    public static ScoreboardObjective currentScoreboardObjective;
    public static ScoreboardDisplaySlot currentScoreboardDisplaySlot;

    private static final Map<UUID, Integer> advancementsCountByPlayerUUID = new Object2IntOpenHashMap<>();

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
        if (count == 0) {
            return;
        }
        UUID playerUuid = player.getUuid();
        int oldCount = advancementsCountByPlayerUUID.getOrDefault(playerUuid, 0);
        advancementsCountByPlayerUUID.put(playerUuid, count);
        if (oldCount != 0) {
            for (AbilityType ability : AbilityType.values()) {
                if (count >= ability.getRequiredAdvancementsCount() &&
                    oldCount < ability.getRequiredAdvancementsCount()
                ) {
                    unlockAbility(player, ability);
                }
            }
        }
        ServerPlayNetworking.send(player, new SyncAdvancementsCountPayload(count));
    }

    public static boolean isAbilityLocked(@NotNull PlayerEntity player, AbilityType ability) {
        return isAbilityLocked(player, ability, false);
    }

    public static boolean isAbilityLocked(@NotNull PlayerEntity player, AbilityType ability, boolean checkOnly) {
        if (ability == null ||
            player.isCreative() ||
            player.isSpectator() ||
            getObtainedAdvancementsCount(player) >= ability.getRequiredAdvancementsCount()
        ) {
            return false;
        }
        if (checkOnly) {
            return true;
        }
        player.sendMessage(ability.getLockedMessage(getObtainedAdvancementsCount(player)), true);
        if (player.getWorld().isClient) {
            ClientPlayNetworking.send(new DemystifyAbilityPayload(ability));
        } else if (player instanceof ServerPlayerEntity serverPlayer) {
            demystifyAbility(serverPlayer, ability);
        }
        return true;
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(DemystifyAbilityPayload.ID, DemystifyAbilityPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DemystifyAbilityPayload.ID, (payload, context) ->
            context.player().server.execute(() -> demystifyAbility(context.player(), payload.ability()))
        );
        PayloadTypeRegistry.playS2C().register(SyncAdvancementsCountPayload.ID, SyncAdvancementsCountPayload.CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            advancementsCountByPlayerUUID.clear();
            prepareScoreboard(server.getScoreboard());
        });
        ServerPlayConnectionEvents.JOIN.register(
            (handler, sender, server) -> updateObtainedAdvancementsCount(server.getScoreboard(), handler.player)
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
            return AchieveToDoClient.obtainedAdvancementsCount;
        }
        if (player instanceof ServerPlayerEntity) {
            return advancementsCountByPlayerUUID.get(player.getUuid());
        }
        return 0;
    }
}
