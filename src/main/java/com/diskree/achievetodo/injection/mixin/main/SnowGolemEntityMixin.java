package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowGolemEntity.class)
public class SnowGolemEntityMixin {

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
        if (AchieveToDoMod.isAbilityLocked(player, AbilityType.USE_SHEARS)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
