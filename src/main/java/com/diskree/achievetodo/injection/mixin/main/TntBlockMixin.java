package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public class TntBlockMixin {

    @Inject(
        method = "onUseWithItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lockFlintAndSteel(
        @NotNull ItemStack stack,
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (AchieveToDoMod.isTargetInLockedLandmark(player, world, pos) ||
            stack.isOf(Items.FLINT_AND_STEEL) &&
                AchieveToDoMod.isAbilityLocked(player, AbilityType.USE_FLINT_AND_STEEL) ||
            AchieveToDoMod.isAbilityLocked(player, AbilityType.IGNITE_TNT)
        ) {
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }

    @WrapOperation(
        method = "onProjectileHit",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/ProjectileEntity;canModifyAt(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    public boolean lockIgniteTnt(
        ProjectileEntity projectileEntity,
        ServerWorld world,
        BlockPos blockPos,
        @NotNull Operation<Boolean> original,
        @Local Entity owner
    ) {
        if (!original.call(projectileEntity, world, blockPos)) {
            return false;
        }
        if (owner instanceof PlayerEntity player) {
            return !AchieveToDoMod.isTargetInLockedLandmark(player, world, blockPos) &&
                !AchieveToDoMod.isAbilityLocked(player, AbilityType.IGNITE_TNT);
        }
        return true;
    }
}
