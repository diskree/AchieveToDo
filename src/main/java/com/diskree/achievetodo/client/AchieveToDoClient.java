package com.diskree.achievetodo.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.*;
import com.diskree.achievetodo.client.gui.LockedLandmarkBox;
import com.diskree.achievetodo.networking.c2s.DemystifyAbilityPayload;
import com.diskree.achievetodo.networking.s2c.*;
import com.diskree.achievetodo.server.Constants;
import com.diskree.achievetodo.tracking.TrackedNearbyEntitiesType;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import com.diskree.achievetodo.tracking.TrackedStatType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AchieveToDoClient implements ClientModInitializer {

    private static Map<AbilityType, Integer> abilitiesConfiguration;
    private static int obtainedAdvancementsCount = Integer.MIN_VALUE;

    private static final Map<LandmarkType, List<DimensionalBlockBox>> lockedLandmarkBlockBoxes = new HashMap<>();
    private static List<LockedLandmarkBox> lockedLandmarksBoxes = new ArrayList<>();

    private static final Map<TrackedScoreType, Integer> trackedScores = new HashMap<>();
    private static final Map<TrackedStatType, Integer> trackedStats = new HashMap<>();

    private static final List<List<AbilityType>> abilityRows = new ArrayList<>();

    public static int getRequiredAdvancementsCount(AbilityType ability) {
        return abilitiesConfiguration.get(ability);
    }

    public static boolean isNotReady() {
        return abilitiesConfiguration == null || obtainedAdvancementsCount == Integer.MIN_VALUE;
    }

    public static int getObtainedAdvancementsCount() {
        return obtainedAdvancementsCount;
    }

    public static int getTrackedScore(@NotNull TrackedScoreType progressType) {
        return trackedScores.getOrDefault(progressType, 0);
    }

    public static int getTrackedStat(@NotNull TrackedStatType statType) {
        return trackedStats.getOrDefault(statType, 0);
    }

    public static List<LockedLandmarkBox> getLockedLandmarkBoxes() {
        return lockedLandmarksBoxes;
    }

    public static int getTrackedNearbyEntitiesCount(TrackedNearbyEntitiesType type) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator()) {
            return 0;
        }
        int radius = type.getRadius();
        Vec3d playerPos = player.getPos();
        Box area = new Box(playerPos, playerPos).expand(radius);
        List<EntityType<?>> trackedEntities = type.getEntities();
        boolean isBabySeparated = type.isBabySeparated();
        Map<EntityType<?>, Map<Boolean, Boolean>> trackedMap = new HashMap<>();
        for (EntityType<?> entityType : trackedEntities) {
            Map<Boolean, Boolean> babyAndAdultMap = new HashMap<>();
            babyAndAdultMap.put(false, false);
            if (isBabySeparated) {
                babyAndAdultMap.put(true, false);
            }
            trackedMap.put(entityType, babyAndAdultMap);
        }
        int entitiesCount = 0;
        List<Entity> entities = player.getWorld().getEntitiesByType(
            TypeFilter.instanceOf(Entity.class),
            area,
            entity -> trackedEntities.contains(entity.getType())
        );
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (playerPos.distanceTo(entity.getPos()) > radius) {
                    continue;
                }
                Map<Boolean, Boolean> stateMap = trackedMap.get(entity.getType());
                if (stateMap != null) {
                    boolean isBaby = isBabySeparated && livingEntity.isBaby();
                    if (!stateMap.getOrDefault(isBaby, false)) {
                        stateMap.put(isBaby, true);
                        entitiesCount++;
                    }
                }
            }
        }
        return entitiesCount;
    }

    public static @NotNull MutableText translate(String keySuffix, Object... args) {
        return Text.translatable(BuildConfig.MOD_ID + "." + keySuffix, args);
    }

    @Override
    public void onInitializeClient() {
        registerPayloads();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            abilitiesConfiguration = null;
            obtainedAdvancementsCount = Integer.MIN_VALUE;
            lockedLandmarkBlockBoxes.clear();
            lockedLandmarksBoxes.clear();
            trackedScores.clear();
            trackedStats.clear();
        });
    }

    private void registerPayloads() {
        ClientPlayNetworking.registerGlobalReceiver(SyncAbilitiesConfigurationPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                abilitiesConfiguration = payload.abilitiesConfiguration();
                abilityRows.clear();
                calculateLockedLandmarksBoxes();
            })
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncObtainedAdvancementsCountPayload.ID, (payload, context) ->
            context.client().execute(() -> obtainedAdvancementsCount = payload.count())
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncLockedLandmarksPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                boolean isChanged = false;
                for (var entry : payload.landmarks().entrySet()) {
                    LandmarkType landmarkType = entry.getKey();
                    if (payload.isLocked()) {
                        if (lockedLandmarkBlockBoxes
                            .computeIfAbsent(landmarkType, k -> new ArrayList<>())
                            .addAll(entry.getValue())
                        ) {
                            isChanged = true;
                        }
                    } else {
                        List<DimensionalBlockBox> dimensionalBoxes = lockedLandmarkBlockBoxes.get(landmarkType);
                        if (dimensionalBoxes != null && dimensionalBoxes.removeAll(entry.getValue())) {
                            isChanged = true;
                            if (dimensionalBoxes.isEmpty()) {
                                lockedLandmarkBlockBoxes.remove(landmarkType);
                            }
                        }
                    }
                }
                if (isChanged) {
                    calculateLockedLandmarksBoxes();
                }
            })
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncLandmarkTypesUnlockedPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                boolean isChanged = false;
                for (LandmarkType landmarkType : payload.landmarks()) {
                    if (lockedLandmarkBlockBoxes.remove(landmarkType) != null) {
                        isChanged = true;
                    }
                }
                if (isChanged) {
                    calculateLockedLandmarksBoxes();
                }
            })
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncResizedLandmarkPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                List<DimensionalBlockBox> dimensionalBlockBoxes = lockedLandmarkBlockBoxes.get(payload.landmarkType());
                if (dimensionalBlockBoxes.remove(payload.oldDimensionalBlockBox())) {
                    dimensionalBlockBoxes.add(payload.newDimensionalBlockBox());
                    calculateLockedLandmarksBoxes();
                }
            })
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncScorePayload.ID, (payload, context) ->
            context.client().execute(() -> trackedScores.put(payload.progressType(), payload.progress()))
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncStatPayload.ID, (payload, context) ->
            context.client().execute(() -> trackedStats.put(payload.statType(), payload.progress()))
        );
    }

    private void calculateLockedLandmarksBoxes() {
        if (abilitiesConfiguration == null) {
            return;
        }
        List<LockedLandmarkBox> result = new ArrayList<>();
        for (var entry : lockedLandmarkBlockBoxes.entrySet()) {
            LandmarkType landmarkType = entry.getKey();
            for (DimensionalBlockBox dimensionalBlockBox : entry.getValue()) {
                result.add(new LockedLandmarkBox(
                    landmarkType,
                    dimensionalBlockBox.dimensionType(),
                    Box.from(dimensionalBlockBox.blockBox())
                ));
            }
        }
        result.sort((lockedLandmarkBox, otherLockedLandmarkBox) -> {
            Box box = lockedLandmarkBox.box();
            Box otherBox = otherLockedLandmarkBox.box();
            double volume = box.getLengthX() * box.getLengthY() * box.getLengthZ();
            double otherVolume = otherBox.getLengthX() * otherBox.getLengthY() * otherBox.getLengthZ();
            int compared = Double.compare(otherVolume, volume);
            if (compared != 0) {
                return compared;
            }
            LandmarkType landmarkType = lockedLandmarkBox.landmarkType();
            LandmarkType otherLandmarkType = otherLockedLandmarkBox.landmarkType();
            AbilityType abilityType = AbilityType.findByLandmarkType(landmarkType);
            AbilityType otherAbilityType = AbilityType.findByLandmarkType(otherLandmarkType);

            int requiredCount = abilitiesConfiguration.get(abilityType);
            int otherRequiredCount = abilitiesConfiguration.get(otherAbilityType);
            if (requiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG) {
                requiredCount = Integer.MIN_VALUE;
            }
            if (otherRequiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG) {
                otherRequiredCount = Integer.MIN_VALUE;
            }
            if (requiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG) {
                requiredCount = Integer.MAX_VALUE;
            }
            if (otherRequiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG) {
                otherRequiredCount = Integer.MAX_VALUE;
            }
            compared = Integer.compare(otherRequiredCount, requiredCount);
            if (compared != 0) {
                return compared;
            }
            return Integer.compare(landmarkType.ordinal(), otherLandmarkType.ordinal());
        });
        lockedLandmarksBoxes = result;
    }

    public static boolean isAbilityLocked(AbilityType ability) {
        return isAbilityLocked(ability, false);
    }

    public static boolean isAbilityLocked(AbilityType ability, boolean checkOnly) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (ability == null || player == null || player.isCreative() || player.isSpectator()) {
            return false;
        }
        if (isNotReady()) {
            return true;
        }
        int requiredCount = abilitiesConfiguration.get(ability);
        if (requiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG ||
            requiredCount != Constants.Progression.PERMANENTLY_LOCKED_FLAG && obtainedAdvancementsCount >= requiredCount
        ) {
            return false;
        }
        if (!checkOnly) {
            Text lockedMessageText;
            if (requiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG) {
                lockedMessageText = ability.buildPermanentlyLockedMessage();
            } else {
                int leftCount = requiredCount - obtainedAdvancementsCount;
                lockedMessageText = ability.buildUnlockProgressMessage(leftCount);
            }
            player.sendMessage(lockedMessageText, true);
            ClientPlayNetworking.send(new DemystifyAbilityPayload(ability));
        }
        return true;
    }

    public static boolean isInLockedLandmark(DimensionType dimensionType, Box targetBox) {
        for (LockedLandmarkBox lockedLandmarksBox : lockedLandmarksBoxes) {
            if (lockedLandmarksBox.dimensionType() == dimensionType && lockedLandmarksBox.box().intersects(targetBox)) {
                return isAbilityLocked(AbilityType.findByLandmarkType(lockedLandmarksBox.landmarkType()), false);
            }
        }
        return false;
    }

    public static @Nullable List<List<AbilityType>> getAbilityRows() {
        if (isNotReady()) {
            return null;
        }
        if (abilityRows.isEmpty()) {
            Map<AbilitiesHierarchyLayerType, List<AbilityType>> abilitiesByCategory = Arrays
                .stream(AbilityType.values())
                .sorted(Comparator.comparingInt((AbilityType ability) -> {
                        int requiredCount = abilitiesConfiguration.get(ability);
                        if (requiredCount == Constants.Progression.INITIALLY_UNLOCKED_FLAG) {
                            return 0;
                        }
                        if (requiredCount == Constants.Progression.PERMANENTLY_LOCKED_FLAG) {
                            return 2;
                        }
                        return 1;
                    })
                    .thenComparingInt(abilitiesConfiguration::get)
                    .thenComparing(Enum::ordinal))
                .collect(Collectors.groupingBy(AbilityType::getHierarchyLayerType));
            for (AbilitiesHierarchyLayerType category : AbilitiesHierarchyLayerType.values()) {
                List<AbilityType> categoryAbilities = abilitiesByCategory.get(category);
                int categoryRowsCount = category.getRowsCount();
                if (categoryRowsCount == 1) {
                    abilityRows.add(categoryAbilities);
                } else {
                    if (categoryAbilities.size() % categoryRowsCount != 0) {
                        throw new IllegalStateException(
                            "Abilities in category " + category +
                                " cannot be evenly distributed across " + categoryRowsCount + " rows."
                        );
                    }
                    int rowLength = categoryAbilities.size() / categoryRowsCount;
                    List<List<AbilityType>> categoryRows = new ArrayList<>(categoryRowsCount);
                    for (int i = 0; i < categoryRowsCount; i++) {
                        categoryRows.add(new ArrayList<>(rowLength));
                    }
                    for (int i = 0; i < categoryAbilities.size(); i++) {
                        int rowIndex = i / rowLength;
                        categoryRows.get(rowIndex).add(categoryAbilities.get(i));
                    }
                    int half = categoryRowsCount / 2;
                    abilityRows.addAll(0, categoryRows.subList(0, half));
                    abilityRows.addAll(categoryRows.subList(half, categoryRowsCount));
                }
            }
        }
        return abilityRows;
    }
}
