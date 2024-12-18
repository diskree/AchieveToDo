package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

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
        PlacedAdvancement root,
        CallbackInfoReturnable<AdvancementTab> cir
    ) {
        if (root == null) {
            cir.setReturnValue(null);
            return;
        }
        AdvancementEntry advancementEntry = root.getAdvancementEntry();
        if (advancementEntry == null) {
            cir.setReturnValue(null);
            return;
        }
        Identifier advancementId = advancementEntry.id();
        if (advancementId == null) {
            cir.setReturnValue(null);
            return;
        }
        Advancement advancement = root.getAdvancement();
        if (advancement == null) {
            cir.setReturnValue(null);
            return;
        }
        AdvancementDisplay advancementDisplay = advancement.display().orElse(null);
        if (advancementDisplay == null) {
            cir.setReturnValue(null);
            return;
        }
        AbilityAdvancementsGenerator.Tab tab;
        try {
            tab = AbilityAdvancementsGenerator.Tab.valueOf(advancementId.getPath().split("/")[0].toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
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
