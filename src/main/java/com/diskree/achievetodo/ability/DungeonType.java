package com.diskree.achievetodo.ability;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public enum DungeonType {

    RUINED_PORTAL(World.OVERWORLD, StructureKeys.RUINED_PORTAL),
    ANCIENT_CITY(World.OVERWORLD, StructureKeys.ANCIENT_CITY),
    MONUMENT(World.OVERWORLD, StructureKeys.MONUMENT),
    DESERT_WELL(World.OVERWORLD, Feature.DESERT_WELL),
    ;

    public static final Set<Feature<?>> FEATURES = new HashSet<>();

    private final RegistryKey<World> dimension;
    private final RegistryKey<Structure> structure;
    private final Feature<?> feature;

    static {
        for (DungeonType dungeon : values()) {
            if (dungeon.isFeature()) {
                FEATURES.add(dungeon.getFeature());
            }
        }
    }

    DungeonType(RegistryKey<World> dimension, RegistryKey<Structure> structure) {
        this(dimension, structure, null);
    }

    DungeonType(RegistryKey<World> dimension, Feature<?> feature) {
        this(dimension, null, feature);
    }

    DungeonType(RegistryKey<World> dimension, RegistryKey<Structure> structure, Feature<?> feature) {
        this.dimension = dimension;
        this.structure = structure;
        this.feature = feature;
    }

    public boolean isInOverworld() {
        return dimension == World.OVERWORLD;
    }

    public boolean isInNether() {
        return dimension == World.NETHER;
    }

    public boolean isInEnd() {
        return dimension == World.END;
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
    public static DungeonType findByStructure(RegistryKey<Structure> structure) {
        for (DungeonType type : values()) {
            if (type.isStructure() && type.getStructure() == structure) {
                return type;
            }
        }
        return null;
    }

    @Nullable
    public static DungeonType findByFeature(Feature<?> feature) {
        for (DungeonType type : values()) {
            if (type.isFeature() && type.getFeature() == feature) {
                return type;
            }
        }
        return null;
    }
}
