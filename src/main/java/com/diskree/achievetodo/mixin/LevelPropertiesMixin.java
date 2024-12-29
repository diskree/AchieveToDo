package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.Constants;
import com.diskree.achievetodo.injection.LevelInfoImpl;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelProperties.class)
public class LevelPropertiesMixin {

    @Shadow
    private LevelInfo levelInfo;

    @Inject(
        method = "updateProperties",
        at = @At("RETURN")
    )
    private void saveConfigName(
        DynamicRegistryManager registryManager,
        NbtCompound levelNbt,
        NbtCompound playerNbt,
        CallbackInfo ci
    ) {
        if (levelInfo instanceof LevelInfoImpl levelInfoImpl) {
            String configName = levelInfoImpl.achievetodo$getConfigName();
            if (configName != null) {
                levelNbt.putString(Constants.CONFIG_NAME_LEVEL_NBT_KEY, configName);
            }
        }
    }
}
