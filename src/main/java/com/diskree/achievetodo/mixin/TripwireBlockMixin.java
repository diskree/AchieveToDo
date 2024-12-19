package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TripwireBlock.class)
public abstract class TripwireBlockMixin {

    @Inject(
        method = "onBreak",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockShears(
        World world,
        BlockPos blockPos,
        BlockState blockState,
        PlayerEntity player,
        CallbackInfoReturnable<BlockState> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.USING_SHEARS)) {
            cir.setReturnValue(blockState);
        }
    }
}
