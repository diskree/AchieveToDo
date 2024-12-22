package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.gui.AdvancementsTabType;
import com.diskree.achievetodo.injection.AdvancementsScreenImpl;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {

    @Shadow
    @Final
    private AdvancementsScreen screen;

    @Inject(
        method = "drawWidgetTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementWidget;drawTooltip(Lnet/minecraft/client/gui/DrawContext;IIFII)V",
            shift = At.Shift.AFTER
        )
    )
    public void saveFocusedAdvancementWidget(
        DrawContext context,
        int mouseX,
        int mouseY,
        int x,
        int y,
        CallbackInfo ci,
        @Local(ordinal = 0) AdvancementWidget advancementWidget
    ) {
        if (screen instanceof AdvancementsScreenImpl screenImpl) {
            screenImpl.advancementssearch$setFocusedAdvancementWidget(advancementWidget);
        }
    }

    @Inject(
        method = "drawWidgetTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
            shift = At.Shift.AFTER
        )
    )
    public void resetFocusedAdvancementWidget(
        DrawContext context,
        int mouseX,
        int mouseY,
        int x,
        int y,
        CallbackInfo ci,
        @Local(ordinal = 0) boolean shouldShowTooltip
    ) {
        if (!shouldShowTooltip && screen instanceof AdvancementsScreenImpl screenImpl) {
            screenImpl.advancementssearch$setFocusedAdvancementWidget(null);
        }
    }

    @Inject(
        method = "create",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void groupTabs(
        MinecraftClient client,
        AdvancementsScreen screen,
        int index,
        @NotNull PlacedAdvancement root,
        CallbackInfoReturnable<AdvancementTab> cir
    ) {
        AdvancementDisplay advancementDisplay = root.getAdvancement().display().orElse(null);
        if (advancementDisplay == null) {
            cir.setReturnValue(null);
            return;
        }
        AdvancementsTabType tab = AdvancementsTabType.findByAdvancement(root.getAdvancementEntry().id());
        if (tab == null) {
            cir.setReturnValue(null);
            return;
        }
        cir.setReturnValue(new AdvancementTab(
            client,
            screen,
            tab.getPosition(),
            tab.getOrder(),
            root,
            advancementDisplay
        ));
    }
}
