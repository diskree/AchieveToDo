package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.AdvancementProgressImpl;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementManagerMixin {

    @Inject(
        method = "initProgress",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/AdvancementProgress;init(Lnet/minecraft/advancement/AdvancementRequirements;)V",
            shift = At.Shift.AFTER
        )
    )
    public void setAdvancementId(
        AdvancementEntry advancement,
        AdvancementProgress progress,
        CallbackInfo ci
    ) {
        if (progress instanceof AdvancementProgressImpl advancementProgress) {
            advancementProgress.achievetodo$setAdvancementId(advancement.id());
        }
    }
}
