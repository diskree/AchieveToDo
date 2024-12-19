package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

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
        return AchieveToDo.isAbilityLocked(
            player,
            pos.getY() < 0 ? AbilityType.BREAK_BLOCKS_IN_NEGATIVE_Y : AbilityType.BREAK_BLOCKS
        );
    }
}
