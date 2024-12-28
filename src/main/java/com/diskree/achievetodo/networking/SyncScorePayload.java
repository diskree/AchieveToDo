package com.diskree.achievetodo.networking;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.TrackedScoreType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record SyncScorePayload(TrackedScoreType progressType, int progress) implements CustomPayload {

    public static final Id<SyncScorePayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_score"));

    public static final PacketCodec<PacketByteBuf, SyncScorePayload> CODEC =
        CustomPayload.codecOf(SyncScorePayload::write, SyncScorePayload::new);

    private SyncScorePayload(@NotNull PacketByteBuf buf) {
        this(
            buf.readEnumConstant(TrackedScoreType.class),
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
