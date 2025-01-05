package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.client.gui.DesignCodePalette;
import com.diskree.achievetodo.client.gui.LockedLandmarkBox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
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
    private static final long LOCKED_LANDMARK_BORDER_ANIMATION_DURATION = TimeUnit.SECONDS.toMillis(6);

    @Unique
    private static final float ENTER_LOCKED_LANDMARK_BORDER_FADE_ALPHA_SPEED = 0.003f;

    @Unique
    private static final Identifier LOCKED_LANDMARK_BORDER_TEXTURE =
        Identifier.ofVanilla("textures/misc/forcefield.png");

    @Unique
    private LockedLandmarkBox lastLockedLandmarkBox = null;

    @Unique
    private float fadeInsideLockedLandmarkAlpha = 1.0f;

    @Unique
    private void renderLockedLandmarkBorderFloor(
        @NotNull BufferBuilder b,
        float minY,
        float x, float segmentX, float halfSegmentX,
        float z, float segmentZ, float halfSegmentZ,
        float textureU,
        float animationTime
    ) {
        b.vertex(x + segmentX, minY, z).texture(animationTime - textureU, animationTime + halfSegmentZ);
        b.vertex(x + segmentX, minY, z + segmentZ).texture(animationTime - textureU, animationTime);
        b.vertex(x, minY, z + segmentZ).texture(animationTime - textureU + halfSegmentX, animationTime);
        b.vertex(x, minY, z).texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentZ);
    }

    @Unique
    private void renderLockedLandmarkBorderRoof(
        @NotNull BufferBuilder b,
        float maxY,
        float x, float segmentX, float halfSegmentX,
        float z, float segmentZ, float halfSegmentZ,
        float textureU,
        float animationTime
    ) {
        b.vertex(x, maxY, z).texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentZ);
        b.vertex(x, maxY, z + segmentZ).texture(animationTime - textureU + halfSegmentX, animationTime);
        b.vertex(x + segmentX, maxY, z + segmentZ).texture(animationTime - textureU, animationTime);
        b.vertex(x + segmentX, maxY, z).texture(animationTime - textureU, animationTime + halfSegmentZ);
    }

    @Unique
    private void renderLockedLandmarkBorderNorthWall(
        @NotNull BufferBuilder b,
        float minZ,
        float x, float segmentX, float halfSegmentX,
        float y, float segmentY, float halfSegmentY,
        float textureU,
        float animationTime
    ) {
        b.vertex(x, y, minZ).texture(animationTime - textureU + halfSegmentX, animationTime);
        b.vertex(x, y + segmentY, minZ).texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentY);
        b.vertex(x + segmentX, y + segmentY, minZ).texture(animationTime - textureU, animationTime + halfSegmentY);
        b.vertex(x + segmentX, y, minZ).texture(animationTime - textureU, animationTime);
    }

    @Unique
    private void renderLockedLandmarkBorderSouthWall(
        @NotNull BufferBuilder b,
        float maxZ,
        float x, float segmentX, float halfSegmentX,
        float y, float segmentY, float halfSegmentY,
        float textureU,
        float animationTime
    ) {
        b.vertex(x, y, maxZ).texture(animationTime - textureU + halfSegmentX, animationTime);
        b.vertex(x + segmentX, y, maxZ).texture(animationTime - textureU, animationTime);
        b.vertex(x + segmentX, y + segmentY, maxZ).texture(animationTime - textureU, animationTime + halfSegmentY);
        b.vertex(x, y + segmentY, maxZ).texture(animationTime - textureU + halfSegmentX, animationTime + halfSegmentY);
    }

    @Unique
    private void renderLockedLandmarkBorderWestWall(
        @NotNull BufferBuilder b,
        float minX,
        float y, float segmentY, float halfSegmentY,
        float z, float segmentZ, float halfSegmentZ,
        float textureU,
        float animationTime
    ) {
        b.vertex(minX, y, z).texture(animationTime - textureU + halfSegmentZ, animationTime);
        b.vertex(minX, y, z + segmentZ).texture(animationTime - textureU, animationTime);
        b.vertex(minX, y + segmentY, z + segmentZ).texture(animationTime - textureU, animationTime + halfSegmentY);
        b.vertex(minX, y + segmentY, z).texture(animationTime - textureU + halfSegmentZ, animationTime + halfSegmentY);
    }

    @Unique
    private void renderLockedLandmarkBorderEastWall(
        @NotNull BufferBuilder b,
        float maxX,
        float y, float segmentY, float halfSegmentY,
        float z, float segmentZ, float halfSegmentZ,
        float textureU,
        float animationTime
    ) {
        b.vertex(maxX, y + segmentY, z).texture(animationTime - textureU + halfSegmentZ, animationTime + halfSegmentY);
        b.vertex(maxX, y + segmentY, z + segmentZ).texture(animationTime - textureU + halfSegmentZ, animationTime);
        b.vertex(maxX, y, z + segmentZ).texture(animationTime - textureU, animationTime);
        b.vertex(maxX, y, z).texture(animationTime - textureU, animationTime + halfSegmentY);
    }

    @Shadow
    private @Nullable ClientWorld world;

    @Shadow
    private Frustum frustum;

    @Inject(
        method = "method_62216",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V",
            shift = At.Shift.AFTER
        )
    )
    public void renderLockedLandmarkBorder(
        Fog fog,
        float tickDelta,
        Vec3d cameraPos,
        int viewDistance,
        float farPlaneDistance,
        CallbackInfo ci
    ) {
        if (world == null) {
            return;
        }
        DimensionType dimensionType = DimensionType.findByWorld(world.getRegistryKey());
        if (dimensionType == null) {
            return;
        }
        LockedLandmarkBox foundBox = null;
        for (LockedLandmarkBox lockedLandmarkBox : AchieveToDoClient.getLockedLandmarkBoxes()) {
            if (lockedLandmarkBox.dimension() != dimensionType) {
                continue;
            }
            Box box = lockedLandmarkBox.box();
            if (!box.contains(cameraPos)) {
                continue;
            }

            float minX = (float) box.minX;
            float minY = (float) box.minY;
            float minZ = (float) box.minZ;
            float maxX = (float) box.maxX;
            float maxY = (float) box.maxY;
            float maxZ = (float) box.maxZ;

            boolean isFloorVisible = frustum.intersectAab(minX, minY, minZ, maxX, minY, maxZ) < 0;
            boolean isRoofVisible = frustum.intersectAab(minX, maxY, minZ, maxX, maxY, maxZ) < 0;
            boolean isNorthWallVisible = frustum.intersectAab(minX, minY, minZ, maxX, maxY, minZ) < 0;
            boolean isSouthWallVisible = frustum.intersectAab(minX, minY, maxZ, maxX, maxY, maxZ) < 0;
            boolean isWestWallVisible = frustum.intersectAab(minX, minY, minZ, minX, maxY, maxZ) < 0;
            boolean isEastWallVisible = frustum.intersectAab(maxX, minY, minZ, maxX, maxY, maxZ) < 0;

            if (!isFloorVisible &&
                !isRoofVisible &&
                !isNorthWallVisible &&
                !isSouthWallVisible &&
                !isWestWallVisible &&
                !isEastWallVisible
            ) {
                continue;
            }

            minX -= (float) cameraPos.x;
            minY -= (float) cameraPos.y;
            minZ -= (float) cameraPos.z;
            maxX -= (float) cameraPos.x;
            maxY -= (float) cameraPos.y;
            maxZ -= (float) cameraPos.z;

            foundBox = lockedLandmarkBox;
            RenderSystem.setShaderTexture(0, LOCKED_LANDMARK_BORDER_TEXTURE);
            RenderLayer renderLayer = RenderLayer.getWorldBorder(MinecraftClient.isFabulousGraphicsOrBetter());
            renderLayer.startDrawing();
            float animationTime = (float) (Util.getMeasuringTimeMs() % LOCKED_LANDMARK_BORDER_ANIMATION_DURATION) /
                LOCKED_LANDMARK_BORDER_ANIMATION_DURATION;

            RenderSystem.setShaderColor(
                ColorHelper.getRedFloat(DesignCodePalette.IN_WORLD_RGB),
                ColorHelper.getGreenFloat(DesignCodePalette.IN_WORLD_RGB),
                ColorHelper.getBlueFloat(DesignCodePalette.IN_WORLD_RGB),
                fadeInsideLockedLandmarkAlpha
            );

            BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            if (isFloorVisible || isRoofVisible) {
                for (float z = minZ; z < maxZ; ) {
                    float segmentZ = Math.min(1.0f, maxZ - z);
                    float textureU = 0f;
                    for (float x = minX; x < maxX; ) {
                        float segmentX = Math.min(1.0f, maxX - x);
                        float halfSegmentX = segmentX * 0.5f;
                        float halfSegmentZ = segmentZ * 0.5f;
                        if (isFloorVisible) {
                            renderLockedLandmarkBorderFloor(
                                bufferBuilder,
                                minY,
                                x, segmentX, halfSegmentX,
                                z, segmentZ, halfSegmentZ,
                                textureU,
                                animationTime
                            );
                        }
                        if (isRoofVisible) {
                            renderLockedLandmarkBorderRoof(
                                bufferBuilder,
                                maxY,
                                x, segmentX, halfSegmentX,
                                z, segmentZ, halfSegmentZ,
                                textureU,
                                animationTime
                            );
                        }
                        x += segmentX;
                        textureU += 0.5f;
                    }
                    z += segmentZ;
                }
            }
            if (isNorthWallVisible || isSouthWallVisible || isWestWallVisible || isEastWallVisible) {
                for (float y = minY; y < maxY; ) {
                    float segmentY = Math.min(1.0f, maxY - y);
                    float textureU = 0f;
                    if (isNorthWallVisible || isSouthWallVisible) {
                        for (float x = minX; x < maxX; ) {
                            float segmentX = Math.min(1.0f, maxX - x);
                            float halfSegmentX = segmentX * 0.5f;
                            float halfSegmentY = segmentY * 0.5f;
                            if (isNorthWallVisible) {
                                renderLockedLandmarkBorderNorthWall(
                                    bufferBuilder,
                                    minZ,
                                    x, segmentX, halfSegmentX,
                                    y, segmentY, halfSegmentY,
                                    textureU,
                                    animationTime
                                );
                            }
                            if (isSouthWallVisible) {
                                renderLockedLandmarkBorderSouthWall(
                                    bufferBuilder,
                                    maxZ,
                                    x, segmentX, halfSegmentX,
                                    y, segmentY, halfSegmentY,
                                    textureU,
                                    animationTime
                                );
                            }
                            x += segmentX;
                            textureU += 0.5f;
                        }
                    }
                    if (isWestWallVisible || isEastWallVisible) {
                        for (float z = minZ; z < maxZ; ) {
                            float segmentZ = Math.min(1.0f, maxZ - z);
                            float halfSegmentZ = segmentZ * 0.5f;
                            float halfSegmentY = segmentY * 0.5f;
                            if (isWestWallVisible) {
                                renderLockedLandmarkBorderWestWall(
                                    bufferBuilder,
                                    minX,
                                    y, segmentY, halfSegmentY,
                                    z, segmentZ, halfSegmentZ,
                                    textureU,
                                    animationTime
                                );
                            }
                            if (isEastWallVisible) {
                                renderLockedLandmarkBorderEastWall(
                                    bufferBuilder,
                                    maxX,
                                    y, segmentY, halfSegmentY,
                                    z, segmentZ, halfSegmentZ,
                                    textureU,
                                    animationTime
                                );
                            }
                            z += segmentZ;
                            textureU += 0.5f;
                        }
                    }
                    y += segmentY;
                }
            }

            BuiltBuffer buffer = bufferBuilder.endNullable();
            if (buffer != null) {
                BufferRenderer.drawWithGlobalProgram(buffer);
            }
            renderLayer.endDrawing();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            break;
        }
        if (foundBox == null) {
            fadeInsideLockedLandmarkAlpha = 1.0f;
        } else if (foundBox.equals(lastLockedLandmarkBox)) {
            fadeInsideLockedLandmarkAlpha += ENTER_LOCKED_LANDMARK_BORDER_FADE_ALPHA_SPEED * tickDelta;
            if (fadeInsideLockedLandmarkAlpha > 1.0f) {
                fadeInsideLockedLandmarkAlpha = 1.0f;
            }
        } else {
            fadeInsideLockedLandmarkAlpha = 0.0f;
        }
        lastLockedLandmarkBox = foundBox;
    }
}
