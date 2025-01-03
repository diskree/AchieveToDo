package com.diskree.achievetodo.ability;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum LandmarkType {

    DESERT_PYRAMID(
        World.OVERWORLD,
        StructureKeys.DESERT_PYRAMID
    ),
    DESERT_WELL(
        World.OVERWORLD,
        Feature.DESERT_WELL
    ),
    JUNGLE_PYRAMID(
        World.OVERWORLD,
        StructureKeys.JUNGLE_PYRAMID
    ),
    PILLAGER_OUTPOST(
        World.OVERWORLD,
        StructureKeys.PILLAGER_OUTPOST
    ),
    IGLOO(
        World.OVERWORLD,
        StructureKeys.IGLOO
    ),
    SWAMP_HUT(
        World.OVERWORLD,
        StructureKeys.SWAMP_HUT
    ),
    MANSION(
        World.OVERWORLD,
        StructureKeys.MANSION
    ),
    VILLAGE(
        World.OVERWORLD,
        StructureKeys.VILLAGE_PLAINS,
        StructureKeys.VILLAGE_DESERT,
        StructureKeys.VILLAGE_SAVANNA,
        StructureKeys.VILLAGE_SNOWY,
        StructureKeys.VILLAGE_TAIGA
    ),
    RUINED_PORTAL(
        World.OVERWORLD,
        StructureKeys.RUINED_PORTAL,
        StructureKeys.RUINED_PORTAL_DESERT,
        StructureKeys.RUINED_PORTAL_JUNGLE,
        StructureKeys.RUINED_PORTAL_SWAMP,
        StructureKeys.RUINED_PORTAL_MOUNTAIN,
        StructureKeys.RUINED_PORTAL_OCEAN
    ),
    BURIED_TREASURE(
        World.OVERWORLD,
        StructureKeys.BURIED_TREASURE
    ),
    SHIPWRECK(
        World.OVERWORLD,
        StructureKeys.SHIPWRECK,
        StructureKeys.SHIPWRECK_BEACHED
    ),
    OCEAN_RUIN(
        World.OVERWORLD,
        StructureKeys.OCEAN_RUIN_COLD,
        StructureKeys.OCEAN_RUIN_WARM
    ),
    MONUMENT(
        World.OVERWORLD,
        StructureKeys.MONUMENT
    ),
    MONSTER_ROOM(
        World.OVERWORLD,
        Feature.MONSTER_ROOM
    ),
    MINESHAFT(
        World.OVERWORLD,
        StructureKeys.MINESHAFT,
        StructureKeys.MINESHAFT_MESA
    ),
    TRAIL_RUINS(
        World.OVERWORLD,
        StructureKeys.TRAIL_RUINS
    ),
    ANCIENT_CITY(
        World.OVERWORLD,
        StructureKeys.ANCIENT_CITY
    ),
    TRIAL_CHAMBERS(
        World.OVERWORLD,
        StructureKeys.TRIAL_CHAMBERS
    ),
    STRONGHOLD(
        World.OVERWORLD,
        StructureKeys.STRONGHOLD
    ),

    FORTRESS(
        World.NETHER,
        StructureKeys.FORTRESS
    ),
    BASTION_REMNANT(
        World.NETHER,
        StructureKeys.BASTION_REMNANT
    ),

    END_CITY(
        World.END,
        StructureKeys.END_CITY
    );

    public static final Set<Feature<?>> FEATURES = new HashSet<>();

    private final RegistryKey<World> world;
    private final List<RegistryKey<Structure>> structures;
    private final Feature<?> feature;

    static {
        for (LandmarkType landmark : values()) {
            if (landmark.isFeature()) {
                FEATURES.add(landmark.getFeature());
            }
        }
    }

    @SafeVarargs
    LandmarkType(RegistryKey<World> world, RegistryKey<Structure>... structures) {
        this(world, null, Arrays.stream(structures).toList());
    }

    LandmarkType(RegistryKey<World> world, Feature<?> feature) {
        this(world, feature, null);
    }

    LandmarkType(RegistryKey<World> world, Feature<?> feature, List<RegistryKey<Structure>> structures) {
        this.world = world;
        this.structures = structures;
        this.feature = feature;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    public boolean isStructure() {
        return structures != null;
    }

    public boolean isFeature() {
        return feature != null;
    }

    public List<RegistryKey<Structure>> getStructures() {
        return structures;
    }

    public Feature<?> getFeature() {
        return feature;
    }

    @Nullable
    public static LandmarkType findByStructure(RegistryKey<Structure> structure) {
        for (LandmarkType type : values()) {
            if (type.isStructure() && type.getStructures().contains(structure)) {
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
