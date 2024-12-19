package com.diskree.achievetodo.networking;

import com.diskree.achievetodo.BuildConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record SyncAdvancementsCountPayload(int count) implements CustomPayload {

    public static final Id<SyncAdvancementsCountPayload> ID =
        new CustomPayload.Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_advancements_count"));

    public static final PacketCodec<PacketByteBuf, SyncAdvancementsCountPayload> CODEC =
        CustomPayload.codecOf(SyncAdvancementsCountPayload::write, SyncAdvancementsCountPayload::new);

    private SyncAdvancementsCountPayload(@NotNull PacketByteBuf buf) {
        this(buf.readInt());
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(count);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
