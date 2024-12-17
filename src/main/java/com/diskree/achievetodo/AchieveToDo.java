package com.diskree.achievetodo;

import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.blocked_actions.datagen.AdvancementsGenerator;
import com.diskree.achievetodo.injection.UsableBlock;
import com.diskree.achievetodo.injection.UsableItem;
import com.diskree.achievetodo.injection.UsableItemOnBlock;
import com.diskree.achievetodo.networking.DemystifyBlockedActionPayload;
import com.diskree.achievetodo.networking.ScoreSyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AchieveToDo implements ModInitializer {

    private static final Map<UUID, Integer> scoresByPlayers = new HashMap<>();

    public static void initScore(ServerPlayerEntity player) {
        int score = 0;
        for (Map.Entry<AdvancementEntry, AdvancementProgress> entry : player.getAdvancementTracker().progress.entrySet()) {
            if (entry.getValue().isDone() && checkAdvancement(entry.getKey())) {
                score++;
            }
        }
        setScore(player, score);
    }

    public static boolean checkAdvancement(AdvancementEntry advancementEntry) {
        Advancement advancement = advancementEntry.value();
        if (advancement.display().isEmpty()) {
            return false;
        }
        return !advancementEntry.id().getNamespace().equals(BuildConfig.MOD_ID);
    }

    public static void onAdvancementGranted(ServerPlayerEntity serverPlayer, AdvancementEntry advancementEntry) {
        if (checkAdvancement(advancementEntry)) {
            setScore(serverPlayer, getScore(serverPlayer) + 1);
        }
    }

    public static void onAdvancementRevoked(ServerPlayerEntity serverPlayer, AdvancementEntry advancementEntry) {
        if (checkAdvancement(advancementEntry)) {
            setScore(serverPlayer, getScore(serverPlayer) - 1);
        }
    }

    public static int getScore(PlayerEntity player) {
        if (player.getWorld().isClient && player instanceof ClientPlayerEntity) {
            return AchieveToDoClient.advancementsCount;
        }
        if (player instanceof ServerPlayerEntity) {
            return scoresByPlayers.get(player.getUuid());
        }
        return 0;
    }

    private static void setScore(ServerPlayerEntity serverPlayer, int score) {
        UUID playerUuid = serverPlayer.getUuid();
        int oldScore = scoresByPlayers.getOrDefault(playerUuid, 0);
        scoresByPlayers.put(playerUuid, score);
        if (oldScore != 0) {
            for (BlockedActionType blockedAction : BlockedActionType.values()) {
                if (score >= blockedAction.getUnblockAdvancementsCount() &&
                    oldScore < blockedAction.getUnblockAdvancementsCount()
                ) {
                    grantBlockedAction(serverPlayer, blockedAction, false);
                }
            }
        }
        ServerPlayNetworking.send(serverPlayer, new ScoreSyncPayload(score));
    }

    public static boolean isActionBlocked(PlayerEntity player, BlockedActionType blockedAction) {
        return isActionBlocked(player, blockedAction, false);
    }

    public static boolean isActionBlocked(PlayerEntity player, BlockedActionType blockedAction, boolean isCheckOnly) {
        if (blockedAction == null ||
            player == null ||
            player.isCreative() ||
            player.isSpectator() ||
            blockedAction.isUnblocked(player)) {
            return false;
        }
        if (isCheckOnly) {
            return true;
        }
        player.sendMessage(blockedAction.buildBlockedDescription(player), true);
        if (player.getWorld().isClient) {
            ClientPlayNetworking.send(new DemystifyBlockedActionPayload(blockedAction));
        } else if (player instanceof ServerPlayerEntity serverPlayer) {
            grantBlockedAction(serverPlayer, blockedAction, true);
        }
        return true;
    }

    private static void grantBlockedAction(
        @NotNull ServerPlayerEntity player,
        BlockedActionType blockedAction,
        boolean isDemystifyOnly
    ) {
        AdvancementEntry advancement = player.server.getAdvancementLoader()
            .get(AdvancementsGenerator.buildAdvancementId(blockedAction));
        if (isDemystifyOnly) {
            player.getAdvancementTracker().grantCriterion(
                advancement,
                AdvancementsGenerator.BLOCKED_ACTION_DEMYSTIFIED_CRITERION_PREFIX + blockedAction.getName()
            );
        } else {
            for (String criterion : player.getAdvancementTracker().getProgress(advancement).getUnobtainedCriteria()) {
                player.getAdvancementTracker().grantCriterion(advancement, criterion);
            }
        }
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(DemystifyBlockedActionPayload.ID, DemystifyBlockedActionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ScoreSyncPayload.ID, ScoreSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DemystifyBlockedActionPayload.ID, (payload, context) ->
            context.player().server.execute(() ->
                grantBlockedAction(context.player(), payload.blockedAction(), true)
            )
        );
        ServerWorldEvents.LOAD.register((server, world) -> scoresByPlayers.clear());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> initScore(handler.player));

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (item == Items.SHEARS || item == Items.BRUSH) {
                return ActionResult.PASS;
            }
            if (isActionBlocked(player, BlockedActionType.findBlockedItem(player, stack))) {
                return ActionResult.CONSUME;
            }
            if (isActionBlocked(player, BlockedActionType.findBlockedEquipment(item))) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            BlockState blockState = world.getBlockState(hit.getBlockPos());
            Block block = blockState.getBlock();
            if ((!player.shouldCancelInteraction() || player.getMainHandStack().isEmpty() &&
                player.getOffHandStack().isEmpty()) &&
                block instanceof UsableBlock usableBlock && usableBlock.achievetodo$canUse(player, hand, hit) &&
                isActionBlocked(player, BlockedActionType.findBlockedBlock(blockState))
            ) {
                return ActionResult.CONSUME;
            }
            if (item instanceof UsableItem usableItem && (usableItem.achievetodo$canUse(player, hit)) ||
                block instanceof UsableItemOnBlock usableItemOnBlock && usableItemOnBlock.achievetodo$canUse(player, stack, hand, hit)
            ) {
                if (isActionBlocked(player, BlockedActionType.findBlockedItem(player, stack))) {
                    return ActionResult.CONSUME;
                }
                if (isActionBlocked(player, BlockedActionType.findBlockedTool(item))) {
                    return ActionResult.CONSUME;
                }
            }
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (entity instanceof ItemFrameEntity) {
                return ActionResult.PASS;
            }
            if (entity instanceof BoatEntity boatEntity &&
                boatEntity.canAddPassenger(player) &&
                isActionBlocked(player, BlockedActionType.USING_BOAT)
            ) {
                return ActionResult.CONSUME;
            }
            if (entity instanceof VillagerEntity villagerEntity &&
                !villagerEntity.isBaby() &&
                isActionBlocked(
                    player,
                    BlockedActionType.findBlockedVillager(villagerEntity.getVillagerData().getProfession())
                )
            ) {
                villagerEntity.sayNo();
                return ActionResult.CONSUME;
            }
            if (item == Items.SHEARS &&
                entity instanceof Shearable shearable &&
                shearable.isShearable() &&
                isActionBlocked(player, BlockedActionType.USING_SHEARS)
            ) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (isActionBlocked(player, BlockedActionType.BREAK_BLOCKS)) {
                return ActionResult.CONSUME;
            }
            if (pos.getY() < 0 && isActionBlocked(player, BlockedActionType.BREAK_BLOCKS_IN_NEGATIVE_Y)) {
                return ActionResult.CONSUME;
            }
            if (isActionBlocked(player, BlockedActionType.findBlockedTool(item))) {
                return ActionResult.CONSUME;
            }
            if (item == Items.SHEARS && isActionBlocked(player, BlockedActionType.USING_SHEARS)) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (isActionBlocked(player, BlockedActionType.findBlockedTool(item))) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
    }
}
