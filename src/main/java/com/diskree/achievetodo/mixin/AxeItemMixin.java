package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.UsableItem;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin implements UsableItem {

    @Unique
    private boolean isCanUseChecking;

    @Override
    public boolean achievetodo$canUse(PlayerEntity player, @NotNull BlockHitResult hit) {
        isCanUseChecking = true;
        boolean canUse = tryStrip(
            player.getWorld(),
            hit.getBlockPos(),
            player,
            player.getWorld().getBlockState(hit.getBlockPos())
        ).isPresent();
        isCanUseChecking = false;
        return canUse;
    }

    @Shadow
    protected abstract Optional<BlockState> tryStrip(
        World world,
        BlockPos pos,
        @Nullable PlayerEntity player,
        BlockState state
    );

    @Inject(
        method = "tryStrip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
            ordinal = 0,
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnStrip(
        World world,
        BlockPos pos,
        @Nullable PlayerEntity player,
        BlockState state,
        CallbackInfoReturnable<Optional<BlockState>> cir,
        @Local(ordinal = 0) Optional<BlockState> blockState
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(blockState);
        }
    }

    @Inject(
        method = "tryStrip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
            ordinal = 1,
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnDecreaseOxidationState(
        World world,
        BlockPos pos,
        @Nullable PlayerEntity player,
        BlockState state,
        CallbackInfoReturnable<Optional<BlockState>> cir,
        @Local(ordinal = 1) Optional<BlockState> blockState
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(blockState);
        }
    }

    @Inject(
        method = "tryStrip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
            ordinal = 2,
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void returnOnWaxOff(
        World world,
        BlockPos pos,
        @Nullable PlayerEntity player,
        BlockState state,
        CallbackInfoReturnable<Optional<BlockState>> cir,
        @Local(ordinal = 2) Optional<BlockState> blockState
    ) {
        if (isCanUseChecking) {
            cir.setReturnValue(blockState);
        }
    }
}
