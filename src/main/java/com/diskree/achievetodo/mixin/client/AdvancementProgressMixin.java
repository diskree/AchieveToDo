package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDoClient;
import com.diskree.achievetodo.DynamicProgressType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementProgress.class)
public abstract class AdvancementProgressMixin {

    @Unique
    private AbilityType ability;

    @Unique
    private DynamicProgressType dynamicProgressType;

    @Shadow
    public abstract boolean isDone();

    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    public void findAbility(AdvancementRequirements requirements, CallbackInfo ci) {
        dynamicProgressType = DynamicProgressType.findByAdvancementRequirements(requirements);
        if (dynamicProgressType == null) {
            ability = AbilityType.findByAdvancementRequirements(requirements);
        }
    }

    @Inject(
        method = "countObtainedRequirements",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setCustomObtainedRequirementsCount(CallbackInfoReturnable<Integer> cir) {
        if (dynamicProgressType != null) {
            if (isDone()) {
                cir.setReturnValue(dynamicProgressType.getFinalValue());
            } else {
                cir.setReturnValue(AchieveToDoClient.getDynamicProgress(dynamicProgressType));
            }
        } else if (ability != null) {
            cir.setReturnValue(
                Math.min(AchieveToDoClient.getObtainedAdvancementsCount(), ability.getRequiredAdvancementsCount())
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
    public int setCustomRequiredAdvancementsCount(
        AdvancementRequirements requirements,
        Operation<Integer> original
    ) {
        if (dynamicProgressType != null) {
            return dynamicProgressType.getFinalValue();
        }
        return ability != null ? ability.getRequiredAdvancementsCount() : original.call(requirements);
    }

    @Inject(
        method = "getProgressBarPercentage",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setDynamicProgressPercentage(CallbackInfoReturnable<Float> cir) {
        if (dynamicProgressType != null && dynamicProgressType.isPercentage()) {
            cir.setReturnValue(isDone() ? 100f : AchieveToDoClient.getDynamicProgress(dynamicProgressType) / 100f);
        }
    }

    @Inject(
        method = "getProgressBarFraction",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setDynamicProgressPercentageText(CallbackInfoReturnable<Text> cir) {
        if (dynamicProgressType != null && dynamicProgressType.isPercentage()) {
            cir.setReturnValue(Text.translatable(
                "mco.upload.percent",
                isDone() ? 100 : AchieveToDoClient.getDynamicProgress(dynamicProgressType)
            ));
        }
    }
}
