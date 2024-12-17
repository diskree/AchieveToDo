package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Unique
    private boolean isEndGatewayOnCentralIsland(@NotNull BlockPos pos) {
        return pos.getY() == 75
            && pos.getX() >= -96 && pos.getX() <= 96
            && pos.getZ() >= -96 && pos.getZ() <= 96;
    }

    @Inject(
        method = "tryUsePortal",
        at = @At("HEAD"),
        cancellable = true
    )
    private void blockPortal(Portal portal, BlockPos pos, CallbackInfo ci) {
        Entity teleportEntity = (Entity) (Object) this;
        if (teleportEntity instanceof EnderPearlEntity enderPearl) {
            teleportEntity = enderPearl.getOwner();
        }
        if (teleportEntity == null) {
            return;
        }

        BlockedActionType blockedActionType = BlockedActionType.findBlockedPortal(portal);
        RegistryKey<World> currentDimension = teleportEntity.getWorld().getRegistryKey();
        if (currentDimension == World.NETHER && blockedActionType == BlockedActionType.NETHER) {
            return;
        }
        if (currentDimension == World.END) {
            if (blockedActionType == BlockedActionType.END) {
                return;
            }
            if (blockedActionType == BlockedActionType.OUTER_ISLANDS && !isEndGatewayOnCentralIsland(pos)) {
                return;
            }
        }

        if (teleportEntity instanceof PlayerEntity playerEntity &&
            AchieveToDo.isActionBlocked(playerEntity, blockedActionType)
        ) {
            ci.cancel();
            return;
        }
        if (!teleportEntity.hasPassengers()) {
            return;
        }
        if (teleportEntity.getControllingPassenger() instanceof PlayerEntity controllingPlayer &&
            AchieveToDo.isActionBlocked(controllingPlayer, blockedActionType)
        ) {
            ci.cancel();
            return;
        }
        for (Entity passengerEntity : teleportEntity.getPassengerList()) {
            if (passengerEntity instanceof PlayerEntity passenger &&
                AchieveToDo.isActionBlocked(passenger, blockedActionType)
            ) {
                passenger.stopRiding();
            }
        }
    }
}
