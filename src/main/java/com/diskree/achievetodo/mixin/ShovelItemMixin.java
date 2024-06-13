package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.UsableItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin implements UsableItem {

    @Unique
    private boolean isCanUseChecking;

    @Override
    public boolean achievetodo$canUse(PlayerEntity player, BlockHitResult hit) {
        isCanUseChecking = true;
        boolean canUse = useOnBlock(new ItemUsageContext(player.getWorld(), player, null, null, hit)) == null;
        isCanUseChecking = false;
        return canUse;
    }

    @Shadow
    public abstract ActionResult useOnBlock(ItemUsageContext context);

    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnFlatten(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (isCanUseChecking) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;isClient()Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnCampfireExtinguish(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (isCanUseChecking) {
            cir.setReturnValue(null);
        }
    }
}
