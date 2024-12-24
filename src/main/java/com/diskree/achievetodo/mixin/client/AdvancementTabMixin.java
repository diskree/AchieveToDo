package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.gui.AdvancementsTab;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {

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
        AdvancementsTab tab = AdvancementsTab.findByAdvancement(root.getAdvancementEntry().id());
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
