package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.server.Constants;
import com.diskree.achievetodo.injection.extension.main.LevelInfoExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.Main;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class MainMixin {

    @WrapOperation(
        method = "method_43613",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;Lnet/minecraft/world/GameMode;ZLnet/minecraft/world/Difficulty;ZLnet/minecraft/world/GameRules;Lnet/minecraft/resource/DataConfiguration;)Lnet/minecraft/world/level/LevelInfo;"
        )
    )
    private static LevelInfo readConfigName(
        String name,
        GameMode gameMode,
        boolean hardcore,
        Difficulty difficulty,
        boolean allowCommands,
        GameRules gameRules,
        DataConfiguration dataConfiguration,
        @NotNull Operation<LevelInfo> original,
        @Local ServerPropertiesHandler serverPropertiesHandler
    ) {
        LevelInfo levelInfo = original.call(
            name, gameMode, hardcore, difficulty, allowCommands, gameRules, dataConfiguration
        );
        if (levelInfo instanceof LevelInfoExtension levelInfoExtension) {
            levelInfoExtension.achievetodo$setConfigName(
                serverPropertiesHandler.getString(Constants.CONFIG_NAME_LEVEL_NBT_KEY, "")
            );
        }
        return levelInfo;
    }
}
