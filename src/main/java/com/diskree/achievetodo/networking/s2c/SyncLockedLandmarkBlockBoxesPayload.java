package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record SyncLockedLandmarkBlockBoxesPayload(
    @NotNull Map<LandmarkType, List<BlockBox>> blockBoxesByLockedLandmark
) implements CustomPayload {

    public static final CustomPayload.Id<SyncLockedLandmarkBlockBoxesPayload> ID =
        new CustomPayload.Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_locked_landmark_block_boxes"));

    public static final PacketCodec<PacketByteBuf, SyncLockedLandmarkBlockBoxesPayload> CODEC =
        CustomPayload.codecOf(SyncLockedLandmarkBlockBoxesPayload::write, SyncLockedLandmarkBlockBoxesPayload::new);

    private SyncLockedLandmarkBlockBoxesPayload(@NotNull PacketByteBuf buf) {
        this(readMap(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(blockBoxesByLockedLandmark.size());
        for (Map.Entry<LandmarkType, List<BlockBox>> lockedLandmarkEntry : blockBoxesByLockedLandmark.entrySet()) {
            buf.writeEnumConstant(lockedLandmarkEntry.getKey());
            List<BlockBox> blockBoxes = lockedLandmarkEntry.getValue();
            buf.writeInt(blockBoxes.size());
            for (BlockBox blockBox : blockBoxes) {
                buf.writeInt(blockBox.getMinX());
                buf.writeInt(blockBox.getMinY());
                buf.writeInt(blockBox.getMinZ());
                buf.writeInt(blockBox.getMaxX());
                buf.writeInt(blockBox.getMaxY());
                buf.writeInt(blockBox.getMaxZ());
            }
        }
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull Map<LandmarkType, List<BlockBox>> readMap(@NotNull PacketByteBuf buf) {
        Map<LandmarkType, List<BlockBox>> map = new EnumMap<>(LandmarkType.class);
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(
                buf.readEnumConstant(LandmarkType.class),
                readList(buf)
            );
        }
        return map;
    }

    private static @NotNull List<BlockBox> readList(@NotNull PacketByteBuf buf) {
        List<BlockBox> list = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            list.add(new BlockBox(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
            ));
        }
        return list;
    }
}

