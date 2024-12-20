package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDoClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementProgress.class)
public class AdvancementProgressMixin {

    @Unique
    private AbilityType ability;

    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    public void findAbility(AdvancementRequirements requirements, CallbackInfo ci) {
        ability = AbilityType.findByAdvancementRequirements(requirements);
    }

    @Inject(
        method = "countObtainedRequirements",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setObtainedAdvancementsCountForAbility(CallbackInfoReturnable<Integer> cir) {
        if (ability != null) {
            cir.setReturnValue(
                Math.min(AchieveToDoClient.obtainedAdvancementsCount, ability.getRequiredAdvancementsCount())
            );
        }
    }

    @WrapOperation(
        method = {
            "getProgressBarPercentage",
            "getProgressBarFraction"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/AdvancementRequirements;getLength()I"
        )
    )
    public int setRequiredAdvancementsCountForAbility(
        AdvancementRequirements requirements,
        Operation<Integer> original
    ) {
        return ability != null ? ability.getRequiredAdvancementsCount() : original.call(requirements);
    }
}
