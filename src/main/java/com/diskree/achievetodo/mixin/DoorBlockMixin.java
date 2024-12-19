package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {

    @Inject(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;cycle(Lnet/minecraft/state/property/Property;)Ljava/lang/Object;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockUsage(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.OPEN_DOOR)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
