package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(
        method = "grantCriterion",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;onStatusUpdate(Lnet/minecraft/advancement/AdvancementEntry;)V",
            shift = At.Shift.BEFORE,
            ordinal = 0
        )
    )
    public void onAdvancementGranted(
        AdvancementEntry advancementEntry,
        String criterionName,
        CallbackInfoReturnable<Boolean> cir
    ) {
        AchieveToDo.onAdvancementGranted(owner, advancementEntry);
    }

    @Inject(
        method = "revokeCriterion",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;onStatusUpdate(Lnet/minecraft/advancement/AdvancementEntry;)V",
            shift = At.Shift.BEFORE,
            ordinal = 0
        )
    )
    public void onAdvancementRevoked(
        AdvancementEntry advancementEntry,
        String criterionName,
        CallbackInfoReturnable<Boolean> cir
    ) {
        AchieveToDo.onAdvancementRevoked(owner, advancementEntry);
    }
}
