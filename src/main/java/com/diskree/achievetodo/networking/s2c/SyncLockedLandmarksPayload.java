package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.ability.DimensionalBlockBox;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncLockedLandmarksPayload(
    @NotNull Map<LandmarkType, List<DimensionalBlockBox>> landmarks,
    boolean isLocked
) implements CustomPayload {

    public static final CustomPayload.Id<SyncLockedLandmarksPayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier("sync_locked_landmarks"));

    public static final PacketCodec<PacketByteBuf, SyncLockedLandmarksPayload> CODEC =
        CustomPayload.codecOf(SyncLockedLandmarksPayload::write, SyncLockedLandmarksPayload::new);

    private SyncLockedLandmarksPayload(@NotNull PacketByteBuf buf) {
        this(
            readMap(buf),
            buf.readBoolean()
        );
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(landmarks.size());
        for (var entry : landmarks.entrySet()) {
            buf.writeEnumConstant(entry.getKey());
            List<DimensionalBlockBox> dimensionalBlockBoxes = entry.getValue();
            buf.writeInt(dimensionalBlockBoxes.size());
            for (DimensionalBlockBox dimensionalBlockBox : dimensionalBlockBoxes) {
                buf.writeEnumConstant(dimensionalBlockBox.dimensionType());
                BlockBox blockBox = dimensionalBlockBox.blockBox();
                buf.writeInt(blockBox.getMinX());
                buf.writeInt(blockBox.getMinY());
                buf.writeInt(blockBox.getMinZ());
                buf.writeInt(blockBox.getMaxX());
                buf.writeInt(blockBox.getMaxY());
                buf.writeInt(blockBox.getMaxZ());
            }
        }
        buf.writeBoolean(isLocked);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull Map<LandmarkType, List<DimensionalBlockBox>> readMap(@NotNull PacketByteBuf buf) {
        int mapSize = buf.readInt();
        Map<LandmarkType, List<DimensionalBlockBox>> map = new HashMap<>(mapSize);
        for (int mapIndex = 0; mapIndex < mapSize; mapIndex++) {
            LandmarkType landmarkType = buf.readEnumConstant(LandmarkType.class);
            int listSize = buf.readInt();
            List<DimensionalBlockBox> dimensionalBlockBoxes = new ArrayList<>(listSize);
            for (int listIndex = 0; listIndex < listSize; listIndex++) {
                DimensionType dimension = buf.readEnumConstant(DimensionType.class);
                BlockBox blockBox = new BlockBox(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
                );
                dimensionalBlockBoxes.add(new DimensionalBlockBox(dimension, blockBox));
            }
            map.put(landmarkType, dimensionalBlockBoxes);
        }
        return map;
    }
}
