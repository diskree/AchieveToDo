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

public record SyncResizedLandmarkPayload(
    LandmarkType landmarkType,
    DimensionalBlockBox oldDimensionalBlockBox,
    DimensionalBlockBox newDimensionalBlockBox
) implements CustomPayload {

    public static final Id<SyncResizedLandmarkPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier("sync_resized_landmark"));

    public static final PacketCodec<PacketByteBuf, SyncResizedLandmarkPayload> CODEC =
        CustomPayload.codecOf(SyncResizedLandmarkPayload::write, SyncResizedLandmarkPayload::new);

    private SyncResizedLandmarkPayload(@NotNull PacketByteBuf buf) {
        this(
            buf.readEnumConstant(LandmarkType.class),
            new DimensionalBlockBox(
                buf.readEnumConstant(DimensionType.class),
                new BlockBox(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
                )
            ),
            new DimensionalBlockBox(
                buf.readEnumConstant(DimensionType.class),
                new BlockBox(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
                )
            )
        );
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(landmarkType);

        buf.writeEnumConstant(oldDimensionalBlockBox.dimensionType());
        BlockBox oldBlockBox = oldDimensionalBlockBox.blockBox();
        buf.writeInt(oldBlockBox.getMinX());
        buf.writeInt(oldBlockBox.getMinY());
        buf.writeInt(oldBlockBox.getMinZ());
        buf.writeInt(oldBlockBox.getMaxX());
        buf.writeInt(oldBlockBox.getMaxY());
        buf.writeInt(oldBlockBox.getMaxZ());

        buf.writeEnumConstant(newDimensionalBlockBox.dimensionType());
        BlockBox newBlockBox = newDimensionalBlockBox.blockBox();
        buf.writeInt(newBlockBox.getMinX());
        buf.writeInt(newBlockBox.getMinY());
        buf.writeInt(newBlockBox.getMinZ());
        buf.writeInt(newBlockBox.getMaxX());
        buf.writeInt(newBlockBox.getMaxY());
        buf.writeInt(newBlockBox.getMaxZ());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
