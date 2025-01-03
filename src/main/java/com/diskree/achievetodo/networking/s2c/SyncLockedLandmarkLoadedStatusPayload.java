package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.NotNull;

public record SyncLockedLandmarkLoadedStatusPayload(
    LandmarkType landmark,
    BlockBox blockBox,
    boolean isLoaded
) implements CustomPayload {

    public static final Id<SyncLockedLandmarkLoadedStatusPayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_locked_landmark_loaded_status"));

    public static final PacketCodec<PacketByteBuf, SyncLockedLandmarkLoadedStatusPayload> CODEC =
        CustomPayload.codecOf(
            SyncLockedLandmarkLoadedStatusPayload::write,
            SyncLockedLandmarkLoadedStatusPayload::new
        );

    public SyncLockedLandmarkLoadedStatusPayload(@NotNull PacketByteBuf buf) {
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
        buf.writeBoolean(isLoaded);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
