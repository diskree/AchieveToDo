package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.injection.extension.main.AdvancementProgressImpl;
import com.diskree.achievetodo.tracking.TrackedNearbyEntitiesType;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import com.diskree.achievetodo.tracking.TrackedStatType;
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
    private TrackedStatType trackedStatType;

    @Unique
    private AbilityType ability;

    @Override
    public void achievetodo$setAdvancementId(Identifier advancementId) {
        trackedScoreType = TrackedScoreType.findByAdvancement(advancementId);
        if (trackedScoreType == null) {
            trackedNearbyEntitiesType = TrackedNearbyEntitiesType.findByAdvancement(advancementId);
            if (trackedNearbyEntitiesType == null) {
                trackedStatType = TrackedStatType.findByAdvancement(advancementId);
                if (trackedStatType == null) {
                    ability = AbilityType.findByAdvancement(advancementId);
                }
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
    public void overrideObtainedRequirementsCount(CallbackInfoReturnable<Integer> cir) {
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
        } else if (trackedStatType != null) {
            if (isDone()) {
                cir.setReturnValue(trackedStatType.getFinalValue());
            } else {
                cir.setReturnValue(AchieveToDoClient.getTrackedStat(trackedStatType));
            }
        } else if (ability != null) {
            cir.setReturnValue(
                Math.min(
                    AchieveToDoClient.getObtainedAdvancementsCount(),
                    AchieveToDoClient.getRequiredAdvancementsCount(ability)
                )
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
    public int overrideRequiredAdvancementsCount(
        AdvancementRequirements requirements,
        Operation<Integer> original
    ) {
        if (trackedScoreType != null) {
            return trackedScoreType.getFinalValue();
        }
        if (trackedNearbyEntitiesType != null) {
            return trackedNearbyEntitiesType.getEntitiesCount();
        }
        if (trackedStatType != null) {
            return trackedStatType.getFinalValue();
        }
        if (ability != null) {
            return AchieveToDoClient.getRequiredAdvancementsCount(ability);
        }
        return original.call(requirements);
    }

    @Inject(
        method = "getProgressBarPercentage",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideProgressPercentage(CallbackInfoReturnable<Float> cir) {
        if (ability != null) {
            int requiredAdvancementsCount = AchieveToDoClient.getRequiredAdvancementsCount(ability);
            if (requiredAdvancementsCount == -1) {
                cir.setReturnValue(0.0f);
            } else if (requiredAdvancementsCount == 0) {
                cir.setReturnValue(1.0f);
            }
        } else if (trackedScoreType != null && trackedScoreType.isPercentage()) {
            cir.setReturnValue(isDone() ? 1.0f : AchieveToDoClient.getTrackedScore(trackedScoreType) / 100.0f);
        } else if (trackedStatType != null && trackedStatType.isPercentage()) {
            cir.setReturnValue(isDone() ? 1.0f : AchieveToDoClient.getTrackedStat(trackedStatType) / 100.0f);
        }
    }

    @Inject(
        method = "getProgressBarFraction",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideProgressText(CallbackInfoReturnable<Text> cir) {
        if (ability != null && AchieveToDoClient.getRequiredAdvancementsCount(ability) <= 0) {
            cir.setReturnValue(null);
        } else if (trackedScoreType != null && trackedScoreType.isPercentage()) {
            cir.setReturnValue(Text.translatable(
                "mco.upload.percent",
                isDone() ? 100 : AchieveToDoClient.getTrackedScore(trackedScoreType)
            ));
        } else if (trackedStatType != null && trackedStatType.isPercentage()) {
            cir.setReturnValue(Text.translatable(
                "mco.upload.percent",
                isDone() ? 100 : AchieveToDoClient.getTrackedStat(trackedStatType)
            ));
        }
    }
}
