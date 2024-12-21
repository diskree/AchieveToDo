package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.Builder.class)
public class BundleContentsComponentMixin {

    @Inject(
        method = "add(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/entity/player/PlayerEntity;)I",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void lockBundle(
        Slot slot,
        PlayerEntity player,
        CallbackInfoReturnable<Integer> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.PUT_IN_BUNDLE)) {
            cir.setReturnValue(0);
        }
    }
}
