package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlock.class)
public class AbstractFurnaceBlockMixin {

    @Inject(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/AbstractFurnaceBlock;openScreen(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V",
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
        AbstractFurnaceBlock abstractFurnaceBlock = (AbstractFurnaceBlock) (Object) this;
        AbilityType ability = null;
        if (abstractFurnaceBlock instanceof FurnaceBlock) {
            ability = AbilityType.OPEN_FURNACE;
        } else if (abstractFurnaceBlock instanceof SmokerBlock) {
            ability = AbilityType.OPEN_SMOKER;
        } else if (abstractFurnaceBlock instanceof BlastFurnaceBlock) {
            ability = AbilityType.OPEN_BLAST_FURNACE;
        }
        if (AchieveToDo.isAbilityLocked(player, ability)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
