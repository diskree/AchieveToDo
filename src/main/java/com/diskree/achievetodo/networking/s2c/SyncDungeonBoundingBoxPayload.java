package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.DungeonType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.NotNull;

public record SyncDungeonBoundingBoxPayload(DungeonType dungeon, BlockBox boundingBox) implements CustomPayload {

    public static final Id<SyncDungeonBoundingBoxPayload> ID =
        new CustomPayload.Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_structure"));

    public static final PacketCodec<PacketByteBuf, SyncDungeonBoundingBoxPayload> CODEC =
        CustomPayload.codecOf(SyncDungeonBoundingBoxPayload::write, SyncDungeonBoundingBoxPayload::new);

    public SyncDungeonBoundingBoxPayload(@NotNull PacketByteBuf buf) {
        this(
            buf.readEnumConstant(DungeonType.class),
            new BlockBox(
                buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readInt(), buf.readInt(), buf.readInt()
            )
        );
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(dungeon);
        buf.writeInt(boundingBox.getMinX());
        buf.writeInt(boundingBox.getMinY());
        buf.writeInt(boundingBox.getMinZ());
        buf.writeInt(boundingBox.getMaxX());
        buf.writeInt(boundingBox.getMaxY());
        buf.writeInt(boundingBox.getMaxZ());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
