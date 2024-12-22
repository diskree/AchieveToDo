package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.injection.AdvancementsScreenImpl;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin extends Screen implements AdvancementsScreenImpl {

    @Unique
    private AdvancementWidget focusedAdvancementWidget;

    @Unique
    private boolean isFocusedAdvancementClicked;

    public AdvancementsScreenMixin() {
        super(null);
    }

    @Override
    public void advancementssearch$setFocusedAdvancementWidget(AdvancementWidget focusedAdvancementWidget) {
        this.focusedAdvancementWidget = focusedAdvancementWidget;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isFocusedAdvancementClicked && focusedAdvancementWidget != null) {
            AdvancementEntry focusedAdvancement = focusedAdvancementWidget.advancement.getAdvancementEntry();
            Identifier focusedAdvancementId = focusedAdvancement.id();
            List<String> childIds = new ArrayList<>();
            for (PlacedAdvancement placedAdvancement : focusedAdvancementWidget.advancement.getChildren()) {
                childIds.add(placedAdvancement.getAdvancementEntry().id().toString());
            }
            System.out.println("Advancement ID: " + focusedAdvancementId.toString() +
                "\nChildren IDs: " + String.join(", ", childIds));
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Inject(
        method = "mouseClicked",
        at = @At(value = "HEAD")
    )
    public void mouseClickedInject(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        isFocusedAdvancementClicked = focusedAdvancementWidget != null;
    }
}
