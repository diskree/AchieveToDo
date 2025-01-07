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

public enum AbilityType {

    VISION(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.ENDER_EYE),
    EAT_SALMON(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SALMON),
    EAT_COD(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COD),
    EAT_TROPICAL_FISH(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.TROPICAL_FISH),
    JUMP(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.SLIME_BLOCK),
    SWIM(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.HEART_OF_THE_SEA),
    EAT_ROTTEN_FLESH(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.ROTTEN_FLESH),
    EAT_SPIDER_EYE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SPIDER_EYE),
    EAT_SWEET_BERRIES(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SWEET_BERRIES),
    OPEN_DOOR(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_DOOR),
    EAT_GLOW_BERRIES(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.GLOW_BERRIES),
    INTERACT_INSIDE_VILLAGE(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.VILLAGE),
    SLEEP(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.LIGHT_GRAY_BED),
    EAT_POISONOUS_POTATO(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.POISONOUS_POTATO),
    SNEAK(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.CHAINMAIL_LEGGINGS),
    SPRINT(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.CHAINMAIL_BOOTS),
    OPEN_INVENTORY(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.MAIN, Items.LIGHT_GRAY_BUNDLE),
    BREAK_BLOCKS(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.COBBLESTONE),
    EAT_SUSPICIOUS_STEW(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.SUSPICIOUS_STEW),
    USE_GOLDEN_TOOLS(AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.GOLD),
    OPEN_CHEST(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CHEST),
    EAT_CHICKEN(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.CHICKEN),
    EAT_CARROT(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.CARROT),
    OPEN_CRAFTING_TABLE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CRAFTING_TABLE),
    EQUIP_GOLDEN_ARMOR(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.GOLD),
    OPEN_BARREL(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BARREL),
    EAT_BEETROOT(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BEETROOT),
    EAT_DRIED_KELP(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.DRIED_KELP),
    EAT_POTATO(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.POTATO),
    OPEN_STONECUTTER(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.STONECUTTER),
    GET_INTO_BOAT(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_BOAT),
    EAT_APPLE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.APPLE),
    INTERACT_INSIDE_SHIPWRECK(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.SHIPWRECK),
    USE_SHIELD(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, Items.SHIELD),
    EAT_MELON_SLICE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.MELON_SLICE),
    INTERACT_INSIDE_RUINED_PORTAL(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.RUINED_PORTAL),
    OPEN_FURNACE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.FURNACE),
    EAT_COOKIE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKIE),
    INTERACT_INSIDE_BURIED_TREASURE(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.BURIED_TREASURE),
    EAT_MUSHROOM_STEW(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.MUSHROOM_STEW),
    EAT_BEETROOT_SOUP(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BEETROOT_SOUP),
    PUT_IN_BUNDLE(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.BUNDLE),
    EAT_RABBIT_STEW(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.RABBIT_STEW),
    OPEN_TRAPDOOR(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_TRAPDOOR),
    USE_WOODEN_TOOLS(AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.WOOD),
    EAT_HONEY(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.HONEY_BOTTLE),
    INTERACT_INSIDE_IGLOO(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.IGLOO),
    USE_WATER_BUCKET(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.WATER_BUCKET),
    EAT_MUTTON(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.MUTTON),
    THROW_SNOWBALL(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.SNOWBALL),
    INTERACT_INSIDE_OCEAN_RUIN(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.OCEAN_RUIN),
    EQUIP_LEATHER_ARMOR(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.LEATHER),
    EAT_PUFFERFISH(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.PUFFERFISH),
    OPEN_FENCE_GATE(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.PALE_OAK_FENCE_GATE),
    EAT_PUMPKIN_PIE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.PUMPKIN_PIE),
    EAT_GOLDEN_APPLE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.GOLDEN_APPLE),
    USE_SHEARS(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.SHEARS),
    INTERACT_INSIDE_DESERT_PYRAMID(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.DESERT_PYRAMID),
    BREAK_BLOCKS_IN_NEGATIVE_Y(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.COBBLED_DEEPSLATE),
    EAT_ENCHANTED_GOLDEN_APPLE(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.ENCHANTED_GOLDEN_APPLE),
    INTERACT_INSIDE_MINESHAFT(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MINESHAFT),
    THROW_EGG(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.EGG),
    USE_STONE_TOOLS(AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.STONE),
    OPEN_GRINDSTONE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.GRINDSTONE),
    SHOOT_CROSSBOW(AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.CROSSBOW),
    EAT_RABBIT(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.RABBIT),
    INTERACT_INSIDE_SWAMP_HUT(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.SWAMP_HUT),
    EQUIP_CHAINMAIL_ARMOR(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.CHAIN),
    OPEN_ANVIL(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.ANVIL),
    USE_FLINT_AND_STEEL(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.FLINT_AND_STEEL),
    IGNITE_TNT(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.TNT),
    INTERACT_INSIDE_ANCIENT_CITY(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.ANCIENT_CITY),
    ENTER_NETHER(AbilityUnlockedToastType.PORTAL, AbilitiesTreeCategoryType.UPGRADE, NetherPortalBlock.class),
    TRADE_WITH_WANDERING_TRADER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, Items.WANDERING_TRADER_SPAWN_EGG),
    GET_INTO_MINECART(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.MINECART),
    INTERACT_INSIDE_FORTRESS(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.FORTRESS),
    USE_OMINOUS_BOTTLE(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.OMINOUS_BOTTLE),
    USE_IRON_TOOLS(AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.IRON),
    INTERACT_INSIDE_JUNGLE_PYRAMID(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.JUNGLE_PYRAMID),
    ATTACK_WITH_TRIDENT(AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.TRIDENT),
    EQUIP_IRON_ARMOR(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.IRON),
    SHOOT_BOW(AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.BOW),
    INTERACT_INSIDE_BASTION_REMNANT(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.BASTION_REMNANT),
    USE_JUKEBOX(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.JUKEBOX),
    THROW_ENDER_PEARL(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.ENDER_PEARL),
    USE_COMPOSTER(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.COMPOSTER),
    CHARGE_RESPAWN_ANCHOR(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.RESPAWN_ANCHOR),
    EAT_BEEF(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BEEF),
    INTERACT_INSIDE_PILLAGER_OUTPOST(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.PILLAGER_OUTPOST),
    TRADE_WITH_MASON(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.MASON),
    USE_FISHING_ROD(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.FISHING_ROD),
    EAT_PORKCHOP(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.PORKCHOP),
    TRADE_WITH_CARTOGRAPHER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.CARTOGRAPHER),
    INTERACT_INSIDE_MONUMENT(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MONUMENT),
    USE_DIAMOND_TOOLS(AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.DIAMOND),
    USE_CAULDRON(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CAULDRON),
    INTERACT_INSIDE_MONSTER_ROOM(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MONSTER_ROOM),
    EAT_BAKED_POTATO(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BAKED_POTATO),
    OPEN_SMOKER(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.SMOKER),
    EQUIP_TURTLE_HELMET(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, Items.TURTLE_HELMET),
    INTERACT_INSIDE_TRAIL_RUINS(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.TRAIL_RUINS),
    USE_BRUSH(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.BRUSH),
    EQUIP_DIAMOND_ARMOR(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.DIAMOND),
    UNLOCK_VAULT(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.VAULT),
    OPEN_BLAST_FURNACE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BLAST_FURNACE),
    INTERACT_INSIDE_STRONGHOLD(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.STRONGHOLD),
    TRADE_WITH_LEATHERWORKER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.LEATHERWORKER),
    USE_SPYGLASS(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.SPYGLASS),
    INTERACT_INSIDE_MANSION(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.MANSION),
    OPEN_BEACON(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BEACON),
    THROW_WIND_CHARGE(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.WIND_CHARGE),
    ENTER_END(AbilityUnlockedToastType.PORTAL, AbilitiesTreeCategoryType.UPGRADE, EndPortalBlock.class),
    OPEN_CARTOGRAPHY_TABLE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CARTOGRAPHY_TABLE),
    INTERACT_INSIDE_DESERT_WELL(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.DESERT_WELL),
    EAT_COOKED_SALMON(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_SALMON),
    EQUIP_ELYTRA(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, Items.ELYTRA),
    TRADE_WITH_SHEPHERD(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.SHEPHERD),
    TRADE_WITH_BUTCHER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.BUTCHER),
    INTERACT_INSIDE_TRIAL_CHAMBERS(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.TRIAL_CHAMBERS),
    OPEN_ENDER_CHEST(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.ENDER_CHEST),
    ATTACK_WITH_MACE(AbilityUnlockedToastType.WEAPON, AbilitiesTreeCategoryType.UPGRADE, Items.MACE),
    INTERACT_INSIDE_END_CITY(AbilityUnlockedToastType.LANDMARK, AbilitiesTreeCategoryType.LANDMARK, LandmarkType.END_CITY),
    USE_ENDER_EYE(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.ENDER_EYE),
    TELEPORT_OUTER_ISLANDS(AbilityUnlockedToastType.PORTAL, AbilitiesTreeCategoryType.UPGRADE, EndGatewayBlock.class),
    USE_NETHERITE_TOOLS(AbilityUnlockedToastType.TOOL, AbilitiesTreeCategoryType.UPGRADE, ToolMaterial.NETHERITE),
    EAT_COOKED_COD(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_COD),
    TRADE_WITH_FARMER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.FARMER),
    GLIDE_WITH_FIREWORKS(AbilityUnlockedToastType.ACTION, AbilitiesTreeCategoryType.ACTIONS, Items.FIREWORK_ROCKET),
    TRADE_WITH_CLERIC(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.CLERIC),
    EQUIP_NETHERITE_ARMOR(AbilityUnlockedToastType.EQUIPMENT, AbilitiesTreeCategoryType.UPGRADE, ArmorMaterials.NETHERITE),
    OPEN_BREWING_STAND(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.BREWING_STAND),
    EAT_COOKED_RABBIT(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_RABBIT),
    PLACE_END_CRYSTAL(AbilityUnlockedToastType.ITEM, AbilitiesTreeCategoryType.ACTIONS, Items.END_CRYSTAL),
    TRADE_WITH_FISHERMAN(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.FISHERMAN),
    OPEN_SMITHING_TABLE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.SMITHING_TABLE),
    EAT_COOKED_CHICKEN(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_CHICKEN),
    EAT_CHORUS_FRUIT(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.CHORUS_FRUIT),
    TRADE_WITH_FLETCHER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.FLETCHER),
    EAT_COOKED_MUTTON(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_MUTTON),
    TRADE_WITH_ARMORER(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.ARMORER),
    EAT_COOKED_PORKCHOP(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_PORKCHOP),
    EAT_BREAD(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.BREAD),
    TRADE_WITH_WEAPONSMITH(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.WEAPONSMITH),
    EAT_COOKED_BEEF(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.COOKED_BEEF),
    OPEN_LOOM(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.LOOM),
    USE_CAMPFIRE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.CAMPFIRE),
    OPEN_SHULKER_BOX(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.LIGHT_GRAY_SHULKER_BOX),
    EAT_GOLDEN_CARROT(AbilityUnlockedToastType.FOOD, AbilitiesTreeCategoryType.FOOD, FoodComponents.GOLDEN_CARROT),
    TRADE_WITH_TOOLSMITH(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.TOOLSMITH),
    TRADE_WITH_LIBRARIAN(AbilityUnlockedToastType.TRADING, AbilitiesTreeCategoryType.TRADING, VillagerProfession.LIBRARIAN),
    OPEN_ENCHANTING_TABLE(AbilityUnlockedToastType.BLOCK, AbilitiesTreeCategoryType.BLOCKS, Blocks.ENCHANTING_TABLE);

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

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, Item item) {
        this(unlockToastType, category, item, null, null, null, null, null, null, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, Block block) {
        this(unlockToastType, category, null, null, block, null, null, null, null, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, FoodComponent food) {
        this(unlockToastType, category, null, food, null, null, null, null, null, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, ToolMaterial toolMaterial) {
        this(unlockToastType, category, null, null, null, toolMaterial, null, null, null, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, ArmorMaterial armorMaterial) {
        this(unlockToastType, category, null, null, null, null, armorMaterial, null, null, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, Class<? extends Portal> portal) {
        this(unlockToastType, category, null, null, null, null, null, portal, null, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, VillagerProfession villager) {
        this(unlockToastType, category, null, null, null, null, null, null, villager, null);
    }

    AbilityType(AbilityUnlockedToastType unlockToastType, AbilitiesTreeCategoryType category, LandmarkType landmarkType) {
        this(unlockToastType, category, null, null, null, null, null, null, null, landmarkType);
    }

    AbilityType(
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
