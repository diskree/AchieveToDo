package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.injection.extension.main.MiningToolItemExtension;
import com.diskree.achievetodo.injection.extension.main.SwordItemExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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
    public void lockSleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (AchieveToDoMod.isTargetInLockedLandmark(player, player.getWorld(), pos) ||
            AchieveToDoMod.isAbilityLocked(player, AbilityType.SLEEP)
        ) {
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
    public boolean lockBlockBreak(
        boolean original,
        @Local(argsOnly = true) World world,
        @Local(argsOnly = true) @NotNull BlockPos pos
    ) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (AchieveToDoMod.isTargetInLockedLandmark(player, world, pos)) {
            return true;
        }
        if (AchieveToDoMod.isAbilityLocked(player, AbilityType.BREAK_BLOCKS, true)) {
            return AchieveToDoMod.isAbilityLocked(player, AbilityType.BREAK_BLOCKS);
        }
        if (pos.getY() < 0 && AchieveToDoMod.isAbilityLocked(player, AbilityType.BREAK_BLOCKS_IN_NEGATIVE_Y)) {
            return true;
        }
        if (AchieveToDoMod.isAbilityLocked(player, AbilityType.BREAK_BLOCKS)) {
            return true;
        }
        Item item = player.getMainHandStack().getItem();
        if (item instanceof SwordItemExtension swordItemExtension &&
            AchieveToDoMod.isAbilityLocked(
                player, AbilityType.findToolMaterialUsageAbility(swordItemExtension.achievetodo$getMaterial())
            )
        ) {
            return true;
        }
        if (item instanceof MiningToolItemExtension toolItem &&
            AchieveToDoMod.isAbilityLocked(
                player, AbilityType.findToolMaterialUsageAbility(toolItem.achievetodo$getMaterial())
            )
        ) {
            return true;
        }
        if (item instanceof ShearsItem && AchieveToDoMod.isAbilityLocked(player, AbilityType.USE_SHEARS)) {
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
    public void lockEntityAttack(@NotNull Entity target, CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (AchieveToDoMod.isTargetInLockedLandmark(player, target)) {
            info.cancel();
            return;
        }
        Item item = player.getMainHandStack().getItem();
        if (item == Items.TRIDENT && AchieveToDoMod.isAbilityLocked(player, AbilityType.ATTACK_WITH_TRIDENT)) {
            info.cancel();
            return;
        }
        if (item == Items.MACE && AchieveToDoMod.isAbilityLocked(player, AbilityType.ATTACK_WITH_MACE)) {
            info.cancel();
            return;
        }
        if (item instanceof SwordItemExtension swordItemExtension &&
            AchieveToDoMod.isAbilityLocked(
                player, AbilityType.findToolMaterialUsageAbility(swordItemExtension.achievetodo$getMaterial())
            )
        ) {
            info.cancel();
            return;
        }
        if (item instanceof MiningToolItemExtension miningToolItemExtension &&
            AchieveToDoMod.isAbilityLocked(
                player, AbilityType.findToolMaterialUsageAbility(miningToolItemExtension.achievetodo$getMaterial())
            )
        ) {
            info.cancel();
        }
    }

    @ModifyReturnValue(
        method = "canPlaceOn",
        at = @At(value = "RETURN", ordinal = 0)
    )
    public boolean lockDecorationItemPlace(
        boolean original,
        @Local(argsOnly = true) BlockPos blockPos,
        @Local(argsOnly = true) Direction facing,
        @Local(argsOnly = true) ItemStack stack
    ) {
        if (!original) {
            return false;
        }
        if (stack.getItem() instanceof DecorationItem) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (AchieveToDoMod.isTargetInLockedLandmark(player, player.getWorld(), blockPos)) {
                return false;
            }
        }
        return true;
    }
}
