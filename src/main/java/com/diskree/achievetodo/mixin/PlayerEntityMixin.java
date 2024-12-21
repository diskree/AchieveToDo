package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.injection.MiningToolItemImpl;
import com.diskree.achievetodo.injection.SwordItemImpl;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(
        method = "trySleep",
        at = @At("HEAD"),
        cancellable = true
    )
    public void lockSleep(
        BlockPos blockPos,
        CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir
    ) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (AchieveToDo.isAbilityLocked(player, AbilityType.SLEEP)) {
            cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM));
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    @ModifyReturnValue(
        method = "isBlockBreakingRestricted",
        at = @At(
            value = "RETURN",
            ordinal = 0
        )
    )
    public boolean lockBlockBreaking(
        boolean original,
        @Local(argsOnly = true) @NotNull BlockPos pos
    ) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (AchieveToDo.isAbilityLocked(player, AbilityType.BREAK_BLOCKS, true)) {
            return AchieveToDo.isAbilityLocked(player, AbilityType.BREAK_BLOCKS);
        }
        if (pos.getY() < 0 && AchieveToDo.isAbilityLocked(player, AbilityType.BREAK_BLOCKS_IN_NEGATIVE_Y)) {
            return true;
        }
        if (AchieveToDo.isAbilityLocked(player, AbilityType.BREAK_BLOCKS)) {
            return true;
        }
        Item item = player.getMainHandStack().getItem();
        if (item instanceof SwordItemImpl swordItem &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findToolUsageAbility(swordItem.achievetodo$getMaterial()))
        ) {
            return true;
        }
        if (item instanceof MiningToolItemImpl toolItem &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findToolUsageAbility(toolItem.achievetodo$getMaterial()))
        ) {
            return true;
        }
        if (item instanceof ShearsItem && AchieveToDo.isAbilityLocked(player, AbilityType.USING_SHEARS)) {
            return true;
        }
        return false;
    }

    @Inject(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;isUsingRiptide()Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockEntityAttack(Entity entity, CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Item item = player.getMainHandStack().getItem();
        if (item == Items.MACE && AchieveToDo.isAbilityLocked(player, AbilityType.USE_MACE)) {
            info.cancel();
        } else if (item instanceof SwordItemImpl swordItem &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findToolUsageAbility(swordItem.achievetodo$getMaterial()))
        ) {
            info.cancel();
        } else if (item instanceof MiningToolItemImpl toolItem &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findToolUsageAbility(toolItem.achievetodo$getMaterial()))
        ) {
            info.cancel();
        }
    }
}
