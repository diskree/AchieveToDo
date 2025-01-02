package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.NotNull;

public record SyncLockedLandmarkBoxPayload(
    LandmarkType landmark,
    BlockBox blockBox,
    boolean add
) implements CustomPayload {

    public static final Id<SyncLockedLandmarkBoxPayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_locked_landmark_box"));

    public static final PacketCodec<PacketByteBuf, SyncLockedLandmarkBoxPayload> CODEC =
        CustomPayload.codecOf(
            SyncLockedLandmarkBoxPayload::write,
            SyncLockedLandmarkBoxPayload::new
        );

    public SyncLockedLandmarkBoxPayload(@NotNull PacketByteBuf buf) {
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
        buf.writeBoolean(add);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
