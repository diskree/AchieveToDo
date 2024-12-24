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
        AbilityCategory.ACTION,
        TreeLine.MAIN, 2,
        Items.ENDER_EYE
    ),
    EAT_SALMON(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.SALMON
    ),
    EAT_COD(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.COD
    ),
    EAT_TROPICAL_FISH(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.TROPICAL_FISH
    ),
    JUMP(
        AbilityCategory.ACTION,
        TreeLine.MAIN, 3,
        Items.SLIME_BLOCK
    ),
    SWIM(
        AbilityCategory.ACTION,
        TreeLine.MAIN, 1,
        Items.HEART_OF_THE_SEA
    ),
    EAT_ROTTEN_FLESH(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.ROTTEN_FLESH
    ),
    EAT_SPIDER_EYE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.SPIDER_EYE
    ),
    EAT_SWEET_BERRIES(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.SWEET_BERRIES
    ),
    OPEN_DOOR(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 1,
        Items.PALE_OAK_DOOR
    ),
    EAT_GLOW_BERRIES(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.GLOW_BERRIES
    ),
    EAT_PUFFERFISH(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.PUFFERFISH
    ),
    SLEEP(
        AbilityCategory.ACTION,
        TreeLine.MAIN, 1,
        Items.LIGHT_GRAY_BED
    ),
    EAT_POISONOUS_POTATO(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 1,
        FoodComponents.POISONOUS_POTATO
    ),
    SPRINT(
        AbilityCategory.ACTION,
        TreeLine.MAIN, 1,
        Items.CHAINMAIL_BOOTS
    ),
    SNEAK(
        AbilityCategory.ACTION,
        TreeLine.MAIN, 2,
        Items.CHAINMAIL_LEGGINGS
    ),
    OPEN_INVENTORY(
        AbilityCategory.ACTION,
        TreeLine.MAIN, 2,
        Items.LIGHT_GRAY_BUNDLE
    ),
    BREAK_BLOCKS(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 2,
        Items.COBBLESTONE
    ),
    EAT_SUSPICIOUS_STEW(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 2,
        FoodComponents.SUSPICIOUS_STEW
    ),
    OPEN_BARREL(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 2,
        Blocks.BARREL
    ),
    EAT_BEETROOT(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 2,
        FoodComponents.BEETROOT
    ),
    EAT_CARROT(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.CARROT
    ),
    EQUIP_GOLDEN_ARMOR(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 5,
        ArmorMaterials.GOLD
    ),
    EAT_CHICKEN(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.CHICKEN
    ),
    EAT_DRIED_KELP(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.DRIED_KELP
    ),
    OPEN_CRAFTING_TABLE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 5,
        Blocks.CRAFTING_TABLE
    ),
    USE_GOLDEN_TOOLS(
        AbilityCategory.TOOL,
        TreeLine.UPGRADE, 5,
        ToolMaterial.GOLD
    ),
    EAT_POTATO(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.POTATO
    ),
    OPEN_STONECUTTER(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 5,
        Blocks.STONECUTTER
    ),
    GET_INTO_BOAT(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 5,
        Items.PALE_OAK_BOAT
    ),
    EAT_APPLE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.APPLE
    ),
    OPEN_CHEST(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 5,
        Blocks.CHEST
    ),
    USE_SHIELD(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 5,
        Items.SHIELD
    ),
    EAT_MELON_SLICE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.MELON_SLICE
    ),
    OPEN_FURNACE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 5,
        Blocks.FURNACE
    ),
    EAT_COOKIE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.COOKIE
    ),
    EAT_MUSHROOM_STEW(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.MUSHROOM_STEW
    ),
    EAT_BEETROOT_SOUP(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.BEETROOT_SOUP
    ),
    PUT_IN_BUNDLE(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.BUNDLE
    ),
    EAT_RABBIT_STEW(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.RABBIT_STEW
    ),
    OPEN_TRAPDOOR(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 10,
        Items.PALE_OAK_TRAPDOOR
    ),
    USE_WOODEN_TOOLS(
        AbilityCategory.TOOL,
        TreeLine.UPGRADE, 5,
        ToolMaterial.WOOD
    ),
    EAT_HONEY(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.HONEY_BOTTLE
    ),
    USE_WATER_BUCKET(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 5,
        Items.WATER_BUCKET
    ),
    EAT_MUTTON(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.MUTTON
    ),
    THROW_SNOWBALL(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.SNOWBALL
    ),
    EQUIP_LEATHER_ARMOR(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 5,
        ArmorMaterials.LEATHER
    ),
    OPEN_FENCE_GATE(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 10,
        Items.PALE_OAK_FENCE_GATE
    ),
    EAT_PUMPKIN_PIE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.PUMPKIN_PIE
    ),
    EAT_GOLDEN_APPLE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.GOLDEN_APPLE
    ),
    USE_SHEARS(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 5,
        Items.SHEARS
    ),
    BREAK_BLOCKS_IN_NEGATIVE_Y(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 10,
        Items.COBBLED_DEEPSLATE
    ),
    EAT_ENCHANTED_GOLDEN_APPLE(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.ENCHANTED_GOLDEN_APPLE
    ),
    THROW_EGG(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.EGG
    ),
    USE_STONE_TOOLS(
        AbilityCategory.TOOL,
        TreeLine.UPGRADE, 10,
        ToolMaterial.STONE
    ),
    OPEN_GRINDSTONE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 5,
        Blocks.GRINDSTONE
    ),
    SHOOT_CROSSBOW(
        AbilityCategory.WEAPON,
        TreeLine.UPGRADE, 5,
        Items.CROSSBOW
    ),
    EAT_RABBIT(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 5,
        FoodComponents.RABBIT
    ),
    EQUIP_CHAINMAIL_ARMOR(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 10,
        ArmorMaterials.CHAIN
    ),
    OPEN_ANVIL(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.ANVIL
    ),
    USE_FLINT_AND_STEEL(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.FLINT_AND_STEEL
    ),
    ENTER_NETHER(
        AbilityCategory.PORTAL,
        TreeLine.UPGRADE, 10,
        NetherPortalBlock.class
    ),
    IGNITE_TNT(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.TNT
    ),
    TRADE_WITH_WANDERING_TRADER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        Items.WANDERING_TRADER_SPAWN_EGG
    ),
    GET_INTO_MINECART(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.MINECART
    ),
    USE_OMINOUS_BOTTLE(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.OMINOUS_BOTTLE
    ),
    USE_IRON_TOOLS(
        AbilityCategory.TOOL,
        TreeLine.UPGRADE, 10,
        ToolMaterial.IRON
    ),
    ATTACK_WITH_TRIDENT(
        AbilityCategory.WEAPON,
        TreeLine.UPGRADE, 10,
        Items.TRIDENT
    ),
    EQUIP_IRON_ARMOR(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 10,
        ArmorMaterials.IRON
    ),
    SHOOT_BOW(
        AbilityCategory.WEAPON,
        TreeLine.UPGRADE, 10,
        Items.BOW
    ),
    USE_JUKEBOX(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.JUKEBOX
    ),
    THROW_ENDER_PEARL(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.ENDER_PEARL
    ),
    USE_COMPOSTER(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.COMPOSTER
    ),
    CHARGE_RESPAWN_ANCHOR(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.RESPAWN_ANCHOR
    ),
    EAT_BEEF(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.BEEF
    ),
    TRADE_WITH_MASON(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.MASON
    ),
    USE_FISHING_ROD(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.FISHING_ROD
    ),
    EAT_PORKCHOP(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.PORKCHOP
    ),
    TRADE_WITH_CARTOGRAPHER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.CARTOGRAPHER
    ),
    USE_DIAMOND_TOOLS(
        AbilityCategory.TOOL,
        TreeLine.UPGRADE, 10,
        ToolMaterial.DIAMOND
    ),
    USE_CAULDRON(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.CAULDRON
    ),
    EAT_BAKED_POTATO(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.BAKED_POTATO
    ),
    OPEN_SMOKER(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.SMOKER
    ),
    EQUIP_TURTLE_HELMET(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 10,
        Items.TURTLE_HELMET
    ),
    USE_BRUSH(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.BRUSH
    ),
    EQUIP_DIAMOND_ARMOR(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 10,
        ArmorMaterials.DIAMOND
    ),
    UNLOCK_VAULT(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.VAULT
    ),
    OPEN_BLAST_FURNACE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.BLAST_FURNACE
    ),
    TRADE_WITH_LEATHERWORKER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.LEATHERWORKER
    ),
    USE_SPYGLASS(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.SPYGLASS
    ),
    OPEN_BEACON(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.BEACON
    ),
    ENTER_END(
        AbilityCategory.PORTAL,
        TreeLine.UPGRADE, 10,
        EndPortalBlock.class
    ),
    THROW_WIND_CHARGE(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.WIND_CHARGE
    ),
    OPEN_CARTOGRAPHY_TABLE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.CARTOGRAPHY_TABLE
    ),
    EAT_COOKED_SALMON(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_SALMON
    ),
    EQUIP_ELYTRA(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 10,
        Items.ELYTRA
    ),
    TRADE_WITH_SHEPHERD(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.SHEPHERD
    ),
    TRADE_WITH_BUTCHER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.BUTCHER
    ),
    OPEN_ENDER_CHEST(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.ENDER_CHEST
    ),
    ATTACK_WITH_MACE(
        AbilityCategory.WEAPON,
        TreeLine.UPGRADE, 10,
        Items.MACE
    ),
    USE_ENDER_EYE(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.ENDER_EYE
    ),
    TELEPORT_OUTER_ISLANDS(
        AbilityCategory.PORTAL,
        TreeLine.UPGRADE, 10,
        EndGatewayBlock.class
    ),
    USE_NETHERITE_TOOLS(
        AbilityCategory.TOOL,
        TreeLine.UPGRADE, 10,
        ToolMaterial.NETHERITE
    ),
    EAT_COOKED_COD(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_COD
    ),
    TRADE_WITH_FARMER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.FARMER
    ),
    GLIDE_WITH_FIREWORKS(
        AbilityCategory.ACTION,
        TreeLine.ACTIONS, 10,
        Items.FIREWORK_ROCKET
    ),
    TRADE_WITH_CLERIC(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.CLERIC
    ),
    EQUIP_NETHERITE_ARMOR(
        AbilityCategory.EQUIPMENT,
        TreeLine.UPGRADE, 10,
        ArmorMaterials.NETHERITE
    ),
    OPEN_BREWING_STAND(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.BREWING_STAND
    ),
    EAT_COOKED_RABBIT(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_RABBIT
    ),
    PLACE_END_CRYSTAL(
        AbilityCategory.ITEM,
        TreeLine.ACTIONS, 10,
        Items.END_CRYSTAL
    ),
    TRADE_WITH_FISHERMAN(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.FISHERMAN
    ),
    OPEN_SMITHING_TABLE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.SMITHING_TABLE
    ),
    EAT_COOKED_CHICKEN(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_CHICKEN
    ),
    EAT_CHORUS_FRUIT(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.CHORUS_FRUIT
    ),
    TRADE_WITH_FLETCHER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.FLETCHER
    ),
    EAT_COOKED_MUTTON(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_MUTTON
    ),
    TRADE_WITH_ARMORER(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.ARMORER
    ),
    EAT_COOKED_PORKCHOP(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_PORKCHOP
    ),
    EAT_BREAD(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.BREAD
    ),
    TRADE_WITH_WEAPONSMITH(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.WEAPONSMITH
    ),
    EAT_COOKED_BEEF(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.COOKED_BEEF
    ),
    OPEN_LOOM(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.LOOM
    ),
    USE_CAMPFIRE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.CAMPFIRE
    ),
    OPEN_SHULKER_BOX(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.LIGHT_GRAY_SHULKER_BOX
    ),
    EAT_GOLDEN_CARROT(
        AbilityCategory.FOOD,
        TreeLine.FOOD, 10,
        FoodComponents.GOLDEN_CARROT
    ),
    TRADE_WITH_TOOLSMITH(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.TOOLSMITH
    ),
    TRADE_WITH_LIBRARIAN(
        AbilityCategory.TRADING,
        TreeLine.TRADING, 10,
        VillagerProfession.LIBRARIAN
    ),
    OPEN_ENCHANTING_TABLE(
        AbilityCategory.BLOCK,
        TreeLine.BLOCKS, 10,
        Blocks.ENCHANTING_TABLE
    );

    private int requiredAdvancementsCount;

    private final TreeLine treeLine;
    private final AbilityCategory category;
    private final Item item;
    private final Block block;
    private final FoodComponent food;
    private final ToolMaterial toolMaterial;
    private final ArmorMaterial equipmentMaterial;
    private final Class<? extends Portal> portal;
    private final VillagerProfession villager;

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, Item item) {
        this(category, treeLine, requiredAdvancementsCount, item, null, null, null, null, null, null);
    }

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, Block block) {
        this(category, treeLine, requiredAdvancementsCount, null, null, block, null, null, null, null);
    }

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, FoodComponent food) {
        this(category, treeLine, requiredAdvancementsCount, null, food, null, null, null, null, null);
    }

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, ToolMaterial materials) {
        this(category, treeLine, requiredAdvancementsCount, null, null, null, materials, null, null, null);
    }

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, ArmorMaterial materials) {
        this(category, treeLine, requiredAdvancementsCount, null, null, null, null, materials, null, null);
    }

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, Class<? extends Portal> portal) {
        this(category, treeLine, requiredAdvancementsCount, null, null, null, null, null, portal, null);
    }

    AbilityType(AbilityCategory category, TreeLine treeLine, int requiredAdvancementsCount, VillagerProfession villager) {
        this(category, treeLine, requiredAdvancementsCount, null, null, null, null, null, null, villager);
    }

    AbilityType(
        AbilityCategory category,
        TreeLine treeLine,
        int requiredAdvancementsCount,
        Item item,
        FoodComponent food,
        Block block,
        ToolMaterial toolMaterial,
        ArmorMaterial equipmentMaterial,
        Class<? extends Portal> portal,
        VillagerProfession villager
    ) {
        this.treeLine = treeLine;
        this.category = category;
        this.requiredAdvancementsCount = requiredAdvancementsCount;
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
            totalCount += ability.requiredAdvancementsCount;
            ability.requiredAdvancementsCount = totalCount;
        }
    }

    public TreeLine getTreeLine() {
        return treeLine;
    }

    public AbilityCategory getCategory() {
        return category;
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
        @NotNull AdvancementRequirements requirementsData
    ) {
        if (requirementsData.requirements().size() != 2) {
            return null;
        }
        String abilityName = null;
        boolean isUnlockedCriterionFound = false;
        for (List<String> requirement : requirementsData.requirements()) {
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
    public static AbilityType findToolUsageAbility(ToolMaterial toolMaterial) {
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

    public @NotNull Text getLockedMessage() {
        if (food != null) {
            return Text.translatable("achievetodo.locked_message.food");
        }
        if (villager != null) {
            return Text.translatable("achievetodo.locked_message.villager");
        }
        return Text.translatable("achievetodo.locked_message." + getLowerCaseName());
    }

    public Text buildLockedDescription(int obtainedAdvancementsCount) {
        int leftAdvancementsCount = getRequiredAdvancementsCount() - obtainedAdvancementsCount;
        boolean isMultiLineActionBarInstalled = FabricLoader.getInstance().isModLoaded("multilineactionbar");
        return Text.of(getLockedMessage().getString() + "." + (isMultiLineActionBarInstalled ? "\n" : " "))
            .copy()
            .append(Text.translatable("achievetodo.locked_message.left"))
            .append(Text.of(String.valueOf(leftAdvancementsCount)))
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
        return Text.translatable("achievetodo.locked_message." + getLowerCaseName() + ".title");
    }

    public @NotNull Text getDescription() {
        return Text.translatable("achievetodo.locked_message." + getLowerCaseName() + ".description");
    }
}
