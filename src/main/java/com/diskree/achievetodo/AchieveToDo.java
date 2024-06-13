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
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AchieveToDo implements ModInitializer {

    private static final Map<UUID, Integer> playersByScore = new HashMap<>();

    public static void initScore(ServerPlayerEntity serverPlayer) {
        int score = 0;
        for (Map.Entry<AdvancementEntry, AdvancementProgress> entry : serverPlayer.getAdvancementTracker().progress.entrySet()) {
            if (entry.getValue().isDone() && checkAdvancement(entry.getKey())) {
                score++;
            }
        }
        setScore(serverPlayer, score);
    }

    public static boolean checkAdvancement(AdvancementEntry advancementEntry) {
        Advancement advancement = advancementEntry.value();
        if (advancement.display().isEmpty()) {
            return false;
        }
        if (advancementEntry.id().getNamespace().equals(BuildConfig.MOD_ID)) {
            return false;
        }
        return true;
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
            return playersByScore.get(player.getUuid());
        }
        return 0;
    }

    private static void setScore(ServerPlayerEntity serverPlayer, int score) {
        UUID playerUuid = serverPlayer.getUuid();
        int oldScore = playersByScore.getOrDefault(playerUuid, 0);
        playersByScore.put(playerUuid, score);
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
        ServerWorldEvents.LOAD.register((server, world) -> playersByScore.clear());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> initScore(handler.player));

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (item == Items.SHEARS || item == Items.BRUSH) {
                return TypedActionResult.pass(ItemStack.EMPTY);
            }
            if (isActionBlocked(player, BlockedActionType.findBlockedItem(player, stack))) {
                return TypedActionResult.fail(ItemStack.EMPTY);
            }
            if (isActionBlocked(player, BlockedActionType.findBlockedEquipment(item))) {
                return TypedActionResult.fail(ItemStack.EMPTY);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
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
                return ActionResult.FAIL;
            }
            if (item instanceof UsableItem usableItem && (usableItem.achievetodo$canUse(player, hit)) ||
                block instanceof UsableItemOnBlock usableItemOnBlock &&
                    usableItemOnBlock.achievetodo$canUse(player, stack, hand, hit)) {
                if (isActionBlocked(player, BlockedActionType.findBlockedItem(player, stack))) {
                    return ActionResult.FAIL;
                }
                if (isActionBlocked(player, BlockedActionType.findBlockedTool(item))) {
                    return ActionResult.FAIL;
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
                return ActionResult.FAIL;
            }
            if (entity instanceof VillagerEntity villagerEntity &&
                !villagerEntity.isBaby() &&
                !villagerEntity.getOffers().isEmpty() &&
                isActionBlocked(player, BlockedActionType.findBlockedVillager(
                    villagerEntity.getVillagerData().getProfession()
                ))
            ) {
                villagerEntity.sayNo();
                return ActionResult.FAIL;
            }
            if (item == Items.SHEARS &&
                entity instanceof Shearable shearable &&
                shearable.isShearable() &&
                isActionBlocked(player, BlockedActionType.USING_SHEARS)
            ) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (player.getWorld().getRegistryKey() == World.OVERWORLD && isActionBlocked(player,
                pos.getY() >= 0 ? BlockedActionType.BREAK_BLOCKS_IN_POSITIVE_Y :
                    BlockedActionType.BREAK_BLOCKS_IN_NEGATIVE_Y)) {
                return ActionResult.FAIL;
            }
            if (isActionBlocked(player, BlockedActionType.findBlockedTool(item))) {
                return ActionResult.FAIL;
            }
            if (item == Items.SHEARS && isActionBlocked(player, BlockedActionType.USING_SHEARS)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (isActionBlocked(player, BlockedActionType.findBlockedTool(item))) {
                return ActionResult.FAIL;
            }
            if (item == Items.SHEARS && isActionBlocked(player, BlockedActionType.USING_SHEARS)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
