package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(
        method = "canEquip",
        at = @At("HEAD"),
        cancellable = true
    )
    public void lockEquip(ItemStack stack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof PlayerEntity player &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findEquipmentEquipAbility(stack.getItem()))
        ) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "canEquipFromDispenser",
        at = @At("HEAD"),
        cancellable = true
    )
    public void lockEquip(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof PlayerEntity player &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findEquipmentEquipAbility(stack.getItem()))
        ) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "jump",
        at = @At("HEAD"),
        cancellable = true
    )
    public void lockJump(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof PlayerEntity player &&
            !player.isTouchingWater() &&
            AchieveToDo.isAbilityLocked(player, AbilityType.JUMP)
        ) {
            ci.cancel();
        }
    }
}
