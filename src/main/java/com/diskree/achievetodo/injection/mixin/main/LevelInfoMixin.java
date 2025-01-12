package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.ChaosProgressionGenerator;
import com.diskree.achievetodo.ability.ProgressionModeType;
import com.diskree.achievetodo.ability.Progressions;
import com.diskree.achievetodo.injection.extension.main.LevelInfoExtension;
import com.diskree.achievetodo.server.Constants;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moandjiezana.toml.Toml;
import com.mojang.serialization.Dynamic;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.LevelInfo;
import org.apache.http.util.TextUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Mixin(LevelInfo.class)
public abstract class LevelInfoMixin implements LevelInfoExtension {

    @Unique
    private String configName;

    @Override
    public String achievetodo$getConfigName() {
        return configName;
    }

    @Override
    public void achievetodo$setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public Map<AbilityType, Integer> achievetodo$getAbilitiesConfiguration(long seed) {
        if (TextUtils.isEmpty(configName)) {
            throw new IllegalStateException("Configuration name missing from level.dat!");
        }
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(BuildConfig.MOD_ID);
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new RuntimeException("Creating config directory", e);
            }
        }
        ProgressionModeType progressionModeType = ProgressionModeType.findByName(configName);
        String fileName = configName;
        if (progressionModeType == ProgressionModeType.CHAOS) {
            fileName += "_" + seed;
        }
        Path configFile = configDir.resolve(fileName + Constants.FileExtension.TOML);
        Map<AbilityType, Integer> abilitiesConfiguration = new HashMap<>();
        Map<String, Object> abilitiesMap = null;
        if (progressionModeType != null) {
            if (Files.exists(configFile)) {
                try {
                    Toml configToml = new Toml().read(configFile.toFile());
                    if (configToml.getLong(Constants.ConfigKey.VERSION) >= progressionModeType.getVersion()) {
                        Toml abilitiesTable = configToml.getTable(Constants.ConfigKey.ABILITIES_TABLE);
                        if (abilitiesTable != null) {
                            abilitiesMap = abilitiesTable.toMap();
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (abilitiesMap == null) {
                StringBuilder configTomlContents = new StringBuilder()
                    .append(Constants.ConfigKey.VERSION + " = ")
                    .append(progressionModeType.getVersion())
                    .append("\n\n")
                    .append("[" + Constants.ConfigKey.ABILITIES_TABLE + "]")
                    .append("\n");
                Map<AbilityType, Integer> progression = switch (progressionModeType) {
                    case EASY -> Progressions.getEasyProgression();
                    case NORMAL -> Progressions.getNormalProgression();
                    case HARD -> Progressions.getHardProgression();
                    case CHAOS -> ChaosProgressionGenerator.generateChaosProgression(seed);
                };
                for (AbilityType abilityType : AbilityType.values()) {
                    Integer requiredCount = progression.get(abilityType);
                    if (requiredCount == null) {
                        throw new RuntimeException("Ability " + abilityType +
                            " is missing in progression mode: " + progressionModeType.getName());
                    }
                    abilitiesConfiguration.put(abilityType, requiredCount);
                    configTomlContents
                        .append(abilityType.getName())
                        .append(" = ")
                        .append(requiredCount)
                        .append("\n");
                }
                try {
                    Files.writeString(configFile, configTomlContents.toString());
                } catch (IOException e) {
                    throw new RuntimeException("Creating config", e);
                }
                return abilitiesConfiguration;
            }
        } else {
            if (!Files.exists(configFile)) {
                throw new IllegalArgumentException("Config " + configFile + " not found!");
            }
            try {
                abilitiesMap = new Toml()
                    .read(configFile.toFile())
                    .getTable(Constants.ConfigKey.ABILITIES_TABLE)
                    .toMap();
            } catch (Exception e) {
                throw new RuntimeException("Reading config", e);
            }
        }
        for (String abilityName : abilitiesMap.keySet()) {
            AbilityType abilityType = AbilityType.findByName(abilityName);
            if (abilityType != null) {
                int requiredCount = Math.toIntExact(((Long) abilitiesMap.get(abilityName)));
                if (requiredCount > Constants.TOTAL_ADVANCEMENTS_COUNT) {
                    requiredCount = Constants.TOTAL_ADVANCEMENTS_COUNT;
                }
                abilitiesConfiguration.put(abilityType, requiredCount);
            }
        }
        for (AbilityType abilityType : AbilityType.values()) {
            abilitiesConfiguration.putIfAbsent(abilityType, 0);
        }
        return abilitiesConfiguration;
    }

    @ModifyReturnValue(
        method = "fromDynamic",
        at = @At("RETURN")
    )
    private static LevelInfo readConfigName(
        LevelInfo levelInfo,
        @Local(argsOnly = true) Dynamic<?> dynamic
    ) {
        if (levelInfo instanceof LevelInfoExtension levelInfoExtension) {
            levelInfoExtension.achievetodo$setConfigName(dynamic.get(Constants.NbtKey.LEVEL_CONFIG_NAME).asString(""));
        }
        return levelInfo;
    }

    @ModifyReturnValue(
        method = {
            "withGameMode",
            "withDifficulty",
            "withDataConfiguration",
            "withCopiedGameRules"
        },
        at = @At("RETURN")
    )
    private LevelInfo keepConfigNameOnRecreate(LevelInfo levelInfo) {
        if (levelInfo instanceof LevelInfoExtension levelInfoExtension) {
            levelInfoExtension.achievetodo$setConfigName(configName);
        }
        return levelInfo;
    }
}
