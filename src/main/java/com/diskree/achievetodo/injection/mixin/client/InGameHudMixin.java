package com.diskree.achievetodo.injection.mixin.client;

import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Unique
    private static final double LOCKED_DUNGEON_VIGNETTE_VISIBLE_DISTANCE_THRESHOLD = 5;

    @WrapOperation(
        method = "renderVignetteOverlay",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/ColorHelper;fromFloats(FFFF)I",
            ordinal = 1
        )
    )
    private int renderLockedDungeonVignette(
        float alpha,
        float red,
        float green,
        float blue,
        Operation<Integer> original,
        @Local(argsOnly = true) @Nullable Entity entity
    ) {
        if (entity != null) {
            Vec3d entityPos = entity.getPos();
            float maxVignetteAlpha = 0.0f;

            for (LandmarkType landmarkType : AchieveToDoClient.getLockedLandmarkBoxes().keySet()) {
                if (entity.getWorld().getRegistryKey() != landmarkType.getDimension()) {
                    continue;
                }
                for (Box box : AchieveToDoClient.getLockedLandmarkBoxes().get(landmarkType)) {
                    if (box.contains(entityPos)) {
                        maxVignetteAlpha = 1.0f;
                        break;
                    }
                    double closestX = Math.clamp(entityPos.x, box.minX, box.maxX);
                    double closestY = Math.clamp(entityPos.y, box.minY, box.maxY);
                    double closestZ = Math.clamp(entityPos.z, box.minZ, box.maxZ);
                    double distance = Math.sqrt(entityPos.squaredDistanceTo(closestX, closestY, closestZ));
                    if (distance > 0 && distance < LOCKED_DUNGEON_VIGNETTE_VISIBLE_DISTANCE_THRESHOLD) {
                        float vignetteAlpha = (float)
                            ((LOCKED_DUNGEON_VIGNETTE_VISIBLE_DISTANCE_THRESHOLD - distance) /
                                LOCKED_DUNGEON_VIGNETTE_VISIBLE_DISTANCE_THRESHOLD);
                        maxVignetteAlpha = Math.max(maxVignetteAlpha, vignetteAlpha);
                        if (maxVignetteAlpha == 1.0f) {
                            break;
                        }
                    }
                }
            }
            if (maxVignetteAlpha > 0.0f) {
                red = 0.0F;
                green = blue = MathHelper.clamp(maxVignetteAlpha, 0.0F, 1.0F);
            }
        }
        return original.call(alpha, red, green, blue);
    }
}
