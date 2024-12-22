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

    VISION(2, Items.ENDER_EYE),
    EAT_SALMON(3, FoodComponents.SALMON),
    EAT_COD(4, FoodComponents.COD),
    EAT_TROPICAL_FISH(5, FoodComponents.TROPICAL_FISH),
    JUMP(8, Items.SLIME_BLOCK),
    SNEAK(15, Items.CHAINMAIL_LEGGINGS),
    OPEN_DOOR(20, Items.PALE_OAK_DOOR),
    OPEN_INVENTORY(25, Items.LIGHT_GRAY_BUNDLE),
    SLEEP(30, Items.LIGHT_GRAY_BED),
    OPEN_CHEST(35, Blocks.CHEST),
    BREAK_BLOCKS(40, Items.COBBLESTONE),
    SWIM(45, Items.HEART_OF_THE_SEA),
    SPRINT(50, Items.CHAINMAIL_BOOTS),
    EAT_ENCHANTED_GOLDEN_APPLE(60, FoodComponents.ENCHANTED_GOLDEN_APPLE),
    EAT_SUSPICIOUS_STEW(70, FoodComponents.SUSPICIOUS_STEW),
    TRADE_WITH_WANDERING_TRADER(80, Items.WANDERING_TRADER_SPAWN_EGG),
    GET_INTO_MINECART(90, Items.MINECART),
    EAT_GLOW_BERRIES(100, FoodComponents.GLOW_BERRIES),
    EAT_ROTTEN_FLESH(110, FoodComponents.ROTTEN_FLESH),
    OPEN_CRAFTING_TABLE(120, Blocks.CRAFTING_TABLE),
    EQUIP_GOLDEN_ARMOR(33, ArmorMaterials.GOLD),
    EAT_BEETROOT(34, FoodComponents.BEETROOT),
    OPEN_FENCE_GATE(35, Items.PALE_OAK_FENCE_GATE),
    USE_WATER_BUCKET(40, Items.WATER_BUCKET),
    EAT_CARROT(45, FoodComponents.CARROT),
    THROW_ENDER_PEARL(50, Items.ENDER_PEARL),
    OPEN_STONECUTTER(60, Blocks.STONECUTTER),
    USE_GOLDEN_TOOLS(70, ToolMaterial.GOLD),
    GET_INTO_BOAT(85, Items.PALE_OAK_BOAT),
    EAT_CHICKEN(100, FoodComponents.CHICKEN),
    OPEN_FURNACE(110, Blocks.FURNACE),
    PUT_IN_BUNDLE(120, Items.BUNDLE),
    OPEN_ANVIL(125, Blocks.ANVIL),
    USE_OMINOUS_BOTTLE(130, Items.OMINOUS_BOTTLE),
    USE_WOODEN_TOOLS(140, ToolMaterial.WOOD),
    USE_FISHING_ROD(150, Items.FISHING_ROD),
    EAT_SWEET_BERRIES(155, FoodComponents.SWEET_BERRIES),
    EAT_DRIED_KELP(160, FoodComponents.DRIED_KELP),
    USE_SHEARS(165, Items.SHEARS),
    EAT_GOLDEN_APPLE(170, FoodComponents.GOLDEN_APPLE),
    SHOOT_CROSSBOW(175, Items.CROSSBOW),
    TRADE_WITH_MASON(180, VillagerProfession.MASON),
    EQUIP_LEATHER_ARMOR(190, ArmorMaterials.LEATHER),
    USE_BRUSH(210, Items.BRUSH),
    BREAK_BLOCKS_IN_NEGATIVE_Y(215, Items.COBBLED_DEEPSLATE),
    OPEN_ENCHANTING_TABLE(220, Blocks.ENCHANTING_TABLE),
    OPEN_SHULKER_BOX(230, Blocks.LIGHT_GRAY_SHULKER_BOX),
    OPEN_GRINDSTONE(250, Blocks.GRINDSTONE),
    SHOOT_BOW(255, Items.BOW),
    USE_CAULDRON(260, Blocks.CAULDRON),
    EAT_POTATO(270, FoodComponents.POTATO),
    USE_JUKEBOX(280, Blocks.JUKEBOX),
    USE_COMPOSTER(290, Blocks.COMPOSTER),
    EQUIP_IRON_ARMOR(300, ArmorMaterials.IRON),
    OPEN_BARREL(310, Blocks.BARREL),
    OPEN_SMOKER(320, Blocks.SMOKER),
    EAT_MELON_SLICE(330, FoodComponents.MELON_SLICE),
    OPEN_BEACON(340, Blocks.BEACON),
    EAT_MUSHROOM_STEW(350, FoodComponents.MUSHROOM_STEW),
    ENTER_NETHER(360, NetherPortalBlock.class),
    OPEN_BLAST_FURNACE(360, Blocks.BLAST_FURNACE),
    EQUIP_CHAINMAIL_ARMOR(370, ArmorMaterials.CHAIN),
    OPEN_CARTOGRAPHY_TABLE(380, Blocks.CARTOGRAPHY_TABLE),
    USE_SPYGLASS(385, Items.SPYGLASS),
    EAT_MUTTON(390, FoodComponents.MUTTON),
    OPEN_ENDER_CHEST(400, Blocks.ENDER_CHEST),
    THROW_EGG(405, Items.EGG),
    OPEN_BREWING_STAND(410, Blocks.BREWING_STAND),
    EAT_BEEF(420, FoodComponents.BEEF),
    ATTACK_WITH_MACE(430, Items.MACE),
    EAT_COOKED_CHICKEN(440, FoodComponents.COOKED_CHICKEN),
    USE_FLINT_AND_STEEL(450, Items.FLINT_AND_STEEL),
    EQUIP_DIAMOND_ARMOR(460, ArmorMaterials.DIAMOND),
    EAT_COOKED_PORKCHOP(470, FoodComponents.COOKED_PORKCHOP),
    OPEN_SMITHING_TABLE(480, Blocks.SMITHING_TABLE),
    USE_STONE_TOOLS(490, ToolMaterial.STONE),
    USE_NETHERITE_TOOLS(500, ToolMaterial.NETHERITE),
    USE_SHIELD(510, Items.SHIELD),
    EAT_PUFFERFISH(515, FoodComponents.PUFFERFISH),
    USE_IRON_TOOLS(520, ToolMaterial.IRON),
    EAT_BAKED_POTATO(530, FoodComponents.BAKED_POTATO),
    UNLOCK_VAULT(540, Blocks.VAULT),
    ATTACK_WITH_TRIDENT(550, Items.TRIDENT),
    EAT_COOKED_MUTTON(560, FoodComponents.COOKED_MUTTON),
    ENTER_END(570, EndPortalBlock.class),
    TRADE_WITH_CARTOGRAPHER(580, VillagerProfession.CARTOGRAPHER),
    EAT_COOKED_SALMON(590, FoodComponents.COOKED_SALMON),
    TRADE_WITH_LEATHERWORKER(600, VillagerProfession.LEATHERWORKER),
    EAT_COOKED_BEEF(610, FoodComponents.COOKED_BEEF),
    TRADE_WITH_SHEPHERD(620, VillagerProfession.SHEPHERD),
    TRADE_WITH_BUTCHER(630, VillagerProfession.BUTCHER),
    EQUIP_ELYTRA(640, Items.ELYTRA),
    TRADE_WITH_FARMER(650, VillagerProfession.FARMER),
    USE_DIAMOND_TOOLS(660, ToolMaterial.DIAMOND),
    EQUIP_NETHERITE_ARMOR(670, ArmorMaterials.NETHERITE),
    EAT_APPLE(680, FoodComponents.APPLE),
    TRADE_WITH_CLERIC(690, VillagerProfession.CLERIC),
    EAT_PORKCHOP(700, FoodComponents.PORKCHOP),
    EAT_POISONOUS_POTATO(705, FoodComponents.POISONOUS_POTATO),
    EAT_RABBIT(710, FoodComponents.RABBIT),
    EAT_COOKED_COD(720, FoodComponents.COOKED_COD),
    USE_ENDER_EYE(730, Items.ENDER_EYE),
    EAT_COOKED_RABBIT(740, FoodComponents.COOKED_RABBIT),
    TELEPORT_OUTER_ISLANDS(750, EndGatewayBlock.class),
    EAT_SPIDER_EYE(760, FoodComponents.SPIDER_EYE),
    TRADE_WITH_FISHERMAN(770, VillagerProfession.FISHERMAN),
    THROW_SNOWBALL(780, Items.SNOWBALL),
    THROW_WIND_CHARGE(790, Items.WIND_CHARGE),
    OPEN_TRAPDOOR(800, Items.PALE_OAK_TRAPDOOR),
    EAT_RABBIT_STEW(810, FoodComponents.RABBIT_STEW),
    TRADE_WITH_FLETCHER(820, VillagerProfession.FLETCHER),
    IGNITE_TNT(830, Blocks.TNT),
    CHARGE_RESPAWN_ANCHOR(840, Blocks.RESPAWN_ANCHOR),
    EAT_BEETROOT_SOUP(850, FoodComponents.BEETROOT_SOUP),
    OPEN_LOOM(860, Blocks.LOOM),
    EAT_HONEY(870, FoodComponents.HONEY_BOTTLE),
    EQUIP_TURTLE_HELMET(880, Items.TURTLE_HELMET),
    TRADE_WITH_ARMORER(890, VillagerProfession.ARMORER),
    PLACE_END_CRYSTAL(900, Items.END_CRYSTAL),
    EAT_CHORUS_FRUIT(910, FoodComponents.CHORUS_FRUIT),
    TRADE_WITH_WEAPONSMITH(920, VillagerProfession.WEAPONSMITH),
    EAT_GOLDEN_CARROT(930, FoodComponents.GOLDEN_CARROT),
    GLIDE_WITH_FIREWORKS(940, Items.FIREWORK_ROCKET),
    EAT_BREAD(950, FoodComponents.BREAD),
    TRADE_WITH_TOOLSMITH(960, VillagerProfession.TOOLSMITH),
    EAT_PUMPKIN_PIE(970, FoodComponents.PUMPKIN_PIE),
    USE_CAMPFIRE(980, Blocks.CAMPFIRE),
    EAT_COOKIE(990, FoodComponents.COOKIE),
    TRADE_WITH_LIBRARIAN(1000, VillagerProfession.LIBRARIAN);

    private int requiredAdvancementsCount;

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

//    static {
//        int totalCount = 0;
//        for (AbilityType ability : AbilityType.values()) {
//            totalCount += ability.requiredAdvancementsCount;
//            ability.requiredAdvancementsCount = totalCount;
//        }
//    }

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
