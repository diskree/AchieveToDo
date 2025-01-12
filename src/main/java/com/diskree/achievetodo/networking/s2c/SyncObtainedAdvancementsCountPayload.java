package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record SyncObtainedAdvancementsCountPayload(int count) implements CustomPayload {

    public static final Id<SyncObtainedAdvancementsCountPayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier("sync_obtained_advancements_count"));

    public static final PacketCodec<PacketByteBuf, SyncObtainedAdvancementsCountPayload> CODEC =
        CustomPayload.codecOf(SyncObtainedAdvancementsCountPayload::write, SyncObtainedAdvancementsCountPayload::new);

    private SyncObtainedAdvancementsCountPayload(@NotNull PacketByteBuf buf) {
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
