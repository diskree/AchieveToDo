package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.UsableItemOnBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PumpkinBlock.class)
public abstract class PumpkinBlockMixin implements UsableItemOnBlock {

    @Unique
    private boolean isCanUseChecking;

    @Override
    public boolean achievetodo$canUse(PlayerEntity player, ItemStack stack, Hand hand, BlockHitResult hit) {
        isCanUseChecking = true;
        boolean canUse = onUseWithItem(
            stack,
            player.getWorld().getBlockState(hit.getBlockPos()),
            player.getWorld(),
            hit.getBlockPos(),
            player,
            hand,
            hit
        ) == null;
        isCanUseChecking = false;
        return canUse;
    }

    @Shadow
    protected abstract ActionResult onUseWithItem(
        ItemStack stack,
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    );

    @Inject(
        method = "onUseWithItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    public void returnOnUse(
        ItemStack stack,
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(null);
        }
    }

    @ModifyReturnValue(
        method = "onUseWithItem",
        at = @At("RETURN")
    )
    public ActionResult skipClientCheck(ActionResult original) {
        if (isCanUseChecking) {
            return ActionResult.SUCCESS;
        }
        return original;
    }
}
