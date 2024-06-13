package com.diskree.achievetodo.networking;

import com.diskree.achievetodo.BuildConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record ScoreSyncPayload(int score) implements CustomPayload {

    public static final Id<ScoreSyncPayload> ID =
        new CustomPayload.Id<>(Identifier.of(BuildConfig.MOD_ID, "score"));

    public static final PacketCodec<PacketByteBuf, ScoreSyncPayload> CODEC =
        CustomPayload.codecOf(ScoreSyncPayload::write, ScoreSyncPayload::new);

    private ScoreSyncPayload(@NotNull PacketByteBuf buf) {
        this(buf.readInt());
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(score);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
