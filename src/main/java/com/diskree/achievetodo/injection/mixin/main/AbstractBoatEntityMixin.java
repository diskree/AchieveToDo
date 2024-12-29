package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.AchieveToDoMod;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBoatEntity.class)
public class AbstractBoatEntityMixin {

    @ModifyReturnValue(
        method = "canAddPassenger",
        at = @At("TAIL")
    )
    private boolean lockBoat(boolean original, @Local(argsOnly = true) Entity passenger) {
        return original && (
            !(passenger instanceof PlayerEntity player) ||
                !AchieveToDoMod.isAbilityLocked(player, AbilityType.GET_INTO_BOAT)
        );
    }
}
