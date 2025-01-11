package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilitiesHierarchyLayerType;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.client.gui.AdvancementsTabType;
import com.diskree.achievetodo.injection.extension.client.AdvancementsScreenExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {

    @Unique
    private final Map<AbilityType, AdvancementWidget> pendingAbilityWidgets = new HashMap<>();

    @Shadow
    protected abstract void addWidget(AdvancementWidget widget, AdvancementEntry advancement);

    @Shadow
    @Final
    private PlacedAdvancement root;

    @Shadow
    @Final
    private Map<AdvancementEntry, AdvancementWidget> widgets;

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
        if (screen instanceof AdvancementsScreenExtension screenImpl &&
            AdvancementsTabType.findByAdvancement(root) != null &&
            advancementWidget.progress != null && !advancementWidget.progress.isDone() &&
            AchieveToDoMod.isTrackableAdvancement(advancementWidget.advancement.getAdvancementEntry())
        ) {
            screenImpl.achievetodo$setFocusedAdvancementWidget(advancementWidget);
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
        if (screen instanceof AdvancementsScreenExtension screenImpl &&
            !shouldShowTooltip &&
            AdvancementsTabType.findByAdvancement(root) != null
        ) {
            screenImpl.achievetodo$setFocusedAdvancementWidget(null);
        }
    }

    @Inject(
        method = "create",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void setCustomTabsLayout(
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
        AdvancementsTabType tab = AdvancementsTabType.findByAdvancement(root);
        if (tab == null) {
            cir.setReturnValue(null);
            return;
        }
        if (tab == AdvancementsTabType.ABILITIES) {
            int childrenCount = 0;
            for (AbilitiesHierarchyLayerType layerType : AbilitiesHierarchyLayerType.values()) {
                childrenCount += layerType.getRowsCount();
            }
            advancementDisplay.setPos(advancementDisplay.getX(), (float) (childrenCount / 2));
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

    @WrapOperation(
        method = "addAdvancement",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/client/gui/screen/advancement/AdvancementTab;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/advancement/PlacedAdvancement;Lnet/minecraft/advancement/AdvancementDisplay;)Lnet/minecraft/client/gui/screen/advancement/AdvancementWidget;"
        )
    )
    public AdvancementWidget restructureAbilitiesAdvancements(
        AdvancementTab tab,
        MinecraftClient client,
        @NotNull PlacedAdvancement advancement,
        AdvancementDisplay display,
        @NotNull Operation<AdvancementWidget> original
    ) {
        AbilityType abilityType = null;
        boolean isFirstInRow = false;
        boolean shouldSkipVanillaBehavior = false;
        if (AdvancementsTabType.findByAdvancement(root) == AdvancementsTabType.ABILITIES) {
            abilityType = AbilityType.findByAdvancement(advancement);
            if (abilityType != null) {
                List<List<AbilityType>> rows = AchieveToDoClient.getAbilityRows();
                if (rows != null) {
                    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                        List<AbilityType> row = rows.get(rowIndex);
                        if (abilityType == row.getFirst()) {
                            isFirstInRow = true;
                            display.setPos(display.getX(), rowIndex);
                            break;
                        }
                    }
                    if (!isFirstInRow) {
                        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                            List<AbilityType> row = rows.get(rowIndex);
                            int columnIndex = row.indexOf(abilityType);
                            if (columnIndex != -1) {
                                display.setPos(columnIndex + 1, rowIndex);
                                break;
                            }
                        }
                        shouldSkipVanillaBehavior = true;
                    }
                }
            }
        }
        AdvancementWidget advancementWidget = original.call(tab, client, advancement, display);
        if (abilityType != null) {
            if (isFirstInRow || shouldSkipVanillaBehavior) {
                pendingAbilityWidgets.put(abilityType, advancementWidget);
            }
            if (pendingAbilityWidgets.size() == AbilityType.values().length) {
                List<List<AbilityType>> rows = AchieveToDoClient.getAbilityRows();

                for (List<AbilityType> row : rows) {
                    for (int columnIndex = 1; columnIndex < row.size(); columnIndex++) {
                        AbilityType currentAbility = row.get(columnIndex);
                        AbilityType previousAbility = row.get(columnIndex - 1);
                        AdvancementWidget currentWidget = pendingAbilityWidgets.get(currentAbility);
                        if (currentWidget == null) {
                            for (AdvancementEntry widgetAdvancement : widgets.keySet()) {
                                if (AbilityType.findByAdvancement(widgetAdvancement) == currentAbility) {
                                    currentWidget = widgets.get(widgetAdvancement);
                                }
                            }
                        }
                        if (currentWidget == null) {
                            continue;
                        }
                        AdvancementWidget parentWidget = pendingAbilityWidgets.get(previousAbility);
                        if (parentWidget == null) {
                            for (AdvancementEntry widgetAdvancement : widgets.keySet()) {
                                if (AbilityType.findByAdvancement(widgetAdvancement) == previousAbility) {
                                    parentWidget = widgets.get(widgetAdvancement);
                                }
                            }
                        }
                        if (parentWidget == null) {
                            continue;
                        }
                        currentWidget.parent = parentWidget;
                        parentWidget.addChild(currentWidget);
                        addWidget(currentWidget, currentWidget.advancement.getAdvancementEntry());
                    }
                }
            }
        }
        return shouldSkipVanillaBehavior ? null : advancementWidget;
    }

    @Inject(
        method = "addWidget",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void skipNullWidget(AdvancementWidget widget, @NotNull AdvancementEntry advancement, CallbackInfo ci) {
        if (widget == null) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementWidget;renderLines(Lnet/minecraft/client/gui/DrawContext;IIZ)V"
        )
    )
    private void disableLinesRenderInAdvancementInfo(
        AdvancementWidget advancementWidget,
        DrawContext context,
        int x,
        int y,
        boolean border,
        Operation<Void> original
    ) {
        if (screen instanceof AdvancementsScreenExtension advancementsScreenExtension &&
            AdvancementsTabType.findByAdvancement(root) != null &&
            advancementsScreenExtension.achievetodo$getActiveAdvancementId() != null
        ) {
            return;
        }
        original.call(advancementWidget, context, x, y, border);
    }

    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementWidget;renderWidgets(Lnet/minecraft/client/gui/DrawContext;II)V"
        )
    )
    private void renderAdvancementInfo(
        AdvancementWidget advancementWidget,
        DrawContext context,
        int x,
        int y,
        Operation<Void> original
    ) {
        if (screen instanceof AdvancementsScreenExtension advancementsScreenExtension) {
            if (AdvancementsTabType.findByAdvancement(root) != null) {
                Identifier activeAdvancementId = advancementsScreenExtension.achievetodo$getActiveAdvancementId();
                if (activeAdvancementId != null) {

                    return;
                }
            } else {
                advancementsScreenExtension.achievetodo$setActiveAdvancementId(null);
            }
        }
        original.call(advancementWidget, context, x, y);
    }

    @Inject(
        method = "drawWidgetTooltip",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void disableWidgetTooltipRenderInAdvancementInfo(
        DrawContext context,
        int mouseX,
        int mouseY,
        int x,
        int y,
        CallbackInfo ci
    ) {
        if (screen instanceof AdvancementsScreenExtension advancementsScreenExtension &&
            AdvancementsTabType.findByAdvancement(root) != null &&
            advancementsScreenExtension.achievetodo$getActiveAdvancementId() != null
        ) {
            ci.cancel();
        }
    }
}
