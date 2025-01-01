package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Unique
    private static final RenderLayer DUNGEON_BORDER_COLOR_MASK = createLockedDungeonBorder(false, false);

    @Unique
    private static final RenderLayer DUNGEON_BORDER_ALL_MASK = createLockedDungeonBorder(true, false);

    @Unique
    private static final RenderLayer DUNGEON_BORDER_COLOR_MASK_CULLING = createLockedDungeonBorder(false, true);

    @Unique
    private static final RenderLayer DUNGEON_BORDER_ALL_MASK_CULLING = createLockedDungeonBorder(true, true);

    @Unique
    private static final Identifier FORCEFIELD_TEXTURE = Identifier.ofVanilla("textures/misc/forcefield.png");

    @Unique
    private static RenderLayer getDungeonBorder(boolean allMask, boolean isCullingEnabled) {
        if (allMask) {
            return isCullingEnabled ? DUNGEON_BORDER_ALL_MASK_CULLING : DUNGEON_BORDER_ALL_MASK;
        }
        return isCullingEnabled ? DUNGEON_BORDER_COLOR_MASK_CULLING : DUNGEON_BORDER_COLOR_MASK;
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
            target = "Lnet/minecraft/client/render/WorldBorderRendering;render(Lnet/minecraft/world/border/WorldBorder;Lnet/minecraft/util/math/Vec3d;DD)V",
            shift = At.Shift.AFTER
        )
    )
    public void renderLockedDungeonBoxes(
        Fog fog,
        float f,
        Vec3d cameraPos,
        int maxVisibleDistance,
        float farPlaneDistance,
        CallbackInfo ci
    ) {
        RenderLayer renderLayer = null;
        for (List<Box> lockedDungeonBoxes : AchieveToDoClient.getLockedDungeons().values()) {
            for (Box box : lockedDungeonBoxes) {
                double closestX = Math.clamp(cameraPos.x, box.minX, box.maxX);
                double closestY = Math.clamp(cameraPos.y, box.minY, box.maxY);
                double closestZ = Math.clamp(cameraPos.z, box.minZ, box.maxZ);
                double distance = cameraPos.distanceTo(new Vec3d(closestX, closestY, closestZ));
                if (distance >= maxVisibleDistance) {
                    continue;
                }
                if (renderLayer == null) {
                    RenderSystem.setShaderTexture(0, FORCEFIELD_TEXTURE);
                    renderLayer = getDungeonBorder(
                        MinecraftClient.isFabulousGraphicsOrBetter(),
                        !box.contains(cameraPos)
                    );
                    renderLayer.startDrawing();
                }
                double alpha = 1.0f;
                double fadeStart = maxVisibleDistance * 0.5f;
                if (distance > fadeStart) {
                    alpha -= (distance - fadeStart) / (maxVisibleDistance - fadeStart);
                }
                int yellowRGB = DyeColor.YELLOW.getEntityColor();
                float r = ((yellowRGB >> 16) & 0xFF) / 255.0F;
                float g = ((yellowRGB >> 8) & 0xFF) / 255.0F;
                float b = (yellowRGB & 0xFF) / 255.0F;
                RenderSystem.setShaderColor(r, g, b, (float) alpha);
                float animationTime = (float) (Util.getMeasuringTimeMs() % 3000L) / 3000.0F;

                BufferBuilder bufferBuilder = Tessellator.getInstance()
                    .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

                float minX = (float) (box.minX - cameraPos.x);
                float maxX = (float) (box.maxX - cameraPos.x);
                float minY = (float) (box.minY - cameraPos.y);
                float maxY = (float) (box.maxY - cameraPos.y);
                float minZ = (float) (box.minZ - cameraPos.z);
                float maxZ = (float) (box.maxZ - cameraPos.z);

                if (cameraPos.x > box.maxX - maxVisibleDistance) {
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
                if (cameraPos.x < box.minX + maxVisibleDistance) {
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
                if (cameraPos.z > box.maxZ - maxVisibleDistance) {
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
                if (cameraPos.z < box.minZ + maxVisibleDistance) {
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
                if (cameraPos.y < box.minY + maxVisibleDistance) {
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
                if (cameraPos.y > box.maxY - maxVisibleDistance) {
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

                BuiltBuffer builtBuffer = bufferBuilder.endNullable();
                if (builtBuffer != null) {
                    BufferRenderer.drawWithGlobalProgram(builtBuffer);
                }
            }
        }
        if (renderLayer != null) {
            renderLayer.endDrawing();
            RenderSystem.disableCull();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
