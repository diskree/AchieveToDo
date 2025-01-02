package com.diskree.achievetodo.ability;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public enum LandmarkType {

    IGLOO(World.OVERWORLD, StructureKeys.IGLOO),
    RUINED_PORTAL(World.OVERWORLD, StructureKeys.RUINED_PORTAL),
    ANCIENT_CITY(World.OVERWORLD, StructureKeys.ANCIENT_CITY),
    MONUMENT(World.OVERWORLD, StructureKeys.MONUMENT),
    PILLAGER_OUTPOST(World.OVERWORLD, StructureKeys.PILLAGER_OUTPOST),
    DESERT_WELL(World.OVERWORLD, Feature.DESERT_WELL),
    MONSTER_ROOM(World.OVERWORLD, Feature.MONSTER_ROOM)
    ;

    public static final Set<Feature<?>> FEATURES = new HashSet<>();

    private final RegistryKey<World> dimension;
    private final RegistryKey<Structure> structure;
    private final Feature<?> feature;

    static {
        for (LandmarkType landmark : values()) {
            if (landmark.isFeature()) {
                FEATURES.add(landmark.getFeature());
            }
        }
    }

    LandmarkType(RegistryKey<World> dimension, RegistryKey<Structure> structure) {
        this(dimension, structure, null);
    }

    LandmarkType(RegistryKey<World> dimension, Feature<?> feature) {
        this(dimension, null, feature);
    }

    LandmarkType(RegistryKey<World> dimension, RegistryKey<Structure> structure, Feature<?> feature) {
        this.dimension = dimension;
        this.structure = structure;
        this.feature = feature;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public boolean isStructure() {
        return structure != null;
    }

    public boolean isFeature() {
        return feature != null;
    }

    public RegistryKey<Structure> getStructure() {
        return structure;
    }

    public Feature<?> getFeature() {
        return feature;
    }

    @Nullable
    public static LandmarkType findByStructure(RegistryKey<Structure> structure) {
        for (LandmarkType type : values()) {
            if (type.isStructure() && type.getStructure() == structure) {
                return type;
            }
        }
        return null;
    }

    @Nullable
    public static LandmarkType findByFeature(Feature<?> feature) {
        for (LandmarkType type : values()) {
            if (type.isFeature() && type.getFeature() == feature) {
                return type;
            }
        }
        return null;
    }
}
