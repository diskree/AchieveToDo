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

    JUMP(
        8
    ),
    OPEN_DOOR(
        15
    ),
    SLEEP(
        20
    ),
    BREAK_BLOCKS(
        25
    ),
    OPEN_INVENTORY(
        30
    ),
    USING_BOAT(
        82
    ),
    USING_SHIELD(
        93, Items.SHIELD
    ),
    USING_WATER_BUCKET(
        135, Items.WATER_BUCKET
    ),
    USING_SHEARS(
        144, Items.SHEARS
    ),
    USING_CROSSBOW(
        178, Items.CROSSBOW
    ),
    BREAK_BLOCKS_IN_NEGATIVE_Y(
        206
    ),
    USING_FISHING_ROD(
        225, Items.FISHING_ROD
    ),
    USING_BOW(
        243, Items.BOW
    ),
    USING_BRUSH(
        261, Items.BRUSH
    ),
    USING_SPYGLASS(
        282, Items.SPYGLASS
    ),
    THROW_TRIDENT(
        311, Items.TRIDENT
    ),
    THROW_ENDER_PEARL(
        334, Items.ENDER_PEARL
    ),
    EQUIP_ELYTRA(
        657
    ),
    OUTER_ISLANDS(
        679, EndGatewayBlock.class
    ),
    FLY(
        721, Items.FIREWORK_ROCKET
    ),
    OPEN_SHULKER_BOX(
        754
    ),

    EAT_SALMON(
        3, FoodComponents.SALMON
    ),
    EAT_COD(
        4, FoodComponents.COD
    ),
    EAT_TROPICAL_FISH(
        5, FoodComponents.TROPICAL_FISH
    ),
    EAT_ROTTEN_FLESH(
        10, FoodComponents.ROTTEN_FLESH
    ),
    EAT_SPIDER_EYE(
        12, FoodComponents.SPIDER_EYE
    ),
    EAT_SWEET_BERRIES(
        14, FoodComponents.SWEET_BERRIES
    ),
    EAT_GLOW_BERRIES(
        16, FoodComponents.GLOW_BERRIES
    ),
    EAT_PUFFERFISH(
        18, FoodComponents.PUFFERFISH
    ),
    EAT_POISONOUS_POTATO(
        22, FoodComponents.POISONOUS_POTATO
    ),
    EAT_SUSPICIOUS_STEW(
        32, FoodComponents.SUSPICIOUS_STEW
    ),
    EAT_BEETROOT(
        38, FoodComponents.BEETROOT
    ),
    EAT_CARROT(
        43, FoodComponents.CARROT
    ),
    EAT_CHICKEN(
        47, FoodComponents.CHICKEN
    ),
    EAT_DRIED_KELP(
        51, FoodComponents.DRIED_KELP
    ),
    EAT_BEETROOT_SOUP(
        68, FoodComponents.BEETROOT_SOUP
    ),
    EAT_POTATO(
        71, FoodComponents.POTATO
    ),
    EAT_APPLE(
        83, FoodComponents.APPLE
    ),
    EAT_MELON_SLICE(
        95, FoodComponents.MELON_SLICE
    ),
    EAT_COOKIE(
        102, FoodComponents.COOKIE
    ),
    EAT_MUSHROOM_STEW(
        114, FoodComponents.MUSHROOM_STEW
    ),
    EAT_RABBIT_STEW(
        127, FoodComponents.RABBIT_STEW
    ),
    EAT_HONEY_BOTTLE(
        132, FoodComponents.HONEY_BOTTLE
    ),
    EAT_PUMPKIN_PIE(
        141, FoodComponents.PUMPKIN_PIE
    ),
    EAT_GOLDEN_APPLE(
        155, FoodComponents.GOLDEN_APPLE
    ),
    EAT_ENCHANTED_GOLDEN_APPLE(
        166, FoodComponents.ENCHANTED_GOLDEN_APPLE
    ),
    EAT_RABBIT(
        182, FoodComponents.RABBIT
    ),
    EAT_MUTTON(
        212, FoodComponents.MUTTON
    ),
    EAT_PORKCHOP(
        226, FoodComponents.PORKCHOP
    ),
    EAT_BEEF(
        249, FoodComponents.BEEF
    ),
    EAT_BAKED_POTATO(
        252, FoodComponents.BAKED_POTATO
    ),
    EAT_COOKED_SALMON(
        312, FoodComponents.COOKED_SALMON
    ),
    EAT_COOKED_COD(
        373, FoodComponents.COOKED_COD
    ),
    EAT_COOKED_RABBIT(
        432, FoodComponents.COOKED_RABBIT
    ),
    EAT_COOKED_CHICKEN(
        459, FoodComponents.COOKED_CHICKEN
    ),
    EAT_COOKED_MUTTON(
        524, FoodComponents.COOKED_MUTTON
    ),
    EAT_COOKED_PORKCHOP(
        550, FoodComponents.COOKED_PORKCHOP
    ),
    EAT_COOKED_BEEF(
        603, FoodComponents.COOKED_BEEF
    ),
    EAT_BREAD(
        654, FoodComponents.BREAD
    ),
    EAT_CHORUS_FRUIT(
        686, FoodComponents.CHORUS_FRUIT
    ),
    EAT_GOLDEN_CARROT(
        702, FoodComponents.GOLDEN_CARROT
    ),

    OPEN_CHEST(
        36, Blocks.CHEST
    ),
    USING_CRAFTING_TABLE(
        52, Blocks.CRAFTING_TABLE
    ),
    USING_STONECUTTER(
        78, Blocks.STONECUTTER
    ),
    OPEN_FURNACE(
        99, Blocks.FURNACE
    ),
    USING_ANVIL(
        103, Blocks.ANVIL
    ),
    USING_GRINDSTONE(
        174, Blocks.GRINDSTONE
    ),
    USING_LOOM(
        215, Blocks.LOOM
    ),
    OPEN_SMOKER(
        257, Blocks.SMOKER
    ),
    OPEN_BLAST_FURNACE(
        272, Blocks.BLAST_FURNACE
    ),
    USING_CARTOGRAPHY_TABLE(
        304, Blocks.CARTOGRAPHY_TABLE
    ),
    OPEN_ENDER_CHEST(
        352, Blocks.ENDER_CHEST
    ),
    OPEN_BREWING_STAND(
        409, Blocks.BREWING_STAND
    ),
    USING_SMITHING_TABLE(
        453, Blocks.SMITHING_TABLE
    ),
    USING_BEACON(
        505, Blocks.BEACON
    ),
    USING_ENCHANTING_TABLE(
        937, Blocks.ENCHANTING_TABLE
    ),

    USING_GOLDEN_TOOLS(
        41, ToolMaterial.GOLD
    ),
    USING_WOODEN_TOOLS(
        63, ToolMaterial.WOOD
    ),
    USING_STONE_TOOLS(
        97, ToolMaterial.STONE
    ),
    USING_IRON_TOOLS(
        122, ToolMaterial.IRON
    ),
    USING_DIAMOND_TOOLS(
        358, ToolMaterial.DIAMOND
    ),
    USING_NETHERITE_TOOLS(
        504, ToolMaterial.NETHERITE
    ),

    EQUIP_GOLDEN_ARMOR(
        44, ArmorMaterials.GOLD
    ),
    EQUIP_LEATHER_ARMOR(
        98, ArmorMaterials.LEATHER
    ),
    EQUIP_IRON_ARMOR(
        158, ArmorMaterials.IRON
    ),
    EQUIP_CHAINMAIL_ARMOR(
        203, ArmorMaterials.CHAIN
    ),
    EQUIP_DIAMOND_ARMOR(
        305, ArmorMaterials.DIAMOND
    ),
    EQUIP_NETHERITE_ARMOR(
        552, ArmorMaterials.NETHERITE
    ),

    NETHER(
        275, NetherPortalBlock.class
    ),
    END(
        575, EndPortalBlock.class
    ),

    VILLAGER_MASON(
        210, VillagerProfession.MASON
    ),
    VILLAGER_CARTOGRAPHER(
        230, VillagerProfession.CARTOGRAPHER
    ),
    VILLAGER_LEATHERWORKER(
        280, VillagerProfession.LEATHERWORKER
    ),
    VILLAGER_SHEPHERD(
        315, VillagerProfession.SHEPHERD
    ),
    VILLAGER_BUTCHER(
        330, VillagerProfession.BUTCHER
    ),
    VILLAGER_FARMER(
        385, VillagerProfession.FARMER
    ),
    VILLAGER_CLERIC(
        400, VillagerProfession.CLERIC
    ),
    VILLAGER_FISHERMAN(
        444, VillagerProfession.FISHERMAN
    ),
    VILLAGER_FLETCHER(
        520, VillagerProfession.FLETCHER
    ),
    VILLAGER_ARMORER(
        540, VillagerProfession.ARMORER
    ),
    VILLAGER_WEAPONSMITH(
        590, VillagerProfession.WEAPONSMITH
    ),
    VILLAGER_TOOLSMITH(
        860, VillagerProfession.TOOLSMITH
    ),
    VILLAGER_LIBRARIAN(
        900, VillagerProfession.LIBRARIAN
    );

    private final int requiredAdvancementsCount;

    private final Item item;
    private final FoodComponent food;
    private final Block block;
    private final ToolMaterial toolMaterial;
    private final ArmorMaterial equipmentMaterial;
    private final Class<? extends Portal> portal;
    private final VillagerProfession villager;

    AbilityType(int requiredAdvancementsCount) {
        this(requiredAdvancementsCount, null, null, null, null, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, Item item) {
        this(requiredAdvancementsCount, item, null, null, null, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, FoodComponent food) {
        this(requiredAdvancementsCount, null, food, null, null, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, Block block) {
        this(requiredAdvancementsCount, null, null, block, null, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, ToolMaterial materials) {
        this(requiredAdvancementsCount, null, null, null, materials, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, ArmorMaterial materials) {
        this(requiredAdvancementsCount, null, null, null, null, materials, null, null);
    }

    AbilityType(int requiredAdvancementsCount, Class<? extends Portal> portal) {
        this(requiredAdvancementsCount, null, null, null, null, null, portal, null);
    }

    AbilityType(int requiredAdvancementsCount, VillagerProfession villager) {
        this(requiredAdvancementsCount, null, null, null, null, null, null, villager);
    }

    AbilityType(
        int requiredAdvancementsCount,
        Item item,
        FoodComponent food,
        Block block,
        ToolMaterial toolMaterial,
        ArmorMaterial equipmentMaterial,
        Class<? extends Portal> portal,
        VillagerProfession villager
    ) {
        this.requiredAdvancementsCount = requiredAdvancementsCount;
        this.item = item;
        this.food = food;
        this.block = block;
        this.toolMaterial = toolMaterial;
        this.equipmentMaterial = equipmentMaterial;
        this.portal = portal;
        this.villager = villager;
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

    public AbilityCategory getAbilityCategory() {
        if (item != null && item.getComponents().contains(DataComponentTypes.FOOD)) {
            return AbilityCategory.FOOD;
        }
        if (item != null && this != USING_WATER_BUCKET && this != FLY) {
            return AbilityCategory.ITEM;
        }
        if (block != null || this == OPEN_SHULKER_BOX) {
            return AbilityCategory.BLOCK;
        }
        if (toolMaterial != null) {
            return AbilityCategory.TOOL;
        }
        if (equipmentMaterial != null || this == EQUIP_ELYTRA) {
            return AbilityCategory.EQUIPMENT;
        }
        if (portal != null) {
            return AbilityCategory.DIMENSION;
        }
        if (villager != null) {
            return AbilityCategory.VILLAGER;
        }
        return AbilityCategory.ACTION;
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
        int leftAdvancementsCount = requiredAdvancementsCount - obtainedAdvancementsCount;
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
        if (food != null) {
            return Registries.ITEM.stream()
                .filter(item -> item.getComponents().get(DataComponentTypes.FOOD) == food)
                .findFirst()
                .orElseThrow();
        }
        if (block != null) {
            return block.asItem();
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
                case NETHER -> Items.OBSIDIAN;
                case END -> Items.END_PORTAL_FRAME;
                case OUTER_ISLANDS -> Items.CHORUS_FLOWER;
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
        return switch (this) {
            case JUMP -> Items.SLIME_BLOCK;
            case OPEN_DOOR -> Items.DARK_OAK_DOOR;
            case SLEEP -> Items.RED_BED;
            case OPEN_INVENTORY -> Items.BUNDLE;
            case BREAK_BLOCKS -> Items.COBBLESTONE;
            case USING_BOAT -> Items.OAK_BOAT;
            case BREAK_BLOCKS_IN_NEGATIVE_Y -> Items.COBBLED_DEEPSLATE;
            case EQUIP_ELYTRA -> Items.ELYTRA;
            case OUTER_ISLANDS -> Items.END_STONE_BRICKS;
            case OPEN_SHULKER_BOX -> Items.SHULKER_BOX;
            default -> null;
        };
    }

    public @NotNull Text getTitle() {
        return Text.translatable("achievetodo.locked_message." + getLowerCaseName() + ".title");
    }

    public @NotNull Text getDescription() {
        return Text.translatable("achievetodo.locked_message." + getLowerCaseName() + ".description");
    }
}
