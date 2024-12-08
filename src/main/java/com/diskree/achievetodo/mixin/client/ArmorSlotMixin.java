package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ArmorSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorSlot.class)
public abstract class ArmorSlotMixin {

    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(
        method = "canInsert",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void blockEquipment(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity player &&
            AchieveToDo.isActionBlocked(player, BlockedActionType.findBlockedEquipment(stack.getItem()))
        ) {
            cir.setReturnValue(false);
        }
    }
}
