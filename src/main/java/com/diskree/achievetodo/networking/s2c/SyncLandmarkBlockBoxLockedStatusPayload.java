package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.NotNull;

public record SyncLandmarkBlockBoxLockedStatusPayload(
    LandmarkType landmark,
    BlockBox blockBox,
    boolean isLocked
) implements CustomPayload {

    public static final Id<SyncLandmarkBlockBoxLockedStatusPayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_landmark_block_box_locked_status"));

    public static final PacketCodec<PacketByteBuf, SyncLandmarkBlockBoxLockedStatusPayload> CODEC =
        CustomPayload.codecOf(
            SyncLandmarkBlockBoxLockedStatusPayload::write,
            SyncLandmarkBlockBoxLockedStatusPayload::new
        );

    public SyncLandmarkBlockBoxLockedStatusPayload(@NotNull PacketByteBuf buf) {
        this(
            buf.readEnumConstant(LandmarkType.class),
            new BlockBox(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
            ),
            buf.readBoolean()
        );
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(landmark);
        buf.writeInt(blockBox.getMinX());
        buf.writeInt(blockBox.getMinY());
        buf.writeInt(blockBox.getMinZ());
        buf.writeInt(blockBox.getMaxX());
        buf.writeInt(blockBox.getMaxY());
        buf.writeInt(blockBox.getMaxZ());
        buf.writeBoolean(isLocked);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
