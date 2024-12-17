package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.injection.UsableBlock;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConsumableComponent.class)
public abstract class ConsumableComponentMixin implements UsableBlock {

    @Inject(
        method = "consume",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/ConsumableComponent;getConsumeTicks()I",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void blockFood(
        LivingEntity user,
        ItemStack stack,
        Hand hand,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (user instanceof PlayerEntity player &&
            AchieveToDo.isActionBlocked(player, BlockedActionType.findBlockedFood(stack))
        ) {
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
