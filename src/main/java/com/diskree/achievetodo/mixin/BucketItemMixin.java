package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Inject(
        method = "placeFluid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/FluidFillable;tryFillWithFluid(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockFillFluid(
        PlayerEntity player,
        World world,
        BlockPos pos,
        BlockHitResult hitResult,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.USING_WATER_BUCKET)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "placeFluid",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/World;isClient:Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockEmptying(
        PlayerEntity player,
        World world,
        BlockPos pos,
        BlockHitResult hitResult,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.USING_WATER_BUCKET)) {
            cir.setReturnValue(false);
        }
    }
}
