package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SnowballItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowballItem.class)
public class SnowballItemMixin {

    @Inject(
        method = "use",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void lockSnowball(
        World world,
        PlayerEntity player,
        Hand hand,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (AchieveToDoMod.isAbilityLocked(player, AbilityType.THROW_SNOWBALL)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
