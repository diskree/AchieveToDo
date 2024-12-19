package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemEntityMixin {

    @Inject(
        method = "interactMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/SnowGolemEntity;getWorld()Lnet/minecraft/world/World;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockShears(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.USING_SHEARS)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
