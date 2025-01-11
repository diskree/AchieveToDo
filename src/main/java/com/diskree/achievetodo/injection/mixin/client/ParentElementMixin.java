package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.injection.extension.client.AdvancementsScreenExtension;
import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParentElement.class)
public interface ParentElementMixin {

    @Inject(
        method = "mouseReleased",
        at = @At(value = "HEAD")
    )
    private void onMouseReleasedInAdvancementsScreen(
        double mouseX,
        double mouseY,
        int button,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (this instanceof AdvancementsScreenExtension advancementsScreenExtension) {
            advancementsScreenExtension.achievetodo$onMouseReleased(button);
        }
    }
}
