package com.diskree.achievetodo.blocked_actions;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.injection.ArmorItemImpl;
import com.diskree.achievetodo.injection.MiningToolItemImpl;
import com.diskree.achievetodo.injection.SwordItemImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
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

public enum BlockedActionType {

    JUMP(
        8
    ),
    OPEN_DOOR(
        15
    ),
    SLEEP(
        20
    ),
    OPEN_INVENTORY(
        25
    ),
    BREAK_BLOCKS_IN_POSITIVE_Y(
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
    END_GATEWAY(
        679, EndGatewayBlock.class
    ),
    FLY(
        721, Items.FIREWORK_ROCKET
    ),
    OPEN_SHULKER_BOX(
        754
    ),

    EAT_SALMON(
        3, Items.SALMON
    ),
    EAT_COD(
        4, Items.COD
    ),
    EAT_TROPICAL_FISH(
        5, Items.TROPICAL_FISH
    ),
    EAT_ROTTEN_FLESH(
        10, Items.ROTTEN_FLESH
    ),
    EAT_SPIDER_EYE(
        12, Items.SPIDER_EYE
    ),
    EAT_SWEET_BERRIES(
        14, Items.SWEET_BERRIES
    ),
    EAT_GLOW_BERRIES(
        16, Items.GLOW_BERRIES
    ),
    EAT_PUFFERFISH(
        18, Items.PUFFERFISH
    ),
    EAT_POISONOUS_POTATO(
        22, Items.POISONOUS_POTATO
    ),
    EAT_CHORUS_FRUIT(
        27, Items.CHORUS_FRUIT
    ),
    EAT_SUSPICIOUS_STEW(
        32, Items.SUSPICIOUS_STEW
    ),
    EAT_BEETROOT(
        38, Items.BEETROOT
    ),
    EAT_CARROT(
        43, Items.CARROT
    ),
    EAT_CHICKEN(
        47, Items.CHICKEN
    ),
    EAT_DRIED_KELP(
        51, Items.DRIED_KELP
    ),
    EAT_BEETROOT_SOUP(
        68, Items.BEETROOT_SOUP
    ),
    EAT_POTATO(
        71, Items.POTATO
    ),
    EAT_APPLE(
        83, Items.APPLE
    ),
    EAT_MELON_SLICE(
        95, Items.MELON_SLICE
    ),
    EAT_COOKIE(
        102, Items.COOKIE
    ),
    EAT_MUSHROOM_STEW(
        114, Items.MUSHROOM_STEW
    ),
    EAT_RABBIT_STEW(
        127, Items.RABBIT_STEW
    ),
    EAT_HONEY_BOTTLE(
        132, Items.HONEY_BOTTLE
    ),
    EAT_PUMPKIN_PIE(
        141, Items.PUMPKIN_PIE
    ),
    EAT_GOLDEN_APPLE(
        155, Items.GOLDEN_APPLE
    ),
    EAT_ENCHANTED_GOLDEN_APPLE(
        166, Items.ENCHANTED_GOLDEN_APPLE
    ),
    EAT_RABBIT(
        182, Items.RABBIT
    ),
    EAT_MUTTON(
        212, Items.MUTTON
    ),
    EAT_PORKCHOP(
        226, Items.PORKCHOP
    ),
    EAT_BEEF(
        249, Items.BEEF
    ),
    EAT_BAKED_POTATO(
        252, Items.BAKED_POTATO
    ),
    EAT_COOKED_SALMON(
        312, Items.COOKED_SALMON
    ),
    EAT_COOKED_COD(
        373, Items.COOKED_COD
    ),
    EAT_COOKED_RABBIT(
        432, Items.COOKED_RABBIT
    ),
    EAT_COOKED_CHICKEN(
        459, Items.COOKED_CHICKEN
    ),
    EAT_COOKED_MUTTON(
        524, Items.COOKED_MUTTON
    ),
    EAT_COOKED_PORKCHOP(
        550, Items.COOKED_PORKCHOP
    ),
    EAT_COOKED_BEEF(
        603, Items.COOKED_BEEF
    ),
    EAT_BREAD(
        654, Items.BREAD
    ),
    EAT_GOLDEN_CARROT(
        702, Items.GOLDEN_CARROT
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

    private final int unblockAdvancementsCount;

    private final Item item;
    private final Block block;
    private final ToolMaterial toolMaterial;
    private final ArmorMaterial equipmentMaterial;
    private final Class<? extends Portal> portal;
    private final VillagerProfession villager;

    BlockedActionType(int unblockAdvancementsCount) {
        this(unblockAdvancementsCount, null, null, null, null, null, null);
    }

    BlockedActionType(int unblockAdvancementsCount, Item item) {
        this(unblockAdvancementsCount, item, null, null, null, null, null);
    }

    BlockedActionType(int unblockAdvancementsCount, Block block) {
        this(unblockAdvancementsCount, null, block, null, null, null, null);
    }

    BlockedActionType(int unblockAdvancementsCount, ToolMaterial materials) {
        this(unblockAdvancementsCount, null, null, materials, null, null, null);
    }

    BlockedActionType(int unblockAdvancementsCount, ArmorMaterial materials) {
        this(unblockAdvancementsCount, null, null, null, materials, null, null);
    }

    BlockedActionType(int unblockAdvancementsCount, Class<? extends Portal> portal) {
        this(unblockAdvancementsCount, null, null, null, null, portal, null);
    }

    BlockedActionType(int unblockAdvancementsCount, VillagerProfession villager) {
        this(unblockAdvancementsCount, null, null, null, null, null, villager);
    }

    BlockedActionType(
        int unblockAdvancementsCount,
        Item item,
        Block block,
        ToolMaterial toolMaterial,
        ArmorMaterial equipmentMaterial,
        Class<? extends Portal> portal,
        VillagerProfession villager
    ) {
        this.unblockAdvancementsCount = unblockAdvancementsCount;
        this.item = item;
        this.block = block;
        this.toolMaterial = toolMaterial;
        this.equipmentMaterial = equipmentMaterial;
        this.portal = portal;
        this.villager = villager;
    }

    public static BlockedActionType map(String name) {
        if (name == null) {
            return null;
        }
        for (BlockedActionType blockedAction : values()) {
            if (blockedAction.name().equalsIgnoreCase(name)) {
                return blockedAction;
            }
        }
        return null;
    }

    public static BlockedActionType map(@NotNull PlacedAdvancement advancement) {
        return map(advancement.getAdvancementEntry().id());
    }

    public static @Nullable BlockedActionType map(@NotNull Identifier advancementId) {
        String[] pathPieces = advancementId.getPath().split("/");
        return pathPieces.length == 2 ? BlockedActionType.map(pathPieces[1]) : null;
    }

    @Nullable
    public static BlockedActionType findBlockedItem(PlayerEntity player, ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.isOf(Items.CROSSBOW)) {
            return CrossbowItem.isCharged(stack) ? BlockedActionType.USING_CROSSBOW : null;
        }
        if (stack.isOf(Items.FIREWORK_ROCKET)) {
            return player.isGliding() ? BlockedActionType.FLY : null;
        }
        for (BlockedActionType blockedAction : BlockedActionType.values()) {
            Item item = stack.getItem();
            if (item == blockedAction.item) {
                return blockedAction;
            }
        }
        return null;
    }

    @Nullable
    public static BlockedActionType findBlockedBlock(BlockState blockState) {
        if (blockState == null) {
            return null;
        }
        if (blockState.getBlock() instanceof BedBlock) {
            return SLEEP;
        }
        if (blockState.getBlock() instanceof ShulkerBoxBlock) {
            return OPEN_SHULKER_BOX;
        }
        if (blockState.getBlock() instanceof AnvilBlock) {
            return USING_ANVIL;
        }
        if (blockState.getBlock() instanceof DoorBlock doorBlock) {
            return !doorBlock.isOpen(blockState) ? OPEN_DOOR : null;
        }
        for (BlockedActionType blockedAction : BlockedActionType.values()) {
            if (blockState.isOf(blockedAction.block)) {
                return blockedAction;
            }
        }
        return null;
    }

    @Nullable
    public static BlockedActionType findBlockedTool(Item item) {
        if (item == null) {
            return null;
        }
        if (item instanceof MiningToolItemImpl toolItem) {
            for (BlockedActionType blockedAction : BlockedActionType.values()) {
                if (toolItem.achievetodo$getToolMaterial() == blockedAction.toolMaterial) {
                    return blockedAction;
                }
            }
        }
        if (item instanceof SwordItemImpl toolItem) {
            for (BlockedActionType blockedAction : BlockedActionType.values()) {
                if (toolItem.achievetodo$getSwordMaterial() == blockedAction.toolMaterial) {
                    return blockedAction;
                }
            }
        }
        return null;
    }

    @Nullable
    public static BlockedActionType findBlockedEquipment(Item item) {
        if (item == null) {
            return null;
        }
        if (item == Items.ELYTRA) {
            return EQUIP_ELYTRA;
        }
        if (item instanceof ArmorItemImpl armorItem) {
            for (BlockedActionType blockedAction : BlockedActionType.values()) {
                if (armorItem.achievetodo$getMaterial() == blockedAction.equipmentMaterial) {
                    return blockedAction;
                }
            }
        }
        return null;
    }

    @Nullable
    public static BlockedActionType findBlockedPortal(Portal portal) {
        if (portal == null) {
            return null;
        }
        for (BlockedActionType blockedAction : BlockedActionType.values()) {
            if (portal.getClass() == blockedAction.portal) {
                return blockedAction;
            }
        }
        return null;
    }

    @Nullable
    public static BlockedActionType findBlockedVillager(VillagerProfession profession) {
        if (profession == null) {
            return null;
        }
        for (BlockedActionType blockedAction : BlockedActionType.values()) {
            if (profession == blockedAction.villager) {
                return blockedAction;
            }
        }
        return null;
    }

    public BlockedActionCategory getCategory() {
        if (item != null && item.getComponents().contains(DataComponentTypes.FOOD)) {
            return BlockedActionCategory.FOOD;
        }
        if (item != null && this != USING_WATER_BUCKET && this != FLY) {
            return BlockedActionCategory.ITEM;
        }
        if (block != null || this == OPEN_SHULKER_BOX) {
            return BlockedActionCategory.BLOCK;
        }
        if (toolMaterial != null) {
            return BlockedActionCategory.TOOL;
        }
        if (equipmentMaterial != null || this == EQUIP_ELYTRA) {
            return BlockedActionCategory.EQUIPMENT;
        }
        if (portal != null) {
            return BlockedActionCategory.DIMENSION;
        }
        if (villager != null) {
            return BlockedActionCategory.VILLAGER;
        }
        return BlockedActionCategory.ACTION;
    }

    public @NotNull Text getBlockedMessage() {
        if (item != null && item.getComponents().contains(DataComponentTypes.FOOD)) {
            return Text.translatable("achievetodo.blocked_message.food");
        }
        if (villager != null) {
            return Text.translatable("achievetodo.blocked_message.villager");
        }
        return Text.translatable("achievetodo.blocked_message." + getName());
    }

    public Text buildBlockedDescription(PlayerEntity player) {
        int leftAdvancementsCount = unblockAdvancementsCount - AchieveToDo.getScore(player);
        boolean isMultiLineActionBarInstalled = FabricLoader.getInstance().isModLoaded("multilineactionbar");
        return Text.of(getBlockedMessage().getString() + "." + (isMultiLineActionBarInstalled ? "\n" : " "))
            .copy()
            .append(Text.translatable("achievetodo.blocked_message.left"))
            .append(Text.of(String.valueOf(leftAdvancementsCount)))
            .formatted(Formatting.YELLOW);
    }

    public int getUnblockAdvancementsCount() {
        return unblockAdvancementsCount;
    }

    public boolean isUnblocked(PlayerEntity player) {
        return AchieveToDo.getScore(player) >= unblockAdvancementsCount;
    }

    public @NotNull String getName() {
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
        if (toolMaterial != null) {
            return Registries.ITEM.stream()
                .filter(item -> item instanceof PickaxeItem &&
                    item instanceof MiningToolItemImpl miningToolItem &&
                    miningToolItem.achievetodo$getToolMaterial() == toolMaterial
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
            return portal == NetherPortalBlock.class ? Items.OBSIDIAN : Items.END_PORTAL_FRAME;
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
            case BREAK_BLOCKS_IN_POSITIVE_Y -> Items.COBBLESTONE;
            case USING_BOAT -> Items.OAK_BOAT;
            case BREAK_BLOCKS_IN_NEGATIVE_Y -> Items.COBBLED_DEEPSLATE;
            case EQUIP_ELYTRA -> Items.ELYTRA;
            case END_GATEWAY -> Items.END_STONE_BRICKS;
            case OPEN_SHULKER_BOX -> Items.SHULKER_BOX;
            default -> null;
        };
    }

    public @NotNull Text getTitle() {
        return Text.translatable("achievetodo.blocked_message." + getName() + ".title");
    }

    public @NotNull Text getDescription() {
        return Text.translatable("achievetodo.blocked_message." + getName() + ".description");
    }
}
