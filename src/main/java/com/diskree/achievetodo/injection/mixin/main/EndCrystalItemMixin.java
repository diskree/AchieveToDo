package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    @WrapOperation(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;isEmpty()Z"
        )
    )
    public boolean lockEndCrystal(
        List<Entity> entities,
        @NotNull Operation<Boolean> original,
        @Local(argsOnly = true) ItemUsageContext context
    ) {
        return original.call(entities) && (
            !(context.getPlayer() instanceof PlayerEntity player) ||
                !AchieveToDoMod.isAbilityLocked(player, AbilityType.PLACE_END_CRYSTAL)
        );
    }
}
