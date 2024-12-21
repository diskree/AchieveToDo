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
        2,
        Items.ENDER_EYE
    ),
    EAT_SALMON(
        4,
        FoodComponents.SALMON
    ),
    EAT_COD(
        6,
        FoodComponents.COD
    ),
    JUMP(
        8,
        Items.SLIME_BLOCK
    ),
    EAT_TROPICAL_FISH(
        10,
        FoodComponents.TROPICAL_FISH
    ),
    SNEAK(
        13,
        Items.CHAINMAIL_LEGGINGS
    ),
    OPEN_DOOR(
        16,
        Items.PALE_OAK_DOOR
    ),
    SLEEP(
        19,
        Items.LIGHT_GRAY_BED
    ),
    BREAK_BLOCKS(
        23,
        Items.COBBLESTONE
    ),
    OPEN_INVENTORY(
        30,
        Items.LIGHT_GRAY_BUNDLE
    ),
    SWIM(
        35,
        Items.WATER_BUCKET
    ),
    SPRINT(
        50,
        Items.CHAINMAIL_BOOTS
    ),
    THROW_ENDER_EYE(999, Items.ENDER_EYE),
    GLIDE_WITH_FIREWORKS(721, Items.FIREWORK_ROCKET),

    OPEN_TRAPDOOR(999, Items.PALE_OAK_TRAPDOOR),
    OPEN_FENCE_GATE(999, Items.PALE_OAK_FENCE_GATE),

    BREAK_BLOCKS_IN_NEGATIVE_Y(206, Items.COBBLED_DEEPSLATE),

    GET_INTO_BOAT(82, Items.PALE_OAK_BOAT),
    GET_INTO_MINECART(999, Items.MINECART),
    USE_SHEARS(144, Items.SHEARS),
    USE_BRUSH(261, Items.BRUSH),
    USE_SPYGLASS(282, Items.SPYGLASS),
    USE_FLINT_AND_STEEL(999, Items.FLINT_AND_STEEL),
    USE_FISHING_ROD(225, Items.FISHING_ROD),
    USE_WATER_BUCKET(135, Items.WATER_BUCKET),

    THROW_EGG(999, Items.EGG),
    THROW_SNOWBALL(999, Items.SNOWBALL),
    THROW_WIND_CHARGE(999, Items.WIND_CHARGE),
    THROW_ENDER_PEARL(334, Items.ENDER_PEARL),
    SHOOT_CROSSBOW(178, Items.CROSSBOW),
    SHOOT_BOW(243, Items.BOW),
    ATTACK_WITH_TRIDENT(311, Items.TRIDENT),
    ATTACK_WITH_MACE(999, Items.MACE),

    PLACE_END_CRYSTAL(999, Items.END_CRYSTAL),
    PUT_IN_BUNDLE(999, Items.BUNDLE),
    DRINK_OMINOUS_BOTTLE(999, Items.OMINOUS_BOTTLE),

    OPEN_CHEST(
        36, Blocks.CHEST
    ),
    OPEN_CRAFTING_TABLE(
        52, Blocks.CRAFTING_TABLE
    ),
    OPEN_STONECUTTER(
        78, Blocks.STONECUTTER
    ),
    OPEN_FURNACE(
        99, Blocks.FURNACE
    ),
    OPEN_ANVIL(
        103, Blocks.ANVIL
    ),
    OPEN_GRINDSTONE(
        174, Blocks.GRINDSTONE
    ),
    OPEN_LOOM(
        215, Blocks.LOOM
    ),
    OPEN_SMOKER(
        257, Blocks.SMOKER
    ),
    OPEN_BLAST_FURNACE(
        272, Blocks.BLAST_FURNACE
    ),
    OPEN_CARTOGRAPHY_TABLE(
        304, Blocks.CARTOGRAPHY_TABLE
    ),
    OPEN_ENDER_CHEST(
        352, Blocks.ENDER_CHEST
    ),
    OPEN_BREWING_STAND(
        409, Blocks.BREWING_STAND
    ),
    OPEN_SMITHING_TABLE(
        453, Blocks.SMITHING_TABLE
    ),
    OPEN_BEACON(
        505, Blocks.BEACON
    ),
    OPEN_ENCHANTING_TABLE(
        937, Blocks.ENCHANTING_TABLE
    ),
    OPEN_SHULKER_BOX(754, Blocks.LIGHT_GRAY_SHULKER_BOX),
    UNLOCK_VAULT(999, Blocks.VAULT),
    USE_CAMPFIRE(999, Blocks.CAMPFIRE),
    USE_CAULDRON(999, Blocks.CAULDRON),
    USE_JUKEBOX(999, Blocks.JUKEBOX),
    USE_COMPOSTER(999, Blocks.COMPOSTER),
    OPEN_BARREL(999, Blocks.BARREL),
    IGNITE_TNT(999, Blocks.TNT),
    CHARGE_RESPAWN_ANCHOR(999, Blocks.RESPAWN_ANCHOR),

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

    USE_GOLDEN_TOOLS(
        41, ToolMaterial.GOLD
    ),
    USE_WOODEN_TOOLS(
        63, ToolMaterial.WOOD
    ),
    USE_STONE_TOOLS(
        97, ToolMaterial.STONE
    ),
    USE_IRON_TOOLS(
        122, ToolMaterial.IRON
    ),
    USE_DIAMOND_TOOLS(
        358, ToolMaterial.DIAMOND
    ),
    USE_NETHERITE_TOOLS(
        504, ToolMaterial.NETHERITE
    ),

    USE_SHIELD(93, Items.SHIELD),

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

    EQUIP_ELYTRA(657, Items.ELYTRA),
    EQUIP_TURTLE_HELMET(999, Items.TURTLE_HELMET),

    ENTER_NETHER(
        275, NetherPortalBlock.class
    ),
    ENTER_END(
        575, EndPortalBlock.class
    ),
    TELEPORT_OUTER_ISLANDS(
        679, EndGatewayBlock.class
    ),

    TRADE_WITH_WANDERING_TRADER(
        100, Items.WANDERING_TRADER_SPAWN_EGG
    ),
    TRADE_WITH_MASON(
        210, VillagerProfession.MASON
    ),
    TRADE_WITH_CARTOGRAPHER(
        230, VillagerProfession.CARTOGRAPHER
    ),
    TRADE_WITH_LEATHERWORKER(
        280, VillagerProfession.LEATHERWORKER
    ),
    TRADE_WITH_SHEPHERD(
        315, VillagerProfession.SHEPHERD
    ),
    TRADE_WITH_BUTCHER(
        330, VillagerProfession.BUTCHER
    ),
    TRADE_WITH_FARMER(
        385, VillagerProfession.FARMER
    ),
    TRADE_WITH_CLERIC(
        400, VillagerProfession.CLERIC
    ),
    TRADE_WITH_FISHERMAN(
        444, VillagerProfession.FISHERMAN
    ),
    TRADE_WITH_FLETCHER(
        520, VillagerProfession.FLETCHER
    ),
    TRADE_WITH_ARMORER(
        540, VillagerProfession.ARMORER
    ),
    TRADE_WITH_WEAPONSMITH(
        590, VillagerProfession.WEAPONSMITH
    ),
    TRADE_WITH_TOOLSMITH(
        860, VillagerProfession.TOOLSMITH
    ),
    TRADE_WITH_LIBRARIAN(
        900, VillagerProfession.LIBRARIAN
    );

    private final int requiredAdvancementsCount;

    private final Item item;
    private final Block block;
    private final FoodComponent food;
    private final ToolMaterial toolMaterial;
    private final ArmorMaterial equipmentMaterial;
    private final Class<? extends Portal> portal;
    private final VillagerProfession villager;

    AbilityType(int requiredAdvancementsCount, Item item) {
        this(requiredAdvancementsCount, item, null, null, null, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, Block block) {
        this(requiredAdvancementsCount, null, null, block, null, null, null, null);
    }

    AbilityType(int requiredAdvancementsCount, FoodComponent food) {
        this(requiredAdvancementsCount, null, food, null, null, null, null, null);
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

    public AbilityCategory getAbilityCategory() {
        if (item != null) {
            return AbilityCategory.ITEM;
        }
        if (food != null) {
            return AbilityCategory.FOOD;
        }
        if (block != null || this == OPEN_SHULKER_BOX) {
            return AbilityCategory.BLOCK;
        }
        if (toolMaterial != null) {
            return AbilityCategory.TOOL;
        }
        if (equipmentMaterial != null || this == EQUIP_ELYTRA || this == EQUIP_TURTLE_HELMET) {
            return AbilityCategory.EQUIPMENT;
        }
        if (portal != null) {
            return AbilityCategory.PORTAL;
        }
        if (villager != null || this == TRADE_WITH_WANDERING_TRADER) {
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
