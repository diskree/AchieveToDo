package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConsumableComponent.class)
public class ConsumableComponentMixin {

    @Inject(
        method = "consume",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/ConsumableComponent;getConsumeTicks()I",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockFood(
        LivingEntity user,
        ItemStack stack,
        Hand hand,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (user instanceof PlayerEntity player &&
            AchieveToDo.isAbilityLocked(player, AbilityType.findEatFoodAbility(stack))
        ) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(
        method = "canConsume",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void lockOminousBottle(
        LivingEntity user,
        ItemStack stack,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (user instanceof PlayerEntity player &&
            stack.isOf(Items.OMINOUS_BOTTLE) &&
            AchieveToDo.isAbilityLocked(player, AbilityType.DRINK_OMINOUS_BOTTLE)
        ) {
            cir.setReturnValue(false);
        }
    }
}
