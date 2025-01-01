package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.generation.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.client.AchieveToDoClient;
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
        Identifier.of(BuildConfig.MOD_ID, "ability_mystified_mask");

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
        if (progress == null ||
            ability == null ||
            !AchieveToDoClient.isAbilityLocked(ability, true) ||
            AchieveToDoClient.getRequiredAdvancementsCount(ability) <= 0
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
    private MutableText appendAbilityInfoToDescription(Text text, @NotNull Operation<MutableText> original) {
        MutableText originalText = original.call(text);
        if (ability != null) {
            int requiredAdvancementsCount = AchieveToDoClient.getRequiredAdvancementsCount(ability);
            if (requiredAdvancementsCount <= 0) {
                Text abilityInfo;
                if (requiredAdvancementsCount == 0) {
                    abilityInfo = AchieveToDoClient.translateModKey("ability.initially_unlocked")
                        .formatted(Formatting.ITALIC)
                        .formatted(Formatting.GRAY);
                } else {
                    abilityInfo = AchieveToDoClient.translateModKey("ability.permanently_locked")
                        .formatted(Formatting.ITALIC)
                        .formatted(Formatting.RED);
                }
                originalText = originalText
                    .append(ScreenTexts.LINE_BREAK)
                    .append(ScreenTexts.LINE_BREAK)
                    .append(abilityInfo);
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
    public int overrideRequirementsCount(AdvancementRequirements requirements, Operation<Integer> original) {
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
        if (ability != null && AchieveToDoClient.getRequiredAdvancementsCount(ability) <= 0) {
            cir.setReturnValue(0);
        } else if (trackedScoreType != null && trackedScoreType.isPercentage() ||
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
