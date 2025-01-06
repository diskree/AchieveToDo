package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemUsageContext;getStack()Lnet/minecraft/item/ItemStack;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockShears(
        ItemUsageContext context,
        CallbackInfoReturnable<ActionResult> cir,
        @Local PlayerEntity player,
        @Local World world,
        @Local BlockPos pos
    ) {
        if (player != null) {
            if (AchieveToDoMod.isTargetInLockedLandmark(player, world, pos) ||
                AchieveToDoMod.isAbilityLocked(player, AbilityType.USE_SHEARS)
            ) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
