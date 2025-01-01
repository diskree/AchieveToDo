package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.DifficultyType;
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
    private static final double CHAOS_UNLOCKED_BY_DEFAULT_CHANCE_PERCENT = 1.0;

    @Unique
    private static final double CHAOS_PERMANENTLY_LOCKED_CHANCE_PERCENT = 0.1;

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
                for (AbilityType ability : AbilityType.values()) {
                    int requiredAdvancementsCount;
                    if (difficulty == DifficultyType.CHAOS) {
                        double unlockedByDefaultChance = ability.canBeUnlockedByDefaultInChaos() ?
                            CHAOS_UNLOCKED_BY_DEFAULT_CHANCE_PERCENT : 0;
                        double permanentlyLockedChance = ability.canBePermanentlyLockedInChaos() ?
                            CHAOS_PERMANENTLY_LOCKED_CHANCE_PERCENT : 0;
                        double roll = chaosRandom.nextDouble() * 100.0;
                        if (roll < permanentlyLockedChance) {
                            requiredAdvancementsCount = -1;
                        } else if (roll < permanentlyLockedChance + unlockedByDefaultChance) {
                            requiredAdvancementsCount = 0;
                        } else {
                            requiredAdvancementsCount = ability.getRequiredAdvancementsCountInChaos(chaosRandom);
                        }
                    } else {
                        requiredAdvancementsCount = ability.getRequiredAdvancementsCount(difficulty);
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
                abilitiesConfiguration.put(ability, Math.toIntExact(((Long) abilitiesMap.get(abilityName))));
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
            levelInfoExtension.achievetodo$setConfigName(dynamic.get(Constants.CONFIG_NAME_LEVEL_NBT_KEY).asString(""));
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
