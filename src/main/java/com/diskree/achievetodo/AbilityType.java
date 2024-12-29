package com.diskree.achievetodo;

import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.injection.ArmorItemImpl;
import com.diskree.achievetodo.injection.MiningToolItemImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.AdvancementEntry;
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
        0, 0, 2,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.ENDER_EYE
    ),
    EAT_SALMON(
        2, 2, 3,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.SALMON
    ),
    EAT_COD(
        3, 3, 4,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COD
    ),
    EAT_TROPICAL_FISH(
        5, 5, 5,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.TROPICAL_FISH
    ),
    JUMP(
        8, 8, 8,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.SLIME_BLOCK
    ),
    SWIM(
        9, 9, 9,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.HEART_OF_THE_SEA
    ),
    EAT_ROTTEN_FLESH(
        10, 10, 10,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.ROTTEN_FLESH
    ),
    EAT_SPIDER_EYE(
        11, 11, 11,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.SPIDER_EYE
    ),
    EAT_SWEET_BERRIES(
        12, 12, 12,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.SWEET_BERRIES
    ),
    OPEN_DOOR(
        13, 13, 13,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.PALE_OAK_DOOR
    ),
    EAT_GLOW_BERRIES(
        14, 14, 14,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.GLOW_BERRIES
    ),
    EAT_PUFFERFISH(
        15, 15, 15,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.PUFFERFISH
    ),
    SLEEP(
        16, 16, 16,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.LIGHT_GRAY_BED
    ),
    EAT_POISONOUS_POTATO(
        17, 17, 17,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.POISONOUS_POTATO
    ),
    SPRINT(
        18, 18, 18,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.CHAINMAIL_BOOTS
    ),
    SNEAK(
        20, 20, 20,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.CHAINMAIL_LEGGINGS
    ),
    OPEN_INVENTORY(
        22, 22, 22,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.MAIN, Items.LIGHT_GRAY_BUNDLE
    ),
    BREAK_BLOCKS(
        24, 24, 24,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.COBBLESTONE
    ),
    EAT_SUSPICIOUS_STEW(
        26, 26, 26,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.SUSPICIOUS_STEW
    ),
    OPEN_BARREL(
        28, 28, 28,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.BARREL
    ),
    EAT_BEETROOT(
        30, 30, 30,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.BEETROOT
    ),
    EAT_CARROT(
        35, 35, 35,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.CARROT
    ),
    EQUIP_GOLDEN_ARMOR(
        40, 40, 40,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, ArmorMaterials.GOLD
    ),
    EAT_CHICKEN(
        45, 45, 45,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.CHICKEN
    ),
    EAT_DRIED_KELP(
        50, 50, 50,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.DRIED_KELP
    ),
    OPEN_CRAFTING_TABLE(
        55, 55, 55,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.CRAFTING_TABLE
    ),
    USE_GOLDEN_TOOLS(
        60, 60, 60,
        AbilityUnlockedToastType.TOOL, AbilitiesBranchType.UPGRADE, ToolMaterial.GOLD
    ),
    EAT_POTATO(
        65, 65, 65,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.POTATO
    ),
    OPEN_STONECUTTER(
        70, 70, 70,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.STONECUTTER
    ),
    GET_INTO_BOAT(
        75, 75, 75,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.PALE_OAK_BOAT
    ),
    EAT_APPLE(
        80, 80, 80,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.APPLE
    ),
    OPEN_CHEST(
        85, 85, 85,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.CHEST
    ),
    USE_SHIELD(
        90, 90, 90,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, Items.SHIELD
    ),
    EAT_MELON_SLICE(
        95, 95, 95,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.MELON_SLICE
    ),
    OPEN_FURNACE(
        100, 100, 100,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.FURNACE
    ),
    EAT_COOKIE(
        105, 105, 105,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKIE
    ),
    EAT_MUSHROOM_STEW(
        110, 110, 110,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.MUSHROOM_STEW
    ),
    EAT_BEETROOT_SOUP(
        115, 115, 115,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.BEETROOT_SOUP
    ),
    PUT_IN_BUNDLE(
        120, 120, 120,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.BUNDLE
    ),
    EAT_RABBIT_STEW(
        125, 125, 125,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.RABBIT_STEW
    ),
    OPEN_TRAPDOOR(
        130, 130, 130,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.PALE_OAK_TRAPDOOR
    ),
    USE_WOODEN_TOOLS(
        135, 135, 135,
        AbilityUnlockedToastType.TOOL, AbilitiesBranchType.UPGRADE, ToolMaterial.WOOD
    ),
    EAT_HONEY(
        140, 140, 140,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.HONEY_BOTTLE
    ),
    USE_WATER_BUCKET(
        145, 145, 145,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.WATER_BUCKET
    ),
    EAT_MUTTON(
        150, 150, 150,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.MUTTON
    ),
    THROW_SNOWBALL(
        155, 155, 155,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.SNOWBALL
    ),
    EQUIP_LEATHER_ARMOR(
        160, 160, 160,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, ArmorMaterials.LEATHER
    ),
    OPEN_FENCE_GATE(
        165, 165, 165,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.PALE_OAK_FENCE_GATE
    ),
    EAT_PUMPKIN_PIE(
        170, 170, 170,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.PUMPKIN_PIE
    ),
    EAT_GOLDEN_APPLE(
        175, 175, 175,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.GOLDEN_APPLE
    ),
    USE_SHEARS(
        180, 180, 180,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.SHEARS
    ),
    BREAK_BLOCKS_IN_NEGATIVE_Y(
        190, 190, 190,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.COBBLED_DEEPSLATE
    ),
    EAT_ENCHANTED_GOLDEN_APPLE(
        200, 200, 200,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.ENCHANTED_GOLDEN_APPLE
    ),
    THROW_EGG(
        210, 210, 210,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.EGG
    ),
    USE_STONE_TOOLS(
        220, 220, 220,
        AbilityUnlockedToastType.TOOL, AbilitiesBranchType.UPGRADE, ToolMaterial.STONE
    ),
    OPEN_GRINDSTONE(
        230, 230, 230,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.GRINDSTONE
    ),
    SHOOT_CROSSBOW(
        240, 240, 240,
        AbilityUnlockedToastType.WEAPON, AbilitiesBranchType.UPGRADE, Items.CROSSBOW
    ),
    EAT_RABBIT(
        250, 250, 250,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.RABBIT
    ),
    EQUIP_CHAINMAIL_ARMOR(
        260, 260, 260,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, ArmorMaterials.CHAIN
    ),
    OPEN_ANVIL(
        270, 270, 270,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.ANVIL
    ),
    USE_FLINT_AND_STEEL(
        280, 280, 280,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.FLINT_AND_STEEL
    ),
    IGNITE_TNT(
        290, 290, 290,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.TNT
    ),
    ENTER_NETHER(
        300, 300, 300,
        AbilityUnlockedToastType.PORTAL, AbilitiesBranchType.UPGRADE, NetherPortalBlock.class
    ),
    TRADE_WITH_WANDERING_TRADER(
        310, 310, 310,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, Items.WANDERING_TRADER_SPAWN_EGG
    ),
    GET_INTO_MINECART(
        320, 320, 320,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.MINECART
    ),
    USE_OMINOUS_BOTTLE(
        330, 330, 330,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.OMINOUS_BOTTLE
    ),
    USE_IRON_TOOLS(
        340, 340, 340,
        AbilityUnlockedToastType.TOOL, AbilitiesBranchType.UPGRADE, ToolMaterial.IRON
    ),
    ATTACK_WITH_TRIDENT(
        350, 350, 350,
        AbilityUnlockedToastType.WEAPON, AbilitiesBranchType.UPGRADE, Items.TRIDENT
    ),
    EQUIP_IRON_ARMOR(
        360, 360, 360,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, ArmorMaterials.IRON
    ),
    SHOOT_BOW(
        370, 370, 370,
        AbilityUnlockedToastType.WEAPON, AbilitiesBranchType.UPGRADE, Items.BOW
    ),
    USE_JUKEBOX(
        380, 380, 380,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.JUKEBOX
    ),
    THROW_ENDER_PEARL(
        390, 390, 390,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.ENDER_PEARL
    ),
    USE_COMPOSTER(
        400, 400, 400,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.COMPOSTER
    ),
    CHARGE_RESPAWN_ANCHOR(
        410, 410, 410,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.RESPAWN_ANCHOR
    ),
    EAT_BEEF(
        420, 420, 420,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.BEEF
    ),
    TRADE_WITH_MASON(
        430, 430, 430,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.MASON
    ),
    USE_FISHING_ROD(
        440, 440, 440,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.FISHING_ROD
    ),
    EAT_PORKCHOP(
        450, 450, 450,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.PORKCHOP
    ),
    TRADE_WITH_CARTOGRAPHER(
        460, 460, 460,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.CARTOGRAPHER
    ),
    USE_DIAMOND_TOOLS(
        470, 470, 470,
        AbilityUnlockedToastType.TOOL, AbilitiesBranchType.UPGRADE, ToolMaterial.DIAMOND
    ),
    USE_CAULDRON(
        480, 480, 480,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.CAULDRON
    ),
    EAT_BAKED_POTATO(
        490, 490, 490,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.BAKED_POTATO
    ),
    OPEN_SMOKER(
        500, 500, 500,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.SMOKER
    ),
    EQUIP_TURTLE_HELMET(
        510, 510, 510,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, Items.TURTLE_HELMET
    ),
    USE_BRUSH(
        520, 520, 520,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.BRUSH
    ),
    EQUIP_DIAMOND_ARMOR(
        530, 530, 530,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, ArmorMaterials.DIAMOND
    ),
    UNLOCK_VAULT(
        540, 540, 540,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.VAULT
    ),
    OPEN_BLAST_FURNACE(
        550, 550, 550,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.BLAST_FURNACE
    ),
    TRADE_WITH_LEATHERWORKER(
        560, 560, 560,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.LEATHERWORKER
    ),
    USE_SPYGLASS(
        570, 570, 570,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.SPYGLASS
    ),
    OPEN_BEACON(
        580, 580, 580,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.BEACON
    ),
    THROW_WIND_CHARGE(
        590, 590, 590,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.WIND_CHARGE
    ),
    ENTER_END(
        600, 600, 600,
        AbilityUnlockedToastType.PORTAL, AbilitiesBranchType.UPGRADE, EndPortalBlock.class
    ),
    OPEN_CARTOGRAPHY_TABLE(
        610, 610, 610,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.CARTOGRAPHY_TABLE
    ),
    EAT_COOKED_SALMON(
        620, 620, 620,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_SALMON
    ),
    EQUIP_ELYTRA(
        630, 630, 630,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, Items.ELYTRA
    ),
    TRADE_WITH_SHEPHERD(
        640, 640, 640,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.SHEPHERD
    ),
    TRADE_WITH_BUTCHER(
        650, 650, 650,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.BUTCHER
    ),
    OPEN_ENDER_CHEST(
        660, 660, 660,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.ENDER_CHEST
    ),
    ATTACK_WITH_MACE(
        670, 670, 670,
        AbilityUnlockedToastType.WEAPON, AbilitiesBranchType.UPGRADE, Items.MACE
    ),
    USE_ENDER_EYE(
        680, 680, 680,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.ENDER_EYE
    ),
    TELEPORT_OUTER_ISLANDS(
        690, 690, 690,
        AbilityUnlockedToastType.PORTAL, AbilitiesBranchType.UPGRADE, EndGatewayBlock.class
    ),
    USE_NETHERITE_TOOLS(
        700, 700, 700,
        AbilityUnlockedToastType.TOOL, AbilitiesBranchType.UPGRADE, ToolMaterial.NETHERITE
    ),
    EAT_COOKED_COD(
        710, 710, 710,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_COD
    ),
    TRADE_WITH_FARMER(
        720, 720, 720,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.FARMER
    ),
    GLIDE_WITH_FIREWORKS(
        730, 730, 730,
        AbilityUnlockedToastType.ACTION, AbilitiesBranchType.ACTIONS, Items.FIREWORK_ROCKET
    ),
    TRADE_WITH_CLERIC(
        740, 740, 740,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.CLERIC
    ),
    EQUIP_NETHERITE_ARMOR(
        750, 750, 750,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesBranchType.UPGRADE, ArmorMaterials.NETHERITE
    ),
    OPEN_BREWING_STAND(
        760, 760, 760,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.BREWING_STAND
    ),
    EAT_COOKED_RABBIT(
        770, 770, 770,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_RABBIT
    ),
    PLACE_END_CRYSTAL(
        780, 780, 780,
        AbilityUnlockedToastType.ITEM, AbilitiesBranchType.ACTIONS, Items.END_CRYSTAL
    ),
    TRADE_WITH_FISHERMAN(
        790, 790, 790,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.FISHERMAN
    ),
    OPEN_SMITHING_TABLE(
        800, 800, 800,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.SMITHING_TABLE
    ),
    EAT_COOKED_CHICKEN(
        810, 810, 810,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_CHICKEN
    ),
    EAT_CHORUS_FRUIT(
        820, 820, 820,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.CHORUS_FRUIT
    ),
    TRADE_WITH_FLETCHER(
        830, 830, 830,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.FLETCHER
    ),
    EAT_COOKED_MUTTON(
        840, 840, 840,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_MUTTON
    ),
    TRADE_WITH_ARMORER(
        850, 850, 850,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.ARMORER
    ),
    EAT_COOKED_PORKCHOP(
        860, 860, 860,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_PORKCHOP
    ),
    EAT_BREAD(
        870, 870, 870,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.BREAD
    ),
    TRADE_WITH_WEAPONSMITH(
        880, 880, 880,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.WEAPONSMITH
    ),
    EAT_COOKED_BEEF(
        890, 890, 890,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.COOKED_BEEF
    ),
    OPEN_LOOM(
        900, 900, 900,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.LOOM
    ),
    USE_CAMPFIRE(
        910, 910, 910,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.CAMPFIRE
    ),
    OPEN_SHULKER_BOX(
        920, 920, 920,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.LIGHT_GRAY_SHULKER_BOX
    ),
    EAT_GOLDEN_CARROT(
        930, 930, 930,
        AbilityUnlockedToastType.FOOD, AbilitiesBranchType.FOOD, FoodComponents.GOLDEN_CARROT
    ),
    TRADE_WITH_TOOLSMITH(
        940, 940, 940,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.TOOLSMITH
    ),
    TRADE_WITH_LIBRARIAN(
        950, 950, 950,
        AbilityUnlockedToastType.TRADING, AbilitiesBranchType.TRADING, VillagerProfession.LIBRARIAN
    ),
    OPEN_ENCHANTING_TABLE(
        960, 960, 960,
        AbilityUnlockedToastType.BLOCK, AbilitiesBranchType.BLOCKS, Blocks.ENCHANTING_TABLE
    );

    private final int easyCount, normalCount, hardCount;
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
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, Item item
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, item, null, null, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, Block block
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, null, null, block, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, FoodComponent food
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, null, food, null, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, ToolMaterial toolMaterial
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, null, null, null, toolMaterial, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, ArmorMaterial armorMaterial
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, null, null, null, null, armorMaterial, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, Class<? extends Portal> portal
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, null, null, null, null, null, portal, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType, AbilitiesBranchType branchType, VillagerProfession villager
    ) {
        this(
            easyCount, normalCount, hardCount,
            unlockToastType, branchType, null, null, null, null, null, null, villager
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        AbilityUnlockedToastType unlockToastType,
        AbilitiesBranchType branchType,
        Item item,
        FoodComponent food,
        Block block,
        ToolMaterial toolMaterial,
        ArmorMaterial equipmentMaterial,
        Class<? extends Portal> portal,
        VillagerProfession villager
    ) {
        this.easyCount = easyCount;
        this.normalCount = normalCount;
        this.hardCount = hardCount;
        this.branchType = branchType;
        this.unlockToastType = unlockToastType;
        this.item = item;
        this.food = food;
        this.block = block;
        this.toolMaterial = toolMaterial;
        this.equipmentMaterial = equipmentMaterial;
        this.portal = portal;
        this.villager = villager;
    }

    public int getRequiredAdvancementsCount(@NotNull DifficultyType difficultyType) {
        return switch (difficultyType) {
            case EASY -> easyCount;
            case NORMAL -> normalCount;
            case HARD -> hardCount;
        };
    }

    public AbilitiesBranchType getBranchType() {
        return branchType;
    }

    public AbilityUnlockedToastType getUnlockToastType() {
        return unlockToastType;
    }

    public Text getLockedMessage(int leftCount) {
        return Text.translatable("achievetodo.ability." + getLowerCaseName() + ".locked_message").copy()
            .append(Text.of("." + (FabricLoader.getInstance().isModLoaded("multilineactionbar") ? "\n" : " ")))
            .append(Text.translatable("achievetodo.ability.left_to_unlock"))
            .append(Text.of(String.valueOf(leftCount)))
            .formatted(Formatting.YELLOW);
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
        return findByAdvancement(advancement.id());
    }

    public static @Nullable AbilityType findByAdvancement(Identifier advancementId) {
        if (advancementId == null || !BuildConfig.MOD_ID.equals(advancementId.getNamespace())) {
            return null;
        }
        String path = advancementId.getPath();
        if (path.startsWith(AbilityAdvancementsGenerator.ABILITY_PATH_PREFIX)) {
            return findByName(path.split(AbilityAdvancementsGenerator.ABILITY_PATH_PREFIX)[1]);
        }
        return null;
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
