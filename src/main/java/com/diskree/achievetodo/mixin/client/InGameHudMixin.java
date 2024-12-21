package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Unique
    private float blackOverlayAlpha = 0.0f;

    @Unique
    private void drawBlackOverlay(@NotNull DrawContext context) {
        context.fill(
            RenderLayer.getGuiOverlay(),
            0,
            0,
            context.getScaledWindowWidth(),
            context.getScaledWindowHeight(),
            (int) (blackOverlayAlpha * 255.0F) << 24
        );
    }

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "renderCrosshair",
        at = @At("RETURN")
    )
    public void renderBlackOverlayWhenSeeAbilityLocked(
        @NotNull DrawContext context,
        RenderTickCounter tickCounter,
        CallbackInfo ci
    ) {
        if (client.player == null) {
            return;
        }
        if (AchieveToDo.isAbilityLocked(client.player, AbilityType.VISION)) {
            blackOverlayAlpha = 1.0f;
            drawBlackOverlay(context);
        } else if (blackOverlayAlpha > 0) {
            blackOverlayAlpha -= 0.02f * tickCounter.getLastFrameDuration();
            blackOverlayAlpha = Math.max(blackOverlayAlpha, 0.0f);
            drawBlackOverlay(context);
        }
    }
}
