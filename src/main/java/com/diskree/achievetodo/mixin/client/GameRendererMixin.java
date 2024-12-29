package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique
    private float blackOverlayAlpha = 0.0f;

    @Unique
    private void drawBlackOverlay(@NotNull MatrixStack stack) {
        VertexConsumerProvider.Immediate immediate = buffers.getEntityVertexConsumers();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getGui());
        int blackColor = (int) (blackOverlayAlpha * 255.0F) << 24;
        vertexConsumer.vertex(matrix, -1.0F, -1.0F, -0.1F).color(blackColor);
        vertexConsumer.vertex(matrix, 1.0F, -1.0F, -0.1F).color(blackColor);
        vertexConsumer.vertex(matrix, 1.0F, 1.0F, -0.1F).color(blackColor);
        vertexConsumer.vertex(matrix, -1.0F, 1.0F, -0.1F).color(blackColor);
        immediate.draw();
    }

    @Shadow
    @Final
    private BufferBuilderStorage buffers;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void updateWorldIcon();

    @Inject(
        method = "renderHand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;",
            shift = At.Shift.BEFORE,
            ordinal = 1
        )
    )
    private void lockVision(
        Camera camera,
        float tickDelta,
        Matrix4f matrix4f,
        CallbackInfo ci,
        @Local @NotNull MatrixStack stack
    ) {
        if (client.player == null) {
            return;
        }
        if (AchieveToDo.isAbilityLocked(client.player, AbilityType.VISION)) {
            blackOverlayAlpha = 1.0f;
            drawBlackOverlay(stack);
        } else if (blackOverlayAlpha > 0) {
            blackOverlayAlpha = Math.max(blackOverlayAlpha - 0.02f * tickDelta, 0);
            if (blackOverlayAlpha == 0) {
                updateWorldIcon();
            } else {
                drawBlackOverlay(stack);
            }
        }
    }

    @Inject(
        method = "updateWorldIcon()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void scheduleWorldIconUpdateUntilVisionAbilityUnlocked(CallbackInfo ci) {
        if (blackOverlayAlpha != 0) {
            ci.cancel();
        }
    }
}
