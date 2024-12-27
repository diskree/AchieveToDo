package com.diskree.achievetodo.networking;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.DynamicProgressType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record SyncDynamicProgressPayload(
    DynamicProgressType progressType,
    int progress
) implements CustomPayload {

    public static final Id<SyncDynamicProgressPayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_dynamic_progress"));

    public static final PacketCodec<PacketByteBuf, SyncDynamicProgressPayload> CODEC =
        CustomPayload.codecOf(SyncDynamicProgressPayload::write, SyncDynamicProgressPayload::new);

    private SyncDynamicProgressPayload(@NotNull PacketByteBuf buf) {
        this(
            buf.readEnumConstant(DynamicProgressType.class),
            buf.readInt()
        );
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(progressType);
        buf.writeInt(progress);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
