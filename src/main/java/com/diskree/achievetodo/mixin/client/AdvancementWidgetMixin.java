package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
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
public abstract class AdvancementWidgetMixin {

    @Unique
    private static final Identifier ABILITY_MYSTIFIED_MASK_TEXTURE =
        Identifier.of(BuildConfig.MOD_ID, "ability_mystified_mask");

    @Unique
    @Nullable
    private AbilityType ability;

    @Unique
    private boolean shouldRenderMystifiedMask() {
        if (ability == null || progress == null || !AchieveToDo.isAbilityLocked(client.player, ability, true)) {
            return false;
        }
        CriterionProgress demystifiedCriterionProgress = progress.getCriterionProgress(
            AbilityAdvancementsGenerator.DEMYSTIFIED_CRITERION_PREFIX + ability.getLowerCaseName()
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
        PlacedAdvancement advancement,
        AdvancementDisplay display,
        CallbackInfo ci
    ) {
        ability = AbilityType.findByAdvancement(advancement);
    }

    @WrapOperation(
        method = "getProgressWidth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/AdvancementRequirements;getLength()I"
        )
    )
    public int overrideRequirementsCountForAbility(
        AdvancementRequirements requirements,
        Operation<Integer> original
    ) {
        return ability != null ? ability.getRequiredAdvancementsCount() : original.call(requirements);
    }

    @ModifyArg(
        method = "renderWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"
        ),
        index = 1
    )
    private Identifier renderMystifiedMaskInsteadFrameIfNeeded(Identifier original) {
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
        DrawContext instance, ItemStack stack, int x, int y, Operation<Void> original
    ) {
        if (!shouldRenderMystifiedMask()) {
            original.call(instance, stack, x, y);
        }
    }

    @Inject(
        method = "shouldRender",
        at = @At("RETURN"),
        cancellable = true
    )
    private void doNotRenderTooltipForMystifiedAbility(
        int originX,
        int originY,
        int mouseX,
        int mouseY,
        @NotNull CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(cir.getReturnValue() && !shouldRenderMystifiedMask());
    }
}
