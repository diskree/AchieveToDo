package com.diskree.achievetodo;

import com.diskree.achievetodo.advancements.AchieveToDoToast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class AchieveToDoMod implements ModInitializer {

    public static final String ID = "achievetodo";
    public static final String ADVANCEMENT_PATH_PREFIX = "action/";
    public static final String ADVANCEMENT_CRITERIA_PREFIX = "action_";
    public static final Item MYSTERY_MASK_ITEM = new Item(new FabricItemSettings().group(ItemGroup.MISC));

    private static final EnumMap<BlockedAction, Boolean> blockedActions = new EnumMap<>(BlockedAction.class);
    public static int lastAchievementsCount;

    public static void showFoodBlockedDescription(FoodComponent food) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        for (BlockedAction action : BlockedAction.values()) {
            if (action.getFoodComponent() == food) {
                MinecraftClient.getInstance().player.sendMessage(action.getLockDescription(), true);
                break;
            }
        }
    }

    public static void setAchievementsCount(int count) {
        if (count == 0 || count < lastAchievementsCount) {
            lastAchievementsCount = 0;
            blockedActions.clear();
            for (BlockedAction action : BlockedAction.values()) {
                blockedActions.put(action, false);
            }
        }
        if (lastAchievementsCount == count) {
            return;
        }
        int oldCount = lastAchievementsCount;
        lastAchievementsCount = count;
        List<BlockedAction> actionsToUnlock = new ArrayList<>();
        for (BlockedAction action : BlockedAction.values()) {
            if (count >= action.getAchievementsCountToUnlock()) {
                if (oldCount < action.getAchievementsCountToUnlock() && oldCount != 0) {
                    actionsToUnlock.add(action);
                }
                blockedActions.put(action, true);
            }
        }
        IntegratedServer server = MinecraftClient.getInstance().getServer();
        if (server == null) {
            return;
        }
        for (BlockedAction action : actionsToUnlock) {
            Advancement advancement = server.getAdvancementLoader().get(action.buildAdvancementId());
            MinecraftClient.getInstance().getToastManager().add(new AchieveToDoToast(advancement, action));
        }
    }

    public static boolean isActionBlocked(BlockedAction action) {
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.isCreative()) {
            return false;
        }
        Boolean value = blockedActions.get(action);
        if (value != null && value) {
            return false;
        }
        MinecraftClient.getInstance().player.sendMessage(action.getLockDescription(), true);
        grantActionAdvancement(action);
        return true;
    }

    public static boolean isFoodBlocked(FoodComponent food) {
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.isCreative()) {
            return false;
        }
        for (BlockedAction action : BlockedAction.values()) {
            if (action.getFoodComponent() == food) {
                grantActionAdvancement(action);
                return action.getAchievementsCountToUnlock() > lastAchievementsCount;
            }
        }
        return false;
    }

    public static BlockedAction getBlockedActionFromAdvancement(Advancement advancement) {
        if (advancement.getId().getNamespace().equals(AchieveToDoMod.ID) && advancement.getId().getPath().startsWith(AchieveToDoMod.ADVANCEMENT_PATH_PREFIX)) {
            String key = advancement.getId().getPath().split(AchieveToDoMod.ADVANCEMENT_PATH_PREFIX)[1];
            return BlockedAction.map(key.toUpperCase());
        }
        return null;
    }

    private static void grantActionAdvancement(BlockedAction action) {
        IntegratedServer server = MinecraftClient.getInstance().getServer();
        if (server == null) {
            return;
        }
        Advancement tab = server.getAdvancementLoader().get(action.buildAdvancementId());
        if (server.getPlayerManager() != null && !server.getPlayerManager().getPlayerList().isEmpty()) {
            server.getPlayerManager().getPlayerList().get(0).getAdvancementTracker().grantCriterion(tab, ADVANCEMENT_CRITERIA_PREFIX + action.name().toLowerCase());
        }
    }

    private boolean isToolBlocked(ItemStack itemStack) {
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.isCreative()) {
            return false;
        }
        if (!itemStack.isDamageable()) {
            return false;
        }
        Item item = itemStack.getItem();
        if (item instanceof ToolItem) {
            ToolMaterial toolMaterial = ((ToolItem) item).getMaterial();
            return toolMaterial == ToolMaterials.IRON && AchieveToDoMod.isActionBlocked(BlockedAction.USING_IRON_TOOLS) ||
                    toolMaterial == ToolMaterials.DIAMOND && AchieveToDoMod.isActionBlocked(BlockedAction.USING_DIAMOND_TOOLS) ||
                    toolMaterial == ToolMaterials.NETHERITE && AchieveToDoMod.isActionBlocked(BlockedAction.USING_NETHERITE_TOOLS);
        }
        return false;
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(AchieveToDoMod.ID, "locked_action"), MYSTERY_MASK_ITEM);
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world != null && world.getRegistryKey() == World.OVERWORLD && pos != null) {
                if (pos.getY() >= 0 && isActionBlocked(BlockedAction.BREAK_BLOCKS_IN_POSITIVE_Y) || pos.getY() < 0 && isActionBlocked(BlockedAction.BREAK_BLOCKS_IN_NEGATIVE_Y)) {
                    return ActionResult.FAIL;
                }
            }
            if (isToolBlocked(player.getInventory().getMainHandStack())) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (isToolBlocked(player.getInventory().getMainHandStack())) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack itemStack = player.getInventory().getMainHandStack();
            if (isToolBlocked(itemStack)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
