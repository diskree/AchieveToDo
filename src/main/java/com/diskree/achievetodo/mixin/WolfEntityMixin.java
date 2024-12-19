package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin {

    @Inject(
        method = "interactMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V",
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
