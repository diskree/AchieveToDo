package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDoClient;
import com.diskree.achievetodo.TrackedNearbyEntitiesType;
import com.diskree.achievetodo.TrackedScoreType;
import com.diskree.achievetodo.injection.AdvancementProgressImpl;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementProgress.class)
public abstract class AdvancementProgressMixin implements AdvancementProgressImpl {

    @Unique
    private TrackedScoreType trackedScoreType;

    @Unique
    private TrackedNearbyEntitiesType trackedNearbyEntitiesType;

    @Unique
    private AbilityType ability;

    @Override
    public void achievetodo$setAdvancementId(Identifier advancementId) {
        trackedScoreType = TrackedScoreType.findByAdvancementId(advancementId);
        if (trackedScoreType == null) {
            trackedNearbyEntitiesType = TrackedNearbyEntitiesType.findByAdvancementId(advancementId);
            if (trackedNearbyEntitiesType == null) {
                ability = AbilityType.findByAdvancementId(advancementId);
            }
        }
    }

    @Shadow
    public abstract boolean isDone();

    @Inject(
        method = "countObtainedRequirements",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setCustomObtainedRequirementsCount(CallbackInfoReturnable<Integer> cir) {
        if (trackedScoreType != null) {
            if (isDone()) {
                cir.setReturnValue(trackedScoreType.getFinalValue());
            } else {
                cir.setReturnValue(AchieveToDoClient.getTrackedScore(trackedScoreType));
            }
        } else if (trackedNearbyEntitiesType != null) {
            if (isDone()) {
                cir.setReturnValue(trackedNearbyEntitiesType.getEntitiesCount());
            } else {
                cir.setReturnValue(AchieveToDoClient.getTrackedNearbyEntitiesCount(trackedNearbyEntitiesType));
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
        if (trackedScoreType != null) {
            return trackedScoreType.getFinalValue();
        }
        if (trackedNearbyEntitiesType != null) {
            return trackedNearbyEntitiesType.getEntitiesCount();
        }
        if (ability != null) {
            return ability.getRequiredAdvancementsCount();
        }
        return original.call(requirements);
    }

    @Inject(
        method = "getProgressBarPercentage",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setTrackedScorePercentage(CallbackInfoReturnable<Float> cir) {
        if (trackedScoreType != null && trackedScoreType.isPercentage()) {
            cir.setReturnValue(isDone() ? 100f : AchieveToDoClient.getTrackedScore(trackedScoreType) / 100f);
        }
    }

    @Inject(
        method = "getProgressBarFraction",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setTrackedScorePercentageText(CallbackInfoReturnable<Text> cir) {
        if (trackedScoreType != null && trackedScoreType.isPercentage()) {
            cir.setReturnValue(Text.translatable(
                "mco.upload.percent",
                isDone() ? 100 : AchieveToDoClient.getTrackedScore(trackedScoreType)
            ));
        }
    }
}
