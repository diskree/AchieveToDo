package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquippableComponent.class)
public abstract class EquippableComponentMixin {

    @Inject(
        method = "equip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getWorld()Lnet/minecraft/world/World;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockEquip(
        @NotNull ItemStack stack,
        PlayerEntity player,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.findEquipmentEquipAbility(stack.getItem()))) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
