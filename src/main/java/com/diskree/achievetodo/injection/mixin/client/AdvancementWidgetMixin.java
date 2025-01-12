package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.generation.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.server.Constants;
import com.diskree.achievetodo.tracking.TrackedNearbyEntitiesType;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import com.diskree.achievetodo.tracking.TrackedStatType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin {

    @Unique
    private static final Identifier ABILITY_MYSTIFIED_MASK_TEXTURE =
        AchieveToDoMod.getIdentifier("ability_mystified_mask");

    @Unique
    private TrackedScoreType trackedScoreType;

    @Unique
    private TrackedNearbyEntitiesType trackedNearbyEntitiesType;

    @Unique
    private TrackedStatType trackedStatType;

    @Unique
    private AbilityType ability;

    @Unique
    private boolean shouldRenderMystifiedMask() {
        if (progress == null || ability == null || !AchieveToDoClient.isAbilityLocked(ability, true)) {
            return false;
        }
        int requiredCount = AchieveToDoClient.getRequiredAdvancementsCount(ability);
        if (requiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG ||
            requiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG
        ) {
            return false;
        }
        CriterionProgress demystifiedCriterionProgress = progress.getCriterionProgress(
            AbilityAdvancementsGenerator.DEMYSTIFIED_CRITERION_PREFIX + ability.getName()
        );
        return demystifiedCriterionProgress != null && !demystifiedCriterionProgress.isObtained();
    }

    @Shadow
    private @Nullable AdvancementProgress progress;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;",
            shift = At.Shift.BEFORE
        )
    )
    private void findAbility(
        AdvancementTab tab,
        MinecraftClient client,
        @NotNull PlacedAdvancement advancement,
        AdvancementDisplay display,
        CallbackInfo ci
    ) {
        Identifier advancementId = advancement.getAdvancementEntry().id();
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

    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;copy()Lnet/minecraft/text/MutableText;"
        )
    )
    private MutableText appendSpecialFlagInfoToDescription(Text text, @NotNull Operation<MutableText> original) {
        MutableText originalText = original.call(text);
        if (ability != null) {
            int requiredCount = AchieveToDoClient.getRequiredAdvancementsCount(ability);
            boolean isInitiallyUnlocked = requiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG;
            boolean isPermanentlyLocked = requiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG;
            if (isInitiallyUnlocked || isPermanentlyLocked) {
                Text specialFlagInfo = AchieveToDoClient
                    .translate(isInitiallyUnlocked ? "ability.initially_unlocked" : "ability.permanently_locked")
                    .formatted(Formatting.ITALIC)
                    .formatted(isInitiallyUnlocked ? Formatting.GRAY : Formatting.RED);
                originalText = originalText
                    .append(ScreenTexts.LINE_BREAK)
                    .append(ScreenTexts.LINE_BREAK)
                    .append(specialFlagInfo);
            }
        }
        return originalText;
    }

    @WrapOperation(
        method = "getProgressWidth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/AdvancementRequirements;getLength()I"
        )
    )
    public int setRequiredAdvancementsCount(AdvancementRequirements requirements, Operation<Integer> original) {
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
        method = "getProgressWidth",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void overrideProgressTextWidth(CallbackInfoReturnable<Integer> cir) {
        if (ability != null) {
            int requiredCount = AchieveToDoClient.getRequiredAdvancementsCount(ability);
            if (requiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG ||
                requiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG
            ) {
                cir.setReturnValue(0);
                return;
            }
        }
        if (trackedScoreType != null && trackedScoreType.isPercentage() ||
            trackedStatType != null && trackedStatType.isPercentage()
        ) {
            cir.setReturnValue(8 + client.textRenderer.getWidth(Text.translatable("mco.upload.percent", 100)));
        }
    }

    @ModifyArg(
        method = "renderWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"
        ),
        index = 1
    )
    private Identifier renderMystifiedMask(Identifier original) {
        return shouldRenderMystifiedMask() ? ABILITY_MYSTIFIED_MASK_TEXTURE : original;
    }

    @WrapOperation(
        method = "renderWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawItemWithoutEntity(Lnet/minecraft/item/ItemStack;II)V"
        )
    )
    private void hideAdvancementIconForMystifiedAbility(
        DrawContext instance,
        ItemStack stack,
        int x,
        int y,
        Operation<Void> original
    ) {
        if (!shouldRenderMystifiedMask()) {
            original.call(instance, stack, x, y);
        }
    }

    @ModifyReturnValue(
        method = "shouldRender",
        at = @At("RETURN")
    )
    private boolean hideTooltipForMystifiedAbility(boolean original) {
        return original && !shouldRenderMystifiedMask();
    }
}
