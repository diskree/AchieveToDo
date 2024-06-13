package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.PlayerEntityImpl;
import com.diskree.achievetodo.injection.UsableBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin implements UsableBlock {

    @Unique
    private boolean isCanUseChecking;

    @Override
    public boolean achievetodo$canUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        isCanUseChecking = true;
        boolean canUse = onUse(
            player.getWorld().getBlockState(hit.getBlockPos()),
            player.getWorld(),
            hit.getBlockPos(),
            player,
            hit
        ) == null;
        isCanUseChecking = false;
        return canUse;
    }

    @Shadow
    protected abstract ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit
    );

    @Inject(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z",
            shift = At.Shift.BEFORE,
            ordinal = 0
        ),
        cancellable = true
    )
    public void returnOnExplosion(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BedBlock;wakeVillager(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnWakeVillager(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;trySleep(Lnet/minecraft/util/math/BlockPos;)Lcom/mojang/datafixers/util/Either;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnTrySleep(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (isCanUseChecking && player instanceof PlayerEntityImpl playerEntityImpl) {
            playerEntityImpl.achievetodo$setCanUseChecking(true);
            boolean canUse = player.trySleep(pos) == null;
            playerEntityImpl.achievetodo$setCanUseChecking(false);
            cir.setReturnValue(canUse ? null : ActionResult.SUCCESS);
        }
    }

    @Redirect(
        method = "onUse",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/World;isClient:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    public boolean skipClientCheck(World world) {
        if (isCanUseChecking) {
            return false;
        }
        return world.isClient;
    }
}
