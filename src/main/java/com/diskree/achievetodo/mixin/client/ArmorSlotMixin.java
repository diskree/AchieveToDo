package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ArmorSlot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorSlot.class)
public class ArmorSlotMixin {

    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(
        method = "canInsert",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void lockEquip(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity player &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findEquipmentEquipAbility(stack.getItem()))
        ) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(
        method = "canTakeItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z"
        )
    )
    private boolean lockEquip(
        ArmorSlot armorSlot,
        PlayerEntity player,
        @NotNull Operation<Boolean> original,
        @Local @NotNull ItemStack itemStack
    ) {
        return original.call(armorSlot, player) &&
            !AchieveToDo.isAbilityLocked(player, AbilityType.findEquipmentEquipAbility(itemStack.getItem()));
    }
}
