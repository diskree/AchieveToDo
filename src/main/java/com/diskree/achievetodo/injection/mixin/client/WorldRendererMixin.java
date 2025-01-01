package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.client.gui.DesignCodePalette;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Unique
    private static final RenderLayer LOCKED_DUNGEON_BORDER_COLOR_MASK = createLockedDungeonBorder(false, false);

    @Unique
    private static final RenderLayer LOCKED_DUNGEON_BORDER_ALL_MASK = createLockedDungeonBorder(true, false);

    @Unique
    private static final RenderLayer LOCKED_DUNGEON_BORDER_COLOR_MASK_CULLING = createLockedDungeonBorder(false, true);

    @Unique
    private static final RenderLayer LOCKED_DUNGEON_BORDER_ALL_MASK_CULLING = createLockedDungeonBorder(true, true);

    @Unique
    private static final long LOCKED_DUNGEON_BORDER_ANIMATION_DURATION = TimeUnit.SECONDS.toMillis(3);

    @Unique
    private static final float ENTER_LOCKED_DUNGEON_BORDER_FADE_ALPHA_SPEED = 0.003f;

    @Unique
    private static final Identifier LOCKED_DUNGEON_BORDER_TEXTURE =
        Identifier.ofVanilla("textures/misc/forcefield.png");

    @Unique
    private boolean wasInsideLockedDungeon = false;

    @Unique
    private float fadeInsideLockedDungeonAlpha = 1.0f;

    @Unique
    private static RenderLayer getLockedDungeonBorder(boolean allMask, boolean isCullingEnabled) {
        if (allMask) {
            return isCullingEnabled ? LOCKED_DUNGEON_BORDER_ALL_MASK_CULLING : LOCKED_DUNGEON_BORDER_ALL_MASK;
        }
        return isCullingEnabled ? LOCKED_DUNGEON_BORDER_COLOR_MASK_CULLING : LOCKED_DUNGEON_BORDER_COLOR_MASK;
    }

    @Unique
    private static @NotNull RenderLayer createLockedDungeonBorder(boolean allMask, boolean isCullingEnabled) {
        return RenderLayer.of(
            BuildConfig.MOD_ID + "_locked_dungeon_border",
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

    @Inject(
        method = "method_62216",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V",
            shift = At.Shift.AFTER
        )
    )
    public void renderLockedDungeonBorders(
        Fog fog,
        float tickDelta,
        Vec3d cameraPos,
        int visibleDistanceThreshold,
        float farPlaneDistance,
        CallbackInfo ci
    ) {
        RenderLayer renderLayer = null;
        float animationTime = 0.0f;
        boolean isInsideLockedDungeon = false;
        for (List<Box> boxes : AchieveToDoClient.getLockedDungeons().values()) {
            for (Box box : boxes) {
                boolean isCameraInside = box.contains(cameraPos);
                double alpha;
                if (isCameraInside) {
                    if (!isInsideLockedDungeon) {
                        isInsideLockedDungeon = true;
                        if (!wasInsideLockedDungeon) {
                            fadeInsideLockedDungeonAlpha = 0.0f;
                        }
                    }
                    alpha = fadeInsideLockedDungeonAlpha;
                } else {
                    double closestX = Math.clamp(cameraPos.x, box.minX, box.maxX);
                    double closestY = Math.clamp(cameraPos.y, box.minY, box.maxY);
                    double closestZ = Math.clamp(cameraPos.z, box.minZ, box.maxZ);

                    double distance = Math.sqrt(cameraPos.squaredDistanceTo(closestX, closestY, closestZ));
                    if (distance >= visibleDistanceThreshold) {
                        continue;
                    }
                    alpha = 1.0f;
                    double fadeStart = visibleDistanceThreshold * 0.5f;
                    if (distance > fadeStart) {
                        alpha -= (distance - fadeStart) / (visibleDistanceThreshold - fadeStart);
                    }
                }

                if (renderLayer == null) {
                    RenderSystem.setShaderTexture(0, LOCKED_DUNGEON_BORDER_TEXTURE);
                    renderLayer = getLockedDungeonBorder(MinecraftClient.isFabulousGraphicsOrBetter(), !isCameraInside);
                    renderLayer.startDrawing();
                    long animationDurationMs = LOCKED_DUNGEON_BORDER_ANIMATION_DURATION;
                    if (isCameraInside) {
                        animationDurationMs *= 2;
                    }
                    animationTime = (float) (Util.getMeasuringTimeMs() % animationDurationMs) / animationDurationMs;
                }

                RenderSystem.setShaderColor(
                    ColorHelper.getRedFloat(DesignCodePalette.IN_WORLD_RGB),
                    ColorHelper.getGreenFloat(DesignCodePalette.IN_WORLD_RGB),
                    ColorHelper.getBlueFloat(DesignCodePalette.IN_WORLD_RGB),
                    (float) alpha
                );

                BufferBuilder bufferBuilder = Tessellator.getInstance()
                    .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

                float minX = (float) (box.minX - cameraPos.x);
                float maxX = (float) (box.maxX - cameraPos.x);
                float minY = (float) (box.minY - cameraPos.y);
                float maxY = (float) (box.maxY - cameraPos.y);
                float minZ = (float) (box.minZ - cameraPos.z);
                float maxZ = (float) (box.maxZ - cameraPos.z);

                if (isCameraInside || cameraPos.x > box.maxX - visibleDistanceThreshold) {
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
                if (isCameraInside || cameraPos.x < box.minX + visibleDistanceThreshold) {
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
                if (isCameraInside || cameraPos.z > box.maxZ - visibleDistanceThreshold) {
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
                if (isCameraInside || cameraPos.z < box.minZ + visibleDistanceThreshold) {
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
                if (isCameraInside || cameraPos.y < box.minY + visibleDistanceThreshold) {
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
                if (isCameraInside || cameraPos.y > box.maxY - visibleDistanceThreshold) {
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
        }
        if (renderLayer != null) {
            renderLayer.endDrawing();
            RenderSystem.disableCull();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (isInsideLockedDungeon) {
            fadeInsideLockedDungeonAlpha += ENTER_LOCKED_DUNGEON_BORDER_FADE_ALPHA_SPEED * tickDelta;
            if (fadeInsideLockedDungeonAlpha > 1.0f) {
                fadeInsideLockedDungeonAlpha = 1.0f;
            }
        } else {
            fadeInsideLockedDungeonAlpha = 1.0f;
        }
        wasInsideLockedDungeon = isInsideLockedDungeon;
    }
}
