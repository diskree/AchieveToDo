package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecartEntity.class)
public abstract class MinecartEntityMixin extends AbstractMinecartEntity {

    protected MinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        if (super.canAddPassenger(passenger)) {
            if (passenger instanceof PlayerEntity player) {
                return !AchieveToDoMod.isTargetInLockedLandmark(player, this) &&
                    !AchieveToDoMod.isAbilityLocked(player, AbilityType.GET_INTO_MINECART);
            }
            return true;
        }
        return false;
    }
}
