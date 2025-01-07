package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.DifficultyType;
import com.diskree.achievetodo.ability.generation.Difficulties;
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
import java.util.Random;

@Mixin(LevelInfo.class)
public abstract class LevelInfoMixin implements LevelInfoExtension {

    @Unique
    private static final String CONFIG_VERSION_KEY = "version";

    @Unique
    private static final String CONFIG_ABILITIES_KEY = "abilities";

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
            throw new IllegalStateException("Cannot get the config name of the level nbt!");
        }
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(BuildConfig.MOD_ID);
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new RuntimeException("Creating config directory", e);
            }
        }
        DifficultyType difficulty = DifficultyType.findByName(configName);
        String fileName = configName;
        Random chaosRandom = null;
        if (difficulty == DifficultyType.CHAOS) {
            fileName += "_" + seed;
            chaosRandom = new Random(seed);
        }
        Path configFile = configDir.resolve(fileName + Constants.FileExtension.TOML);
        Map<AbilityType, Integer> abilitiesConfiguration = new HashMap<>();
        Map<String, Object> abilitiesMap = null;
        if (difficulty != null) {
            if (Files.exists(configFile)) {
                try {
                    Toml configToml = new Toml().read(configFile.toFile());
                    if (configToml.getLong(CONFIG_VERSION_KEY) >= difficulty.getVersion()) {
                        Toml abilitiesTable = configToml.getTable(CONFIG_ABILITIES_KEY);
                        if (abilitiesTable != null) {
                            abilitiesMap = abilitiesTable.toMap();
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (abilitiesMap == null) {
                StringBuilder configTomlContents = new StringBuilder()
                    .append(CONFIG_VERSION_KEY + " = ")
                    .append(difficulty.getVersion())
                    .append("\n\n")
                    .append("[" + CONFIG_ABILITIES_KEY + "]")
                    .append("\n");
                Map<AbilityType, Integer> progression = switch (difficulty) {
                    case EASY -> Difficulties.createEasyProgression();
                    case NORMAL -> Difficulties.createNormalProgression();
                    case HARD -> Difficulties.createHardProgression();
                    case CHAOS -> Difficulties.createChaosProgression(chaosRandom);
                };
                for (AbilityType ability : AbilityType.values()) {
                    Integer requiredAdvancementsCount = progression.get(ability);
                    if (requiredAdvancementsCount == null) {
                        throw new RuntimeException("Ability " + ability + " is not set in difficulty " + difficulty);
                    }
                    abilitiesConfiguration.put(ability, requiredAdvancementsCount);
                    configTomlContents
                        .append(ability.getName())
                        .append(" = ")
                        .append(requiredAdvancementsCount)
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
                abilitiesMap = new Toml().read(configFile.toFile()).getTable(CONFIG_ABILITIES_KEY).toMap();
            } catch (Exception e) {
                throw new RuntimeException("Reading config", e);
            }
        }
        for (String abilityName : abilitiesMap.keySet()) {
            AbilityType ability = AbilityType.findByName(abilityName);
            if (ability != null) {
                int requiredAdvancementsCount = Math.toIntExact(((Long) abilitiesMap.get(abilityName)));
                if (requiredAdvancementsCount > Difficulties.MAX_REQUIRED_ADVANCEMENTS_COUNT) {
                    requiredAdvancementsCount = Difficulties.MAX_REQUIRED_ADVANCEMENTS_COUNT;
                }
                abilitiesConfiguration.put(ability, requiredAdvancementsCount);
            }
        }
        for (AbilityType ability : AbilityType.values()) {
            abilitiesConfiguration.putIfAbsent(ability, 0);
        }
        return abilitiesConfiguration;
    }

    @ModifyReturnValue(
        method = "fromDynamic",
        at = @At("RETURN")
    )
    private static LevelInfo readConfigName(LevelInfo levelInfo, @Local(argsOnly = true) Dynamic<?> dynamic) {
        if (levelInfo instanceof LevelInfoExtension levelInfoExtension) {
            levelInfoExtension.achievetodo$setConfigName(dynamic.get(Constants.NbtKey.LEVEL_CONFIG_NAME).asString(""));
        }
        return levelInfo;
    }

    @ModifyReturnValue(
        method = {
            "withCopiedGameRules",
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
