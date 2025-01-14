package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.client.Utils;
import com.diskree.achievetodo.injection.extension.main.LevelInfoExtension;
import com.diskree.achievetodo.server.Constants;
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
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class MainMixin {

    @WrapOperation(
        method = "method_43613(Lcom/mojang/serialization/Dynamic;Ljoptsimple/OptionSet;Ljoptsimple/OptionSpec;Lnet/minecraft/server/dedicated/ServerPropertiesLoader;Ljoptsimple/OptionSpec;Lnet/minecraft/server/SaveLoading$LoadContextSupplierContext;)Lnet/minecraft/server/SaveLoading$LoadContext;",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;Lnet/minecraft/world/GameMode;ZLnet/minecraft/world/Difficulty;ZLnet/minecraft/world/GameRules;Lnet/minecraft/resource/DataConfiguration;)Lnet/minecraft/world/level/LevelInfo;"
        )
    )
    private static LevelInfo readConfigNameFromServerProperties(
        String name,
        GameMode gameMode,
        boolean hardcore,
        Difficulty difficulty,
        boolean allowCommands,
        GameRules gameRules,
        DataConfiguration dataConfiguration,
        @NotNull Operation<LevelInfo> original,
        @Local @NotNull ServerPropertiesHandler serverPropertiesHandler
    ) {
        String configName = serverPropertiesHandler.getString(Constants.ConfigKey.SERVER_CONFIG_PROPERTY_NAME, "");
        if (TextUtils.isEmpty(configName)) {
            throw new IllegalStateException(
                "You must set " + Constants.ConfigKey.SERVER_CONFIG_PROPERTY_NAME +
                    " with selected configuration in your `server.properties` file!" +
                    " Check out the `Server-side setup` section in the mod description: " +
                    Utils.buildModrinthModUrl(BuildConfig.MOD_ID)
            );
        }
        LevelInfo levelInfo = original.call(
            name, gameMode, hardcore, difficulty, allowCommands, gameRules, dataConfiguration
        );
        if (levelInfo instanceof LevelInfoExtension levelInfoExtension) {
            levelInfoExtension.achievetodo$setConfigName(configName);
        }
        return levelInfo;
    }
}
