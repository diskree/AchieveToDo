package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.injection.PlayerEntityImpl;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityImpl {

    @Unique
    private boolean isCanUseChecking;

    @Override
    public void achievetodo$setCanUseChecking(boolean isCanUseChecking) {
        this.isCanUseChecking = isCanUseChecking;
    }

    @Override
    public boolean achievetodo$isCanUseChecking() {
        return isCanUseChecking;
    }

    @Inject(
        method = "jump",
        at = @At("HEAD"),
        cancellable = true
    )
    public void blockJump(CallbackInfo ci) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (!playerEntity.isTouchingWater() && AchieveToDo.isActionBlocked(playerEntity, BlockedActionType.JUMP)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "canEquip",
        at = @At("HEAD"),
        cancellable = true
    )
    public void blockEquipment(@NotNull ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (AchieveToDo.isActionBlocked(player, BlockedActionType.findBlockedEquipment(stack.getItem()))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "trySleep",
        at = @At("HEAD"),
        cancellable = true
    )
    public void returnOnSleep(
        BlockPos blockPos,
        CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(Either.right(Unit.INSTANCE));
        }
    }
}
