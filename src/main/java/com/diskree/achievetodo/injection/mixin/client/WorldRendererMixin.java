package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.client.gui.DesignCodePalette;
import com.diskree.achievetodo.client.gui.LockedLandmarkBox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Unique
    private static final RenderLayer LOCKED_LANDMARK_BORDER_COLOR_MASK = createLockedLandmarkBorder(false, false);

    @Unique
    private static final RenderLayer LOCKED_LANDMARK_BORDER_ALL_MASK = createLockedLandmarkBorder(true, false);

    @Unique
    private static final RenderLayer LOCKED_LANDMARK_BORDER_COLOR_MASK_CULLING = createLockedLandmarkBorder(false, true);

    @Unique
    private static final RenderLayer LOCKED_LANDMARK_BORDER_ALL_MASK_CULLING = createLockedLandmarkBorder(true, true);

    @Unique
    private static final long LOCKED_LANDMARK_BORDER_ANIMATION_DURATION = TimeUnit.SECONDS.toMillis(3);

    @Unique
    private static final float ENTER_LOCKED_LANDMARK_BORDER_FADE_ALPHA_SPEED = 0.003f;

    @Unique
    private static final int LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD = 60;

    @Unique
    private static final Identifier LOCKED_LANDMARK_BORDER_TEXTURE =
        Identifier.ofVanilla("textures/misc/forcefield.png");

    @Unique
    private boolean wasInsideLockedLandmark = false;

    @Unique
    private float fadeInsideLockedLandmarkAlpha = 1.0f;

    @Unique
    private static RenderLayer getLockedLandmarkBorderRenderLayer(boolean allMask, boolean isCullingEnabled) {
        if (allMask) {
            return isCullingEnabled ? LOCKED_LANDMARK_BORDER_ALL_MASK_CULLING : LOCKED_LANDMARK_BORDER_ALL_MASK;
        }
        return isCullingEnabled ? LOCKED_LANDMARK_BORDER_COLOR_MASK_CULLING : LOCKED_LANDMARK_BORDER_COLOR_MASK;
    }

    @Unique
    private static @NotNull RenderLayer createLockedLandmarkBorder(boolean allMask, boolean isCullingEnabled) {
        return RenderLayer.of(
            BuildConfig.MOD_ID + "_locked_landmark_border",
            VertexFormats.POSITION_TEXTURE,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                .program(RenderPhase.POSITION_TEXTURE_PROGRAM)
                .texture(new RenderPhase.Texture(WorldBorderRendering.FORCEFIELD, TriState.FALSE, false))
                .transparency(RenderPhase.OVERLAY_TRANSPARENCY)
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .target(RenderPhase.WEATHER_TARGET)
                .writeMaskState(allMask ? RenderPhase.ALL_MASK : RenderPhase.COLOR_MASK)
                .layering(RenderPhase.WORLD_BORDER_LAYERING)
                .cull(isCullingEnabled ? RenderPhase.ENABLE_CULLING : RenderPhase.DISABLE_CULLING)
                .build(false)
        );
    }

    @Shadow
    private @Nullable ClientWorld world;

    @Inject(
        method = "method_62216",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V",
            shift = At.Shift.AFTER
        )
    )
    public void renderLockedLandmarkBorders(
        Fog fog,
        float tickDelta,
        Vec3d cameraPos,
        int viewDistance,
        float farPlaneDistance,
        CallbackInfo ci
    ) {
        RenderLayer renderLayer = null;
        boolean isInsideLockedLandmark = false;
        if (world == null) {
            return;
        }
        DimensionType dimensionType = DimensionType.findByWorld(world.getRegistryKey());
        if (dimensionType == null) {
            return;
        }
        for (LockedLandmarkBox lockedLandmarkBox : AchieveToDoClient.getLockedLandmarkBoxes()) {
            if (lockedLandmarkBox.dimension() != dimensionType) {
                continue;
            }
            Box box = lockedLandmarkBox.box();
            boolean isCameraInside = box.contains(cameraPos);
            double alpha;
            if (isCameraInside) {
                if (!isInsideLockedLandmark) {
                    isInsideLockedLandmark = true;
                    if (!wasInsideLockedLandmark) {
                        fadeInsideLockedLandmarkAlpha = 0.0f;
                    }
                }
                alpha = fadeInsideLockedLandmarkAlpha;
            } else {
                double closestX = Math.clamp(cameraPos.x, box.minX, box.maxX);
                double closestY = Math.clamp(cameraPos.y, box.minY, box.maxY);
                double closestZ = Math.clamp(cameraPos.z, box.minZ, box.maxZ);

                double distance = Math.sqrt(cameraPos.squaredDistanceTo(closestX, closestY, closestZ));
                if (distance >= LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                    continue;
                }
                alpha = 1.0f;
                double fadeStart = LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD * 0.5f;
                if (distance > fadeStart) {
                    alpha -= (distance - fadeStart) /
                        (LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD - fadeStart);
                }
            }

            RenderSystem.setShaderTexture(0, LOCKED_LANDMARK_BORDER_TEXTURE);
            renderLayer = getLockedLandmarkBorderRenderLayer(
                MinecraftClient.isFabulousGraphicsOrBetter(),
                !isCameraInside
            );
            renderLayer.startDrawing();
            long animationDurationMs = LOCKED_LANDMARK_BORDER_ANIMATION_DURATION;
            if (isCameraInside) {
                animationDurationMs *= 2;
            }
            float animationTime = (float) (Util.getMeasuringTimeMs() % animationDurationMs) / animationDurationMs;

            RenderSystem.setShaderColor(
                ColorHelper.getRedFloat(DesignCodePalette.IN_WORLD_RGB),
                ColorHelper.getGreenFloat(DesignCodePalette.IN_WORLD_RGB),
                ColorHelper.getBlueFloat(DesignCodePalette.IN_WORLD_RGB),
                (float) alpha
            );

            BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            float minX = (float) (box.minX - cameraPos.x + 0.00001);
            float minY = (float) (box.minY - cameraPos.y + 0.00001);
            float minZ = (float) (box.minZ - cameraPos.z + 0.00001);
            float maxX = (float) (box.maxX - cameraPos.x - 0.00001);
            float maxY = (float) (box.maxY - cameraPos.y - 0.00001);
            float maxZ = (float) (box.maxZ - cameraPos.z - 0.00001);

            if (isCameraInside || cameraPos.x > box.maxX - LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                for (float y = minY; y < maxY; ) {
                    float segmentY = Math.min(1.0f, maxY - y);
                    float textureU = 0f;
                    for (float z = minZ; z < maxZ; ) {
                        float segmentZ = Math.min(1.0f, maxZ - z);
                        float halfSegmentZ = segmentZ * 0.5f;
                        float halfSegmentY = segmentY * 0.5f;

                        bufferBuilder
                            .vertex(maxX, y + segmentY, z)
                            .normal(1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentZ, animationTime + halfSegmentY);
                        bufferBuilder
                            .vertex(maxX, y + segmentY, z + segmentZ)
                            .normal(1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentZ, animationTime);
                        bufferBuilder
                            .vertex(maxX, y, z + segmentZ)
                            .normal(1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime);
                        bufferBuilder
                            .vertex(maxX, y, z)
                            .normal(1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime + halfSegmentY);

                        z += segmentZ;
                        textureU += 0.5f;
                    }
                    y += segmentY;
                }
            }
            if (isCameraInside || cameraPos.x < box.minX + LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                for (float y = minY; y < maxY; ) {
                    float segmentY = Math.min(1.0f, maxY - y);
                    float textureU = 0f;
                    for (float z = minZ; z < maxZ; ) {
                        float segmentZ = Math.min(1.0f, maxZ - z);
                        float halfSegmentZ = segmentZ * 0.5f;
                        float halfSegmentY = segmentY * 0.5f;

                        bufferBuilder
                            .vertex(minX, y, z)
                            .normal(-1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentZ, animationTime);
                        bufferBuilder
                            .vertex(minX, y, z + segmentZ)
                            .normal(-1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime);
                        bufferBuilder
                            .vertex(minX, y + segmentY, z + segmentZ)
                            .normal(-1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime + halfSegmentY);
                        bufferBuilder
                            .vertex(minX, y + segmentY, z)
                            .normal(-1.0f, 0.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentZ, animationTime + halfSegmentY);

                        z += segmentZ;
                        textureU += 0.5f;
                    }
                    y += segmentY;
                }
            }
            if (isCameraInside || cameraPos.z > box.maxZ - LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                for (float y = minY; y < maxY; ) {
                    float segmentY = Math.min(1.0f, maxY - y);
                    float textureU = 0f;
                    for (float x = minX; x < maxX; ) {
                        float segmentX = Math.min(1.0f, maxX - x);
                        float halfSegmentX = segmentX * 0.5f;
                        float halfSegmentY = segmentY * 0.5f;

                        bufferBuilder
                            .vertex(x, y, maxZ)
                            .normal(0.0f, 0.0f, 1.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime);
                        bufferBuilder
                            .vertex(x + segmentX, y, maxZ)
                            .normal(0.0f, 0.0f, 1.0f)
                            .texture(animationTime - textureU, animationTime);
                        bufferBuilder
                            .vertex(x + segmentX, y + segmentY, maxZ)
                            .normal(0.0f, 0.0f, 1.0f)
                            .texture(animationTime - textureU, animationTime + halfSegmentY);
                        bufferBuilder
                            .vertex(x, y + segmentY, maxZ)
                            .normal(0.0f, 0.0f, 1.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentY);

                        x += segmentX;
                        textureU += 0.5f;
                    }
                    y += segmentY;
                }
            }
            if (isCameraInside || cameraPos.z < box.minZ + LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                for (float y = minY; y < maxY; ) {
                    float segmentY = Math.min(1.0f, maxY - y);
                    float textureU = 0f;
                    for (float x = minX; x < maxX; ) {
                        float segmentX = Math.min(1.0f, maxX - x);
                        float halfSegmentX = segmentX * 0.5f;
                        float halfSegmentY = segmentY * 0.5f;

                        bufferBuilder
                            .vertex(x, y, minZ)
                            .normal(0.0f, 0.0f, -1.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime);
                        bufferBuilder
                            .vertex(x, y + segmentY, minZ)
                            .normal(0.0f, 0.0f, -1.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentY);
                        bufferBuilder
                            .vertex(x + segmentX, y + segmentY, minZ)
                            .normal(0.0f, 0.0f, -1.0f)
                            .texture(animationTime - textureU, animationTime + halfSegmentY);
                        bufferBuilder
                            .vertex(x + segmentX, y, minZ)
                            .normal(0.0f, 0.0f, -1.0f)
                            .texture(animationTime - textureU, animationTime);

                        x += segmentX;
                        textureU += 0.5f;
                    }
                    y += segmentY;
                }
            }
            if (isCameraInside || cameraPos.y < box.minY + LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                for (float z = minZ; z < maxZ; ) {
                    float segmentZ = Math.min(1.0f, maxZ - z);
                    float textureU = 0f;
                    for (float x = minX; x < maxX; ) {
                        float segmentX = Math.min(1.0f, maxX - x);
                        float halfSegmentX = segmentX * 0.5f;
                        float halfSegmentZ = segmentZ * 0.5f;

                        bufferBuilder
                            .vertex(x + segmentX, minY, z)
                            .normal(0.0f, -1.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime + halfSegmentZ);
                        bufferBuilder
                            .vertex(x + segmentX, minY, z + segmentZ)
                            .normal(0.0f, -1.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime);
                        bufferBuilder
                            .vertex(x, minY, z + segmentZ)
                            .normal(0.0f, -1.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime);
                        bufferBuilder
                            .vertex(x, minY, z)
                            .normal(0.0f, -1.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentZ);

                        x += segmentX;
                        textureU += 0.5f;
                    }
                    z += segmentZ;
                }
            }
            if (isCameraInside || cameraPos.y > box.maxY - LOCKED_LANDMARK_BORDER_VISIBLE_DISTANCE_THRESHOLD) {
                for (float z = minZ; z < maxZ; ) {
                    float segmentZ = Math.min(1.0f, maxZ - z);
                    float textureU = 0f;
                    for (float x = minX; x < maxX; ) {
                        float segmentX = Math.min(1.0f, maxX - x);
                        float halfSegmentX = segmentX * 0.5f;
                        float halfSegmentZ = segmentZ * 0.5f;

                        bufferBuilder
                            .vertex(x, maxY, z)
                            .normal(0.0f, 1.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentZ);
                        bufferBuilder
                            .vertex(x, maxY, z + segmentZ)
                            .normal(0.0f, 1.0f, 0.0f)
                            .texture(animationTime - textureU + halfSegmentX, animationTime);
                        bufferBuilder
                            .vertex(x + segmentX, maxY, z + segmentZ)
                            .normal(0.0f, 1.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime);
                        bufferBuilder
                            .vertex(x + segmentX, maxY, z)
                            .normal(0.0f, 1.0f, 0.0f)
                            .texture(animationTime - textureU, animationTime + halfSegmentZ);

                        x += segmentX;
                        textureU += 0.5f;
                    }
                    z += segmentZ;
                }
            }

            BuiltBuffer buffer = bufferBuilder.endNullable();
            if (buffer != null) {
                BufferRenderer.drawWithGlobalProgram(buffer);
            }
        }
        if (renderLayer != null) {
            renderLayer.endDrawing();
            RenderSystem.disableCull();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (isInsideLockedLandmark) {
            fadeInsideLockedLandmarkAlpha += ENTER_LOCKED_LANDMARK_BORDER_FADE_ALPHA_SPEED * tickDelta;
            if (fadeInsideLockedLandmarkAlpha > 1.0f) {
                fadeInsideLockedLandmarkAlpha = 1.0f;
            }
        } else {
            fadeInsideLockedLandmarkAlpha = 1.0f;
        }
        wasInsideLockedLandmark = isInsideLockedLandmark;
    }
}
