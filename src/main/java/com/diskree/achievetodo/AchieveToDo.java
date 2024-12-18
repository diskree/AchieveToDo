package com.diskree.achievetodo;

import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.injection.UsableItem;
import com.diskree.achievetodo.networking.DemystifyAbilityPayload;
import com.diskree.achievetodo.networking.SyncAdvancementsCountPayload;
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

    private static final Map<UUID, Integer> advancementsCountByPlayerUUID = new HashMap<>();

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
            return AchieveToDoClient.obtainedAdvancementsCount;
        }
        if (player instanceof ServerPlayerEntity) {
            return advancementsCountByPlayerUUID.get(player.getUuid());
        }
        return 0;
    }

    private static void setScore(ServerPlayerEntity serverPlayer, int score) {
        UUID playerUuid = serverPlayer.getUuid();
        int oldScore = advancementsCountByPlayerUUID.getOrDefault(playerUuid, 0);
        advancementsCountByPlayerUUID.put(playerUuid, score);
        if (oldScore != 0) {
            for (AbilityType ability : AbilityType.values()) {
                if (score >= ability.getRequiredAdvancementsCount() &&
                    oldScore < ability.getRequiredAdvancementsCount()
                ) {
                    unlockAbility(serverPlayer, ability);
                }
            }
        }
        ServerPlayNetworking.send(serverPlayer, new SyncAdvancementsCountPayload(score));
    }

    public static boolean isAbilityLocked(PlayerEntity player, AbilityType ability) {
        return isAbilityLocked(player, ability, false);
    }

    public static boolean isAbilityLocked(PlayerEntity player, AbilityType ability, boolean checkOnly) {
        if (player == null ||
            ability == null ||
            player.isCreative() ||
            player.isSpectator() ||
            getScore(player) >= ability.getRequiredAdvancementsCount()
        ) {
            return false;
        }
        if (checkOnly) {
            return true;
        }
        player.sendMessage(ability.buildLockedDescription(player), true);
        if (player.getWorld().isClient) {
            ClientPlayNetworking.send(new DemystifyAbilityPayload(ability));
        } else if (player instanceof ServerPlayerEntity serverPlayer) {
            demystifyAbility(serverPlayer, ability);
        }
        return true;
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

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(DemystifyAbilityPayload.ID, DemystifyAbilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAdvancementsCountPayload.ID, SyncAdvancementsCountPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DemystifyAbilityPayload.ID, (payload, context) ->
            context.player().server.execute(() ->
                demystifyAbility(context.player(), payload.ability())
            )
        );
        ServerWorldEvents.LOAD.register((server, world) -> advancementsCountByPlayerUUID.clear());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> initScore(handler.player));

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (item == Items.SHEARS || item == Items.BRUSH) {
                return ActionResult.PASS;
            }
            if (isAbilityLocked(player, AbilityType.findItemUsageAbility(player, stack))) {
                return ActionResult.CONSUME;
            }
            if (isAbilityLocked(player, AbilityType.findEquipmentUsageAbility(item))) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if ((!player.shouldCancelInteraction() || player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty())) {
                return ActionResult.CONSUME;
            }
            if (item instanceof UsableItem usableItem &&
                usableItem.achievetodo$canUse(player, hit) &&
                isAbilityLocked(player, AbilityType.findToolUsageAbility(item))
            ) {
                return ActionResult.CONSUME;
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
                isAbilityLocked(player, AbilityType.USING_BOAT)
            ) {
                return ActionResult.CONSUME;
            }
            if (entity instanceof VillagerEntity villager &&
                !villager.isBaby() &&
                isAbilityLocked(player, AbilityType.findVillagerAbility(villager.getVillagerData().getProfession()))
            ) {
                villager.sayNo();
                return ActionResult.CONSUME;
            }
            if (item == Items.SHEARS &&
                entity instanceof Shearable shearable &&
                shearable.isShearable() &&
                isAbilityLocked(player, AbilityType.USING_SHEARS)
            ) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (isAbilityLocked(player, AbilityType.BREAK_BLOCKS)) {
                return ActionResult.CONSUME;
            }
            if (pos.getY() < 0 && isAbilityLocked(player, AbilityType.BREAK_BLOCKS_IN_NEGATIVE_Y)) {
                return ActionResult.CONSUME;
            }
            if (isAbilityLocked(player, AbilityType.findToolUsageAbility(item))) {
                return ActionResult.CONSUME;
            }
            if (item == Items.SHEARS && isAbilityLocked(player, AbilityType.USING_SHEARS)) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (isAbilityLocked(player, AbilityType.findToolUsageAbility(item))) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
    }
}
