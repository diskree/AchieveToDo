package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.client.gui.AdvancementsTab;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin extends Screen {

    @Unique
    private final Identifier LOCKED_TAB_ICON = AchieveToDoMod.getIdentifier("locked_tab_icon");

    @Shadow
    @Final
    private Map<AdvancementEntry, AdvancementTab> tabs;

    @Unique
    private boolean isLockedTab(@NotNull AdvancementTab tab) {
        Identifier advancementId = tab.getRoot().getAdvancementEntry().id();
        AdvancementsTab advancementsTab = AdvancementsTab.findByAdvancement(advancementId);
        return advancementsTab != null && advancementsTab.getLockedTabId().equals(advancementId);
    }

    public AdvancementsScreenMixin() {
        super(null);
    }

    @Inject(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;clear()V",
            shift = At.Shift.AFTER
        )
    )
    public void addLockedTabs(CallbackInfo ci) {
        if (client == null) {
            return;
        }
        AdvancementsScreen advancementsScreen = (AdvancementsScreen) (Object) this;
        for (AdvancementsTab tab : AdvancementsTab.values()) {
            AdvancementDisplay advancementDisplay = new AdvancementDisplay(
                new ItemStack(Items.AIR),
                tab.getLockedTabTooltipText(),
                Text.empty(),
                Optional.empty(),
                AdvancementFrame.TASK,
                false,
                false,
                false
            );
            PlacedAdvancement placedAdvancement = new PlacedAdvancement(
                Advancement.Builder
                    .createUntelemetered()
                    .display(advancementDisplay)
                    .build(tab.getLockedTabId()),
                null
            );
            tabs.put(
                placedAdvancement.getAdvancementEntry(),
                new AdvancementTab(
                    client,
                    advancementsScreen,
                    tab.getPosition(),
                    tab.getOrder(),
                    placedAdvancement,
                    advancementDisplay
                )
            );
        }
    }

    @Inject(
        method = "onRootAdded",
        at = @At(value = "HEAD")
    )
    public void removeLockedTab(@NotNull PlacedAdvancement root, CallbackInfo ci) {
        AdvancementsTab tab = AdvancementsTab.findByAdvancement(root);
        AdvancementEntry lockedRoot = null;
        for (AdvancementEntry advancementEntry : tabs.keySet()) {
            if (tab != null && tab.getLockedTabId().equals(advancementEntry.id())) {
                lockedRoot = advancementEntry;
                break;
            }
        }
        if (lockedRoot != null) {
            tabs.remove(lockedRoot);
        }
    }

    @WrapOperation(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;values()Ljava/util/Collection;"
        )
    )
    public Collection<AdvancementTab> setAbilitiesTabOpenedByDefault(
        Map<AdvancementEntry, AdvancementTab> tabs,
        @NotNull Operation<Collection<AdvancementTab>> original
    ) {
        return original.call(tabs).stream().filter(tab ->
            AdvancementsTab.findByAdvancement(tab.getRoot()) == AdvancementsTab.ABILITIES
        ).toList();
    }

    @WrapOperation(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementTab;isClickOnTab(IIDD)Z"
        )
    )
    public boolean disallowClickOnLockedTab(
        @NotNull AdvancementTab tab,
        int screenX,
        int screenY,
        double mouseX,
        double mouseY,
        Operation<Boolean> original
    ) {
        return !isLockedTab(tab) && original.call(tab, screenX, screenY, mouseX, mouseY);
    }

    @WrapOperation(
        method = "drawWindow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementTab;drawBackground(Lnet/minecraft/client/gui/DrawContext;IIZ)V"
        )
    )
    public void renderLockedTab(
        AdvancementTab tab,
        DrawContext context,
        int x,
        int y,
        boolean selected,
        @NotNull Operation<Void> original
    ) {
        boolean isLockedTab = isLockedTab(tab);
        if (isLockedTab) {
            context.draw();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
            int maskX = x + tab.getType().getTabX(tab.getIndex());
            int maskY = y + tab.getType().getTabY(tab.getIndex());
            switch (tab.getType()) {
                case AdvancementTabType.ABOVE:
                    maskX += 6;
                    maskY += 9;
                    break;
                case AdvancementTabType.BELOW:
                    maskX += 6;
                    maskY += 6;
                    break;
                case AdvancementTabType.LEFT:
                    maskX += 10;
                    maskY += 6;
                    break;
                case AdvancementTabType.RIGHT:
                    maskX += 6;
                    maskY += 5;
            }
            context.drawGuiTexture(RenderLayer::getGuiTextured, LOCKED_TAB_ICON, maskX, maskY, 16, 16);
        }
        original.call(tab, context, x, y, selected);
        if (isLockedTab) {
            context.draw();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
}
