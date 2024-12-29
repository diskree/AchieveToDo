package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.tracking.TrackedStatType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record SyncStatPayload(TrackedStatType statType, int progress) implements CustomPayload {

    public static final Id<SyncStatPayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_stat"));

    public static final PacketCodec<PacketByteBuf, SyncStatPayload> CODEC =
        CustomPayload.codecOf(SyncStatPayload::write, SyncStatPayload::new);

    private SyncStatPayload(@NotNull PacketByteBuf buf) {
        this(
            buf.readEnumConstant(TrackedStatType.class),
            buf.readInt()
        );
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(statType);
        buf.writeInt(progress);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
