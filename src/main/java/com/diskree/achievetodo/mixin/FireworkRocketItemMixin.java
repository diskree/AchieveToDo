package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin {

    @Inject(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockFly(
        World world,
        PlayerEntity player,
        Hand hand,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.FLY)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
