package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @WrapOperation(
        method = "onClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/slot/Slot;canTakePartial(Lnet/minecraft/entity/player/PlayerEntity;)Z",
            ordinal = 0
        )
    )
    public boolean lockBundle(
        Slot instance,
        PlayerEntity player,
        @NotNull Operation<Boolean> original
    ) {
        return original.call(instance, player) && !AchieveToDo.isAbilityLocked(player, AbilityType.PUT_IN_BUNDLE);
    }
}
