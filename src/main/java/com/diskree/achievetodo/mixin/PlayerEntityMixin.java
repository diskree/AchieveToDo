package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
