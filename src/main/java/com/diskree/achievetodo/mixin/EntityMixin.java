package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
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
        return pos.getY() == 75 &&
            pos.getX() >= -96 && pos.getX() <= 96 &&
            pos.getZ() >= -96 && pos.getZ() <= 96;
    }

    @Inject(
        method = "setSneaking",
        at = @At("HEAD"),
        cancellable = true
    )
    public void lockSneaking(boolean sneaking, CallbackInfo ci) {
        if (sneaking) {
            Entity entity = (Entity) (Object) this;
            if (entity instanceof PlayerEntity player && AchieveToDo.isAbilityLocked(player, AbilityType.SNEAK)) {
                ci.cancel();
            }
        }
    }

    @Inject(
        method = "tryUsePortal",
        at = @At("HEAD"),
        cancellable = true
    )
    private void lockPortal(Portal portal, BlockPos pos, CallbackInfo ci) {
        Entity teleportEntity = (Entity) (Object) this;
        EnderPearlEntity enderPearl = null;
        if (teleportEntity instanceof EnderPearlEntity enderPearlEntity) {
            teleportEntity = enderPearlEntity.getOwner();
            enderPearl = enderPearlEntity;
        }
        if (teleportEntity == null) {
            return;
        }

        AbilityType ability = AbilityType.findPortalTeleportAbility(portal);
        RegistryKey<World> currentDimension = teleportEntity.getWorld().getRegistryKey();
        if (currentDimension == World.NETHER && ability == AbilityType.ENTER_NETHER) {
            return;
        }
        if (currentDimension == World.END) {
            if (ability == AbilityType.ENTER_END) {
                return;
            }
            if (ability == AbilityType.TELEPORT_OUTER_ISLANDS && !isEndGatewayOnCentralIsland(pos)) {
                return;
            }
        }

        if (teleportEntity instanceof PlayerEntity playerEntity &&
            AchieveToDo.isAbilityLocked(playerEntity, ability)
        ) {
            if (enderPearl != null) {
                enderPearl.remove(Entity.RemovalReason.DISCARDED);
            }
            ci.cancel();
            return;
        }
        if (enderPearl != null || !teleportEntity.hasPassengers()) {
            return;
        }
        if (teleportEntity.getControllingPassenger() instanceof PlayerEntity controllingPlayer &&
            AchieveToDo.isAbilityLocked(controllingPlayer, ability)
        ) {
            ci.cancel();
            return;
        }
        for (Entity passengerEntity : teleportEntity.getPassengerList()) {
            if (passengerEntity instanceof PlayerEntity passenger &&
                AchieveToDo.isAbilityLocked(passenger, ability)
            ) {
                passenger.stopRiding();
            }
        }
    }
}
