package com.diskree.achievetodo.ability;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.generation.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.client.gui.AbilityUnlockedToastType;
import com.diskree.achievetodo.client.gui.DesignCodePalette;
import com.diskree.achievetodo.injection.extension.main.ArmorItemExtension;
import com.diskree.achievetodo.injection.extension.main.MiningToolItemExtension;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public enum AbilityType {

    VISION(
        1, 1, 2,
        1, 2,
        true, false,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.ENDER_EYE
    ),
    EAT_SALMON(
        2, 2, 3,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SALMON
    ),
    EAT_COD(
        3, 3, 4,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COD
    ),
    EAT_TROPICAL_FISH(
        5, 5, 5,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.TROPICAL_FISH
    ),
    JUMP(
        8, 8, 8,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.SLIME_BLOCK
    ),
    SWIM(
        9, 9, 9,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.HEART_OF_THE_SEA
    ),
    EAT_ROTTEN_FLESH(
        10, 10, 10,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.ROTTEN_FLESH
    ),
    EAT_SPIDER_EYE(
        11, 11, 11,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SPIDER_EYE
    ),
    EAT_SWEET_BERRIES(
        12, 12, 12,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SWEET_BERRIES
    ),
    OPEN_DOOR(
        13, 13, 13,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_DOOR
    ),
    EAT_GLOW_BERRIES(
        14, 14, 14,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.GLOW_BERRIES
    ),
    EAT_PUFFERFISH(
        15, 15, 15,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.PUFFERFISH
    ),
    SLEEP(
        16, 16, 16,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.LIGHT_GRAY_BED
    ),
    EAT_POISONOUS_POTATO(
        17, 17, 17,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.POISONOUS_POTATO
    ),
    SPRINT(
        18, 18, 18,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.CHAINMAIL_BOOTS
    ),
    SNEAK(
        20, 20, 20,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.CHAINMAIL_LEGGINGS
    ),
    OPEN_INVENTORY(
        22, 22, 22,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.LIGHT_GRAY_BUNDLE
    ),
    BREAK_BLOCKS(
        24, 24, 24,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.COBBLESTONE
    ),
    EAT_SUSPICIOUS_STEW(
        26, 26, 26,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SUSPICIOUS_STEW
    ),
    OPEN_BARREL(
        28, 28, 28,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BARREL
    ),
    EAT_BEETROOT(
        30, 30, 30,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BEETROOT
    ),
    EAT_CARROT(
        35, 35, 35,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.CARROT
    ),
    EQUIP_GOLDEN_ARMOR(
        40, 40, 40,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.GOLD
    ),
    EAT_CHICKEN(
        45, 45, 45,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.CHICKEN
    ),
    EAT_DRIED_KELP(
        50, 50, 50,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.DRIED_KELP
    ),
    OPEN_CRAFTING_TABLE(
        55, 55, 55,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CRAFTING_TABLE
    ),
    USE_GOLDEN_TOOLS(
        60, 60, 60,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.GOLD
    ),
    EAT_POTATO(
        65, 65, 65,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.POTATO
    ),
    OPEN_STONECUTTER(
        70, 70, 70,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.STONECUTTER
    ),
    GET_INTO_BOAT(
        75, 75, 75,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_BOAT
    ),
    EAT_APPLE(
        80, 80, 80,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.APPLE
    ),
    OPEN_CHEST(
        85, 85, 85,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CHEST
    ),
    USE_SHIELD(
        90, 90, 90,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, Items.SHIELD
    ),
    EAT_MELON_SLICE(
        95, 95, 95,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.MELON_SLICE
    ),
    OPEN_FURNACE(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.FURNACE
    ),
    EAT_COOKIE(
        105, 105, 105,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKIE
    ),
    EAT_MUSHROOM_STEW(
        110, 110, 110,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.MUSHROOM_STEW
    ),
    EAT_BEETROOT_SOUP(
        115, 115, 115,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BEETROOT_SOUP
    ),
    PUT_IN_BUNDLE(
        120, 120, 120,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.BUNDLE
    ),
    EAT_RABBIT_STEW(
        125, 125, 125,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.RABBIT_STEW
    ),
    OPEN_TRAPDOOR(
        130, 130, 130,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_TRAPDOOR
    ),
    USE_WOODEN_TOOLS(
        135, 135, 135,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.WOOD
    ),
    EAT_HONEY(
        140, 140, 140,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.HONEY_BOTTLE
    ),
    USE_WATER_BUCKET(
        145, 145, 145,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.WATER_BUCKET
    ),
    EAT_MUTTON(
        150, 150, 150,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.MUTTON
    ),
    THROW_SNOWBALL(
        155, 155, 155,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.SNOWBALL
    ),
    EQUIP_LEATHER_ARMOR(
        160, 160, 160,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.LEATHER
    ),
    OPEN_FENCE_GATE(
        165, 165, 165,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_FENCE_GATE
    ),
    EAT_PUMPKIN_PIE(
        170, 170, 170,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.PUMPKIN_PIE
    ),
    EAT_GOLDEN_APPLE(
        175, 175, 175,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.GOLDEN_APPLE
    ),
    USE_SHEARS(
        180, 180, 180,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.SHEARS
    ),
    BREAK_BLOCKS_IN_NEGATIVE_Y(
        190, 190, 190,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.COBBLED_DEEPSLATE
    ),
    EAT_ENCHANTED_GOLDEN_APPLE(
        200, 200, 200,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.ENCHANTED_GOLDEN_APPLE
    ),
    THROW_EGG(
        210, 210, 210,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.EGG
    ),
    USE_STONE_TOOLS(
        220, 220, 220,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.STONE
    ),
    OPEN_GRINDSTONE(
        230, 230, 230,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.GRINDSTONE
    ),
    SHOOT_CROSSBOW(
        240, 240, 240,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.CROSSBOW
    ),
    EAT_RABBIT(
        250, 250, 250,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.RABBIT
    ),
    EQUIP_CHAINMAIL_ARMOR(
        260, 260, 260,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.CHAIN
    ),
    OPEN_ANVIL(
        270, 270, 270,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.ANVIL
    ),
    USE_FLINT_AND_STEEL(
        280, 280, 280,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.FLINT_AND_STEEL
    ),
    IGNITE_TNT(
        290, 290, 290,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.TNT
    ),
    ENTER_NETHER(
        300, 300, 300,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.PORTAL, AbilitiesTreeCategoryType.UPGRADE, NetherPortalBlock.class
    ),
    TRADE_WITH_WANDERING_TRADER(
        310, 310, 310,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, Items.WANDERING_TRADER_SPAWN_EGG
    ),
    GET_INTO_MINECART(
        320, 320, 320,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.MINECART
    ),
    USE_OMINOUS_BOTTLE(
        330, 330, 330,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.OMINOUS_BOTTLE
    ),
    USE_IRON_TOOLS(
        340, 340, 340,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.IRON
    ),
    ATTACK_WITH_TRIDENT(
        350, 350, 350,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.TRIDENT
    ),
    EQUIP_IRON_ARMOR(
        360, 360, 360,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.IRON
    ),
    SHOOT_BOW(
        370, 370, 370,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.BOW
    ),
    USE_JUKEBOX(
        380, 380, 380,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.JUKEBOX
    ),
    THROW_ENDER_PEARL(
        390, 390, 390,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.ENDER_PEARL
    ),
    USE_COMPOSTER(
        400, 400, 400,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.COMPOSTER
    ),
    CHARGE_RESPAWN_ANCHOR(
        410, 410, 410,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.RESPAWN_ANCHOR
    ),
    EAT_BEEF(
        420, 420, 420,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BEEF
    ),
    TRADE_WITH_MASON(
        430, 430, 430,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.MASON
    ),
    USE_FISHING_ROD(
        440, 440, 440,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.FISHING_ROD
    ),
    EAT_PORKCHOP(
        450, 450, 450,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.PORKCHOP
    ),
    TRADE_WITH_CARTOGRAPHER(
        460, 460, 460,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.CARTOGRAPHER
    ),
    USE_DIAMOND_TOOLS(
        470, 470, 470,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.DIAMOND
    ),
    USE_CAULDRON(
        480, 480, 480,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CAULDRON
    ),
    EAT_BAKED_POTATO(
        490, 490, 490,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BAKED_POTATO
    ),
    OPEN_SMOKER(
        500, 500, 500,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.SMOKER
    ),
    EQUIP_TURTLE_HELMET(
        510, 510, 510,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, Items.TURTLE_HELMET
    ),
    USE_BRUSH(
        520, 520, 520,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.BRUSH
    ),
    EQUIP_DIAMOND_ARMOR(
        530, 530, 530,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.DIAMOND
    ),
    UNLOCK_VAULT(
        540, 540, 540,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.VAULT
    ),
    OPEN_BLAST_FURNACE(
        550, 550, 550,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BLAST_FURNACE
    ),
    TRADE_WITH_LEATHERWORKER(
        560, 560, 560,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.LEATHERWORKER
    ),
    USE_SPYGLASS(
        570, 570, 570,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.SPYGLASS
    ),
    OPEN_BEACON(
        580, 580, 580,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BEACON
    ),
    THROW_WIND_CHARGE(
        590, 590, 590,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.WIND_CHARGE
    ),
    ENTER_END(
        600, 600, 600,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.PORTAL, AbilitiesTreeCategoryType.UPGRADE, EndPortalBlock.class
    ),
    OPEN_CARTOGRAPHY_TABLE(
        610, 610, 610,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CARTOGRAPHY_TABLE
    ),
    EAT_COOKED_SALMON(
        620, 620, 620,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_SALMON
    ),
    EQUIP_ELYTRA(
        630, 630, 630,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, Items.ELYTRA
    ),
    TRADE_WITH_SHEPHERD(
        640, 640, 640,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.SHEPHERD
    ),
    TRADE_WITH_BUTCHER(
        650, 650, 650,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.BUTCHER
    ),
    OPEN_ENDER_CHEST(
        660, 660, 660,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.ENDER_CHEST
    ),
    ATTACK_WITH_MACE(
        670, 670, 670,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.MACE
    ),
    USE_ENDER_EYE(
        680, 680, 680,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.ENDER_EYE
    ),
    TELEPORT_OUTER_ISLANDS(
        690, 690, 690,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.PORTAL, AbilitiesTreeCategoryType.UPGRADE, EndGatewayBlock.class
    ),
    USE_NETHERITE_TOOLS(
        700, 700, 700,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.NETHERITE
    ),
    EAT_COOKED_COD(
        710, 710, 710,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_COD
    ),
    TRADE_WITH_FARMER(
        720, 720, 720,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.FARMER
    ),
    GLIDE_WITH_FIREWORKS(
        730, 730, 730,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.FIREWORK_ROCKET
    ),
    TRADE_WITH_CLERIC(
        740, 740, 740,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.CLERIC
    ),
    EQUIP_NETHERITE_ARMOR(
        750, 750, 750,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.NETHERITE
    ),
    OPEN_BREWING_STAND(
        760, 760, 760,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BREWING_STAND
    ),
    EAT_COOKED_RABBIT(
        770, 770, 770,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_RABBIT
    ),
    PLACE_END_CRYSTAL(
        780, 780, 780,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.END_CRYSTAL
    ),
    TRADE_WITH_FISHERMAN(
        790, 790, 790,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.FISHERMAN
    ),
    OPEN_SMITHING_TABLE(
        800, 800, 800,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.SMITHING_TABLE
    ),
    EAT_COOKED_CHICKEN(
        810, 810, 810,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_CHICKEN
    ),
    EAT_CHORUS_FRUIT(
        820, 820, 820,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.CHORUS_FRUIT
    ),
    TRADE_WITH_FLETCHER(
        830, 830, 830,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.FLETCHER
    ),
    EAT_COOKED_MUTTON(
        840, 840, 840,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_MUTTON
    ),
    TRADE_WITH_ARMORER(
        850, 850, 850,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.ARMORER
    ),
    EAT_COOKED_PORKCHOP(
        860, 860, 860,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_PORKCHOP
    ),
    EAT_BREAD(
        870, 870, 870,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BREAD
    ),
    TRADE_WITH_WEAPONSMITH(
        880, 880, 880,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.WEAPONSMITH
    ),
    EAT_COOKED_BEEF(
        890, 890, 890,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_BEEF
    ),
    OPEN_LOOM(
        900, 900, 900,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.LOOM
    ),
    USE_CAMPFIRE(
        910, 910, 910,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CAMPFIRE
    ),
    OPEN_SHULKER_BOX(
        920, 920, 920,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.LIGHT_GRAY_SHULKER_BOX
    ),
    EAT_GOLDEN_CARROT(
        930, 930, 930,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.GOLDEN_CARROT
    ),
    TRADE_WITH_TOOLSMITH(
        940, 940, 940,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.TOOLSMITH
    ),
    TRADE_WITH_LIBRARIAN(
        950, 950, 950,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.LIBRARIAN
    ),
    OPEN_ENCHANTING_TABLE(
        960, 960, 960,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.ENCHANTING_TABLE
    ),
    INTERACT_INSIDE_DESERT_PYRAMID(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.DESERT_PYRAMID
    ),
    INTERACT_INSIDE_DESERT_WELL(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.DESERT_WELL
    ),
    INTERACT_INSIDE_JUNGLE_PYRAMID(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.JUNGLE_PYRAMID
    ),
    INTERACT_INSIDE_PILLAGER_OUTPOST(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.PILLAGER_OUTPOST
    ),
    INTERACT_INSIDE_IGLOO(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.IGLOO
    ),
    INTERACT_INSIDE_SWAMP_HUT(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.SWAMP_HUT
    ),
    INTERACT_INSIDE_MANSION(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MANSION
    ),
    INTERACT_INSIDE_VILLAGE(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.VILLAGE
    ),
    INTERACT_INSIDE_RUINED_PORTAL(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.RUINED_PORTAL
    ),
    INTERACT_INSIDE_BURIED_TREASURE(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.BURIED_TREASURE
    ),
    INTERACT_INSIDE_SHIPWRECK(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.SHIPWRECK
    ),
    INTERACT_INSIDE_OCEAN_RUIN(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.OCEAN_RUIN
    ),
    INTERACT_INSIDE_MONUMENT(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MONUMENT
    ),
    INTERACT_INSIDE_MONSTER_ROOM(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MONSTER_ROOM
    ),
    INTERACT_INSIDE_MINESHAFT(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MINESHAFT
    ),
    INTERACT_INSIDE_TRAIL_RUINS(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.TRAIL_RUINS
    ),
    INTERACT_INSIDE_ANCIENT_CITY(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.ANCIENT_CITY
    ),
    INTERACT_INSIDE_TRIAL_CHAMBERS(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.TRIAL_CHAMBERS
    ),
    INTERACT_INSIDE_STRONGHOLD(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.STRONGHOLD
    ),
    INTERACT_INSIDE_FORTRESS(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.FORTRESS
    ),
    INTERACT_INSIDE_BASTION_REMNANT(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.BASTION_REMNANT
    ),
    INTERACT_INSIDE_END_CITY(
        100, 100, 100,
        1, 1000,
        true, true,
        AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.END_CITY
    );

    private final int easyCount, normalCount, hardCount;
    private final int chaosMinLimit, chaosMaxLimit;
    private final boolean canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos;
    private final AbilitiesTreeCategoryType category;
    private final AbilityUnlockedToastType unlockToastType;
    private final Item item;
    private final Block block;
    private final FoodComponent food;
    private final ToolMaterial toolMaterial;
    private final ArmorMaterial equipmentMaterial;
    private final Class<? extends Portal> portal;
    private final VillagerProfession villager;
    private final LandmarkType landmarkType;

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, Item item
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, item, null, null, null, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, Block block
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, null, block, null, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, FoodComponent food
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, food, null, null, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, ToolMaterial toolMaterial
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, null, null, toolMaterial, null, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, ArmorMaterial armorMaterial
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, null, null, null, armorMaterial, null, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, Class<? extends Portal> portal
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, null, null, null, null, portal, null, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, VillagerProfession villager
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, null, null, null, null, null, villager, null
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, LandmarkType landmarkType
    ) {
        this(
            easyCount, normalCount, hardCount,
            chaosMinLimit, chaosMaxLimit,
            canBeInitiallyUnlockedInChaos, canBePermanentlyLockedInChaos,
            unlockToastType, category, null, null, null, null, null, null, null, landmarkType
        );
    }

    AbilityType(
        int easyCount, int normalCount, int hardCount,
        int chaosMinLimit, int chaosMaxLimit,
        boolean canBeInitiallyUnlockedInChaos, boolean canBePermanentlyLockedInChaos,
        AbilityUnlockedToastType unlockToastType,
        AbilitiesTreeCategoryType category,
        Item item,
        FoodComponent food,
        Block block,
        ToolMaterial toolMaterial,
        ArmorMaterial equipmentMaterial,
        Class<? extends Portal> portal,
        VillagerProfession villager,
        LandmarkType landmarkType
    ) {
        this.easyCount = easyCount;
        this.normalCount = normalCount;
        this.hardCount = hardCount;
        this.chaosMinLimit = chaosMinLimit;
        this.chaosMaxLimit = chaosMaxLimit;
        this.canBeInitiallyUnlockedInChaos = canBeInitiallyUnlockedInChaos;
        this.canBePermanentlyLockedInChaos = canBePermanentlyLockedInChaos;
        this.category = category;
        this.unlockToastType = unlockToastType;
        this.item = item;
        this.food = food;
        this.block = block;
        this.toolMaterial = toolMaterial;
        this.equipmentMaterial = equipmentMaterial;
        this.portal = portal;
        this.villager = villager;
        this.landmarkType = landmarkType;
    }

    public int getRequiredAdvancementsCount(@NotNull DifficultyType difficultyType) {
        return switch (difficultyType) {
            case EASY -> easyCount;
            case NORMAL -> normalCount;
            case HARD -> hardCount;
            case CHAOS -> throw new IllegalArgumentException("Use getRequiredAdvancementsCountInChaos() instead");
        };
    }

    public boolean canBeInitiallyUnlockedInChaos() {
        return canBeInitiallyUnlockedInChaos;
    }

    public boolean canBePermanentlyLockedInChaos() {
        return canBePermanentlyLockedInChaos;
    }

    public int getRequiredAdvancementsCountInChaos(@NotNull Random random) {
        return chaosMinLimit + random.nextInt(chaosMaxLimit - chaosMinLimit + 1);
    }

    public AbilitiesTreeCategoryType getCategory() {
        return category;
    }

    public AbilityUnlockedToastType getUnlockToastType() {
        return unlockToastType;
    }

    public LandmarkType getLandmarkType() {
        return landmarkType;
    }

    public Text buildUnlockProgressMessage(int leftCount) {
        return buildLockedMessagePrefix()
            .append(AchieveToDoClient.translateModKey("ability.left_to_unlock", leftCount))
            .formatted(DesignCodePalette.TEXT_COLOR);
    }

    public Text buildPermanentlyLockedMessage() {
        return buildLockedMessagePrefix()
            .append(AchieveToDoClient.translateModKey("ability.permanently_locked"))
            .formatted(Formatting.RED);
    }

    private MutableText buildLockedMessagePrefix() {
        return AchieveToDoClient.translateModKey("ability." + getName() + ".locked_message")
            .append("." + (FabricLoader.getInstance().isModLoaded("multilineactionbar") ? "\n" : " "));
    }

    public @NotNull String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public @Nullable Item getIcon() {
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
                    pickaxeItem instanceof MiningToolItemExtension miningToolItem &&
                    miningToolItem.achievetodo$getMaterial() == toolMaterial
                )
                .findFirst()
                .orElse(null);
        }
        if (equipmentMaterial != null) {
            return Registries.ITEM.stream()
                .filter(item -> item instanceof ArmorItemExtension armorItemExtension &&
                    armorItemExtension.achievetodo$getEquipmentType() == EquipmentType.CHESTPLATE &&
                    armorItemExtension.achievetodo$getMaterial() == equipmentMaterial
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
        if (landmarkType != null) {
            return switch (landmarkType) {
                case DESERT_PYRAMID -> Items.CHISELED_SANDSTONE;
                case DESERT_WELL -> Items.SANDSTONE;
                case JUNGLE_PYRAMID -> Items.MOSSY_COBBLESTONE;
                case PILLAGER_OUTPOST -> Items.DARK_OAK_PLANKS;
                case IGLOO -> Items.SNOW_BLOCK;
                case SWAMP_HUT -> Items.CAULDRON;
                case MANSION -> Items.DARK_OAK_LOG;
                case VILLAGE -> Items.BELL;
                case RUINED_PORTAL -> Items.CRYING_OBSIDIAN;
                case BURIED_TREASURE -> Items.SAND;
                case SHIPWRECK -> Items.OAK_PLANKS;
                case OCEAN_RUIN -> Items.SEA_LANTERN;
                case MONUMENT -> Items.PRISMARINE;
                case MONSTER_ROOM -> Items.SPAWNER;
                case MINESHAFT -> Items.RAIL;
                case TRAIL_RUINS -> Items.SUSPICIOUS_GRAVEL;
                case ANCIENT_CITY -> Items.REINFORCED_DEEPSLATE;
                case TRIAL_CHAMBERS -> Items.TRIAL_SPAWNER;
                case STRONGHOLD -> Items.INFESTED_CRACKED_STONE_BRICKS;
                case FORTRESS -> Items.CHISELED_NETHER_BRICKS;
                case BASTION_REMNANT -> Items.GILDED_BLACKSTONE;
                case END_CITY -> Items.PURPUR_BLOCK;
            };
        }
        throw new IllegalStateException("Ability " + this + " haven't icon!");
    }

    public @NotNull Text getTitle() {
        return AchieveToDoClient.translateModKey("ability." + getName() + ".name");
    }

    public @NotNull Text getDescription() {
        return AchieveToDoClient.translateModKey("ability." + getName() + ".description");
    }

    public static @Nullable AbilityType findByName(String name) {
        if (TextUtils.isEmpty(name)) {
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

    public static @Nullable AbilityType findEatFoodAbility(ItemStack stack) {
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

    public static @Nullable AbilityType findToolMaterialUsageAbility(ToolMaterial toolMaterial) {
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

    public static @Nullable AbilityType findEquipmentEquipAbility(Item item) {
        if (item == null) {
            return null;
        }
        if (item == Items.TURTLE_HELMET) {
            return EQUIP_TURTLE_HELMET;
        }
        if (item == Items.ELYTRA) {
            return EQUIP_ELYTRA;
        }
        if (item instanceof ArmorItemExtension armorItemExtension) {
            for (AbilityType ability : values()) {
                if (armorItemExtension.achievetodo$getMaterial() == ability.equipmentMaterial) {
                    return ability;
                }
            }
        }
        return null;
    }

    public static @Nullable AbilityType findPortalTeleportAbility(Portal portal) {
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

    public static @Nullable AbilityType findVillagerTradeAbility(VillagerProfession profession) {
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

    public static @Nullable AbilityType findByLandmarkType(LandmarkType landmarkType) {
        if (landmarkType == null) {
            return null;
        }
        for (AbilityType ability : values()) {
            if (landmarkType == ability.landmarkType) {
                return ability;
            }
        }
        return null;
    }
}
