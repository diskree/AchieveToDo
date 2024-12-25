package com.diskree.achievetodo;

import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.injection.ArmorItemImpl;
import com.diskree.achievetodo.injection.MiningToolItemImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.*;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum AbilityType {

    VISION(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 2,
        Items.ENDER_EYE
    ),
    EAT_SALMON(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.SALMON
    ),
    EAT_COD(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.COD
    ),
    EAT_TROPICAL_FISH(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.TROPICAL_FISH
    ),
    JUMP(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 3,
        Items.SLIME_BLOCK
    ),
    SWIM(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 1,
        Items.HEART_OF_THE_SEA
    ),
    EAT_ROTTEN_FLESH(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.ROTTEN_FLESH
    ),
    EAT_SPIDER_EYE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.SPIDER_EYE
    ),
    EAT_SWEET_BERRIES(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.SWEET_BERRIES
    ),
    OPEN_DOOR(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 1,
        Items.PALE_OAK_DOOR
    ),
    EAT_GLOW_BERRIES(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.GLOW_BERRIES
    ),
    EAT_PUFFERFISH(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.PUFFERFISH
    ),
    SLEEP(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 1,
        Items.LIGHT_GRAY_BED
    ),
    EAT_POISONOUS_POTATO(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 1,
        FoodComponents.POISONOUS_POTATO
    ),
    SPRINT(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 1,
        Items.CHAINMAIL_BOOTS
    ),
    SNEAK(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 2,
        Items.CHAINMAIL_LEGGINGS
    ),
    OPEN_INVENTORY(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.MAIN, 2,
        Items.LIGHT_GRAY_BUNDLE
    ),
    BREAK_BLOCKS(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 2,
        Items.COBBLESTONE
    ),
    EAT_SUSPICIOUS_STEW(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 2,
        FoodComponents.SUSPICIOUS_STEW
    ),
    OPEN_BARREL(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 2,
        Blocks.BARREL
    ),
    EAT_BEETROOT(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 2,
        FoodComponents.BEETROOT
    ),
    EAT_CARROT(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.CARROT
    ),
    EQUIP_GOLDEN_ARMOR(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 5,
        ArmorMaterials.GOLD
    ),
    EAT_CHICKEN(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.CHICKEN
    ),
    EAT_DRIED_KELP(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.DRIED_KELP
    ),
    OPEN_CRAFTING_TABLE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 5,
        Blocks.CRAFTING_TABLE
    ),
    USE_GOLDEN_TOOLS(
        AbilityUnlockedToastType.TOOL,
        AbilitiesBranchType.UPGRADE, 5,
        ToolMaterial.GOLD
    ),
    EAT_POTATO(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.POTATO
    ),
    OPEN_STONECUTTER(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 5,
        Blocks.STONECUTTER
    ),
    GET_INTO_BOAT(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 5,
        Items.PALE_OAK_BOAT
    ),
    EAT_APPLE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.APPLE
    ),
    OPEN_CHEST(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 5,
        Blocks.CHEST
    ),
    USE_SHIELD(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 5,
        Items.SHIELD
    ),
    EAT_MELON_SLICE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.MELON_SLICE
    ),
    OPEN_FURNACE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 5,
        Blocks.FURNACE
    ),
    EAT_COOKIE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.COOKIE
    ),
    EAT_MUSHROOM_STEW(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.MUSHROOM_STEW
    ),
    EAT_BEETROOT_SOUP(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.BEETROOT_SOUP
    ),
    PUT_IN_BUNDLE(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 5,
        Items.BUNDLE
    ),
    EAT_RABBIT_STEW(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.RABBIT_STEW
    ),
    OPEN_TRAPDOOR(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 5,
        Items.PALE_OAK_TRAPDOOR
    ),
    USE_WOODEN_TOOLS(
        AbilityUnlockedToastType.TOOL,
        AbilitiesBranchType.UPGRADE, 5,
        ToolMaterial.WOOD
    ),
    EAT_HONEY(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.HONEY_BOTTLE
    ),
    USE_WATER_BUCKET(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 5,
        Items.WATER_BUCKET
    ),
    EAT_MUTTON(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.MUTTON
    ),
    THROW_SNOWBALL(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 5,
        Items.SNOWBALL
    ),
    EQUIP_LEATHER_ARMOR(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 5,
        ArmorMaterials.LEATHER
    ),
    OPEN_FENCE_GATE(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 5,
        Items.PALE_OAK_FENCE_GATE
    ),
    EAT_PUMPKIN_PIE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.PUMPKIN_PIE
    ),
    EAT_GOLDEN_APPLE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 5,
        FoodComponents.GOLDEN_APPLE
    ),
    USE_SHEARS(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 5,
        Items.SHEARS
    ),
    BREAK_BLOCKS_IN_NEGATIVE_Y(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 10,
        Items.COBBLED_DEEPSLATE
    ),
    EAT_ENCHANTED_GOLDEN_APPLE(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.ENCHANTED_GOLDEN_APPLE
    ),
    THROW_EGG(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.EGG
    ),
    USE_STONE_TOOLS(
        AbilityUnlockedToastType.TOOL,
        AbilitiesBranchType.UPGRADE, 10,
        ToolMaterial.STONE
    ),
    OPEN_GRINDSTONE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.GRINDSTONE
    ),
    SHOOT_CROSSBOW(
        AbilityUnlockedToastType.WEAPON,
        AbilitiesBranchType.UPGRADE, 10,
        Items.CROSSBOW
    ),
    EAT_RABBIT(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.RABBIT
    ),
    EQUIP_CHAINMAIL_ARMOR(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 10,
        ArmorMaterials.CHAIN
    ),
    OPEN_ANVIL(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.ANVIL
    ),
    USE_FLINT_AND_STEEL(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.FLINT_AND_STEEL
    ),
    IGNITE_TNT(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.TNT
    ),
    ENTER_NETHER(
        AbilityUnlockedToastType.PORTAL,
        AbilitiesBranchType.UPGRADE, 10,
        NetherPortalBlock.class
    ),
    TRADE_WITH_WANDERING_TRADER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        Items.WANDERING_TRADER_SPAWN_EGG
    ),
    GET_INTO_MINECART(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.MINECART
    ),
    USE_OMINOUS_BOTTLE(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.OMINOUS_BOTTLE
    ),
    USE_IRON_TOOLS(
        AbilityUnlockedToastType.TOOL,
        AbilitiesBranchType.UPGRADE, 10,
        ToolMaterial.IRON
    ),
    ATTACK_WITH_TRIDENT(
        AbilityUnlockedToastType.WEAPON,
        AbilitiesBranchType.UPGRADE, 10,
        Items.TRIDENT
    ),
    EQUIP_IRON_ARMOR(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 10,
        ArmorMaterials.IRON
    ),
    SHOOT_BOW(
        AbilityUnlockedToastType.WEAPON,
        AbilitiesBranchType.UPGRADE, 10,
        Items.BOW
    ),
    USE_JUKEBOX(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.JUKEBOX
    ),
    THROW_ENDER_PEARL(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.ENDER_PEARL
    ),
    USE_COMPOSTER(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.COMPOSTER
    ),
    CHARGE_RESPAWN_ANCHOR(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.RESPAWN_ANCHOR
    ),
    EAT_BEEF(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.BEEF
    ),
    TRADE_WITH_MASON(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.MASON
    ),
    USE_FISHING_ROD(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.FISHING_ROD
    ),
    EAT_PORKCHOP(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.PORKCHOP
    ),
    TRADE_WITH_CARTOGRAPHER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.CARTOGRAPHER
    ),
    USE_DIAMOND_TOOLS(
        AbilityUnlockedToastType.TOOL,
        AbilitiesBranchType.UPGRADE, 10,
        ToolMaterial.DIAMOND
    ),
    USE_CAULDRON(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.CAULDRON
    ),
    EAT_BAKED_POTATO(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.BAKED_POTATO
    ),
    OPEN_SMOKER(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.SMOKER
    ),
    EQUIP_TURTLE_HELMET(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 10,
        Items.TURTLE_HELMET
    ),
    USE_BRUSH(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.BRUSH
    ),
    EQUIP_DIAMOND_ARMOR(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 10,
        ArmorMaterials.DIAMOND
    ),
    UNLOCK_VAULT(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.VAULT
    ),
    OPEN_BLAST_FURNACE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.BLAST_FURNACE
    ),
    TRADE_WITH_LEATHERWORKER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.LEATHERWORKER
    ),
    USE_SPYGLASS(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.SPYGLASS
    ),
    OPEN_BEACON(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.BEACON
    ),
    THROW_WIND_CHARGE(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.WIND_CHARGE
    ),
    ENTER_END(
        AbilityUnlockedToastType.PORTAL,
        AbilitiesBranchType.UPGRADE, 10,
        EndPortalBlock.class
    ),
    OPEN_CARTOGRAPHY_TABLE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.CARTOGRAPHY_TABLE
    ),
    EAT_COOKED_SALMON(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_SALMON
    ),
    EQUIP_ELYTRA(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 10,
        Items.ELYTRA
    ),
    TRADE_WITH_SHEPHERD(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.SHEPHERD
    ),
    TRADE_WITH_BUTCHER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.BUTCHER
    ),
    OPEN_ENDER_CHEST(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.ENDER_CHEST
    ),
    ATTACK_WITH_MACE(
        AbilityUnlockedToastType.WEAPON,
        AbilitiesBranchType.UPGRADE, 10,
        Items.MACE
    ),
    USE_ENDER_EYE(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.ENDER_EYE
    ),
    TELEPORT_OUTER_ISLANDS(
        AbilityUnlockedToastType.PORTAL,
        AbilitiesBranchType.UPGRADE, 10,
        EndGatewayBlock.class
    ),
    USE_NETHERITE_TOOLS(
        AbilityUnlockedToastType.TOOL,
        AbilitiesBranchType.UPGRADE, 10,
        ToolMaterial.NETHERITE
    ),
    EAT_COOKED_COD(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_COD
    ),
    TRADE_WITH_FARMER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.FARMER
    ),
    GLIDE_WITH_FIREWORKS(
        AbilityUnlockedToastType.ACTION,
        AbilitiesBranchType.ACTIONS, 10,
        Items.FIREWORK_ROCKET
    ),
    TRADE_WITH_CLERIC(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.CLERIC
    ),
    EQUIP_NETHERITE_ARMOR(
        AbilityUnlockedToastType.EQUIPMENT,
        AbilitiesBranchType.UPGRADE, 10,
        ArmorMaterials.NETHERITE
    ),
    OPEN_BREWING_STAND(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.BREWING_STAND
    ),
    EAT_COOKED_RABBIT(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_RABBIT
    ),
    PLACE_END_CRYSTAL(
        AbilityUnlockedToastType.ITEM,
        AbilitiesBranchType.ACTIONS, 10,
        Items.END_CRYSTAL
    ),
    TRADE_WITH_FISHERMAN(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.FISHERMAN
    ),
    OPEN_SMITHING_TABLE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.SMITHING_TABLE
    ),
    EAT_COOKED_CHICKEN(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_CHICKEN
    ),
    EAT_CHORUS_FRUIT(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.CHORUS_FRUIT
    ),
    TRADE_WITH_FLETCHER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.FLETCHER
    ),
    EAT_COOKED_MUTTON(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_MUTTON
    ),
    TRADE_WITH_ARMORER(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.ARMORER
    ),
    EAT_COOKED_PORKCHOP(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_PORKCHOP
    ),
    EAT_BREAD(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.BREAD
    ),
    TRADE_WITH_WEAPONSMITH(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.WEAPONSMITH
    ),
    EAT_COOKED_BEEF(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.COOKED_BEEF
    ),
    OPEN_LOOM(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.LOOM
    ),
    USE_CAMPFIRE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.CAMPFIRE
    ),
    OPEN_SHULKER_BOX(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.LIGHT_GRAY_SHULKER_BOX
    ),
    EAT_GOLDEN_CARROT(
        AbilityUnlockedToastType.FOOD,
        AbilitiesBranchType.FOOD, 10,
        FoodComponents.GOLDEN_CARROT
    ),
    TRADE_WITH_TOOLSMITH(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.TOOLSMITH
    ),
    TRADE_WITH_LIBRARIAN(
        AbilityUnlockedToastType.TRADING,
        AbilitiesBranchType.TRADING, 10,
        VillagerProfession.LIBRARIAN
    ),
    OPEN_ENCHANTING_TABLE(
        AbilityUnlockedToastType.BLOCK,
        AbilitiesBranchType.BLOCKS, 10,
        Blocks.ENCHANTING_TABLE
    );

    private int requiredAdvancementsCount;

    private final int requiredAdvancementsIncrementalValue;
    private final AbilitiesBranchType branchType;
    private final AbilityUnlockedToastType unlockToastType;
    private final Item item;
    private final Block block;
    private final FoodComponent food;
    private final ToolMaterial toolMaterial;
    private final ArmorMaterial equipmentMaterial;
    private final Class<? extends Portal> portal;
    private final VillagerProfession villager;

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        Item item
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            item,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        Block block
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            null,
            null,
            block,
            null,
            null,
            null,
            null
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        FoodComponent food
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            null,
            food,
            null,
            null,
            null,
            null,
            null
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        ToolMaterial toolMaterial
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            null,
            null,
            null,
            toolMaterial,
            null,
            null,
            null
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        ArmorMaterial armorMaterial
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            null,
            null,
            null,
            null,
            armorMaterial,
            null,
            null
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        Class<? extends Portal> portal
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            null,
            null,
            null,
            null,
            null,
            portal,
            null
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        VillagerProfession villager
    ) {
        this(
            unlockToastType,
            branchType,
            requiredAdvancementsIncrementalValue,
            null,
            null,
            null,
            null,
            null,
            null,
            villager
        );
    }

    AbilityType(
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        int requiredAdvancementsIncrementalValue,
        Item item,
        FoodComponent food,
        Block block,
        ToolMaterial toolMaterial,
        ArmorMaterial equipmentMaterial,
        Class<? extends Portal> portal,
        VillagerProfession villager
    ) {
        this.branchType = branchType;
        this.unlockToastType = unlockToastType;
        this.requiredAdvancementsIncrementalValue = requiredAdvancementsIncrementalValue;
        this.item = item;
        this.food = food;
        this.block = block;
        this.toolMaterial = toolMaterial;
        this.equipmentMaterial = equipmentMaterial;
        this.portal = portal;
        this.villager = villager;
    }

    static {
        int totalCount = 0;
        for (AbilityType ability : AbilityType.values()) {
            totalCount += ability.requiredAdvancementsIncrementalValue;
            ability.requiredAdvancementsCount = totalCount;
        }
    }

    public AbilitiesBranchType getBranchType() {
        return branchType;
    }

    public AbilityUnlockedToastType getUnlockToastType() {
        return unlockToastType;
    }

    public Text getLockedMessage(int obtainedAdvancementsCount) {
        return Text.translatable("achievetodo.ability." + getLowerCaseName() + ".locked_message").copy()
            .append(Text.of("." + (FabricLoader.getInstance().isModLoaded("multilineactionbar") ? "\n" : " ")))
            .append(Text.translatable("achievetodo.ability.left_to_unlock"))
            .append(Text.of(String.valueOf(getRequiredAdvancementsCount() - obtainedAdvancementsCount)))
            .formatted(Formatting.YELLOW);
    }

    public int getRequiredAdvancementsCount() {
        return requiredAdvancementsCount;
    }

    public @NotNull String getLowerCaseName() {
        return name().toLowerCase();
    }

    @Nullable
    public Item getIcon() {
        if (item != null) {
            return item;
        }
        if (block != null) {
            return block.asItem();
        }
        if (food != null) {
            return Registries.ITEM.stream()
                .filter(item -> item.getComponents().get(DataComponentTypes.FOOD) == food)
                .findFirst()
                .orElseThrow();
        }
        if (toolMaterial != null) {
            return Registries.ITEM.stream()
                .filter(item -> item instanceof PickaxeItem pickaxeItem &&
                    pickaxeItem instanceof MiningToolItemImpl miningToolItem &&
                    miningToolItem.achievetodo$getMaterial() == toolMaterial
                )
                .findFirst()
                .orElse(null);
        }
        if (equipmentMaterial != null) {
            return Registries.ITEM.stream()
                .filter(item -> item instanceof ArmorItemImpl chestPlateItem &&
                    chestPlateItem.achievetodo$getEquipmentType() == EquipmentType.CHESTPLATE &&
                    chestPlateItem.achievetodo$getMaterial() == equipmentMaterial
                )
                .findFirst()
                .orElse(null);
        }
        if (portal != null) {
            return switch (this) {
                case ENTER_NETHER -> Items.OBSIDIAN;
                case ENTER_END -> Items.END_PORTAL_FRAME;
                case TELEPORT_OUTER_ISLANDS -> Items.CHORUS_FLOWER;
                default -> null;
            };
        }
        if (villager != null) {
            PointOfInterestType poi = Registries.POINT_OF_INTEREST_TYPE.get(Identifier.ofVanilla(villager.id()));
            if (poi != null && poi.blockStates() != null) {
                List<BlockState> blockStates = new ArrayList<>(poi.blockStates());
                BlockState blockState = blockStates.getFirst();
                if (blockState != null) {
                    return blockState.getBlock().asItem();
                }
            }
        }
        throw new IllegalStateException("Ability " + this + " haven't icon!");
    }

    public @NotNull Text getTitle() {
        return Text.translatable("achievetodo.ability." + getLowerCaseName() + ".name");
    }

    public @NotNull Text getDescription() {
        return Text.translatable("achievetodo.ability." + getLowerCaseName() + ".description");
    }

    public static AbilityType findByName(String name) {
        if (name == null) {
            return null;
        }
        for (AbilityType ability : values()) {
            if (ability.name().equalsIgnoreCase(name)) {
                return ability;
            }
        }
        return null;
    }

    public static AbilityType findByAdvancement(@NotNull PlacedAdvancement advancement) {
        return findByAdvancement(advancement.getAdvancementEntry());
    }

    public static AbilityType findByAdvancement(@NotNull AdvancementEntry advancement) {
        return findByAdvancementId(advancement.id());
    }

    public static @Nullable AbilityType findByAdvancementId(Identifier advancementId) {
        if (advancementId == null || !BuildConfig.MOD_ID.equals(advancementId.getNamespace())) {
            return null;
        }
        String path = advancementId.getPath();
        if (path.startsWith(AbilityAdvancementsGenerator.ABILITY_PATH_PREFIX)) {
            return findByName(path.split(AbilityAdvancementsGenerator.ABILITY_PATH_PREFIX)[1]);
        }
        return null;
    }

    public static @Nullable AbilityType findByAdvancementRequirements(
        @NotNull AdvancementRequirements advancementRequirements
    ) {
        if (advancementRequirements.requirements().size() != 2) {
            return null;
        }
        String abilityName = null;
        boolean isUnlockedCriterionFound = false;
        for (List<String> requirement : advancementRequirements.requirements()) {
            if (requirement.size() != 1) {
                return null;
            }
            String criteriaName = requirement.getFirst();
            if (abilityName == null &&
                criteriaName.startsWith(AbilityAdvancementsGenerator.DEMYSTIFIED_CRITERION_PREFIX)
            ) {
                abilityName = criteriaName.split(AbilityAdvancementsGenerator.DEMYSTIFIED_CRITERION_PREFIX)[1];
            } else if (!isUnlockedCriterionFound &&
                criteriaName.equals(AbilityAdvancementsGenerator.UNLOCKED_CRITERION)
            ) {
                isUnlockedCriterionFound = true;
            } else {
                return null;
            }
        }
        if (abilityName == null || !isUnlockedCriterionFound) {
            return null;
        }
        return findByName(abilityName);
    }

    @Nullable
    public static AbilityType findEatFoodAbility(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
        if (foodComponent == null) {
            return null;
        }
        for (AbilityType ability : values()) {
            if (foodComponent == ability.food) {
                return ability;
            }
        }
        return null;
    }

    @Nullable
    public static AbilityType findToolMaterialUsageAbility(ToolMaterial toolMaterial) {
        if (toolMaterial == null) {
            return null;
        }
        for (AbilityType ability : values()) {
            if (toolMaterial == ability.toolMaterial) {
                return ability;
            }
        }
        return null;
    }

    @Nullable
    public static AbilityType findEquipmentEquipAbility(Item item) {
        if (item == null) {
            return null;
        }
        if (item == Items.TURTLE_HELMET) {
            return EQUIP_TURTLE_HELMET;
        }
        if (item == Items.ELYTRA) {
            return EQUIP_ELYTRA;
        }
        if (item instanceof ArmorItemImpl armorItem) {
            for (AbilityType ability : values()) {
                if (armorItem.achievetodo$getMaterial() == ability.equipmentMaterial) {
                    return ability;
                }
            }
        }
        return null;
    }

    @Nullable
    public static AbilityType findPortalTeleportAbility(Portal portal) {
        if (portal == null) {
            return null;
        }
        for (AbilityType ability : values()) {
            if (portal.getClass() == ability.portal) {
                return ability;
            }
        }
        return null;
    }

    @Nullable
    public static AbilityType findVillagerTradeAbility(VillagerProfession profession) {
        if (profession == null) {
            return null;
        }
        for (AbilityType ability : values()) {
            if (profession == ability.villager) {
                return ability;
            }
        }
        return null;
    }
}
