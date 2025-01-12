package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.packet.CustomPayload.codecOf;

public record SyncObtainedAdvancementsCountPayload(
    int obtainedAdvancementsCount
) implements CustomPayload {

    public static final Id<SyncObtainedAdvancementsCountPayload> ID = new Id<>(AchieveToDoMod.getIdentifier(
        SyncObtainedAdvancementsCountPayload.class.getName()
    ));

    public static final PacketCodec<PacketByteBuf, SyncObtainedAdvancementsCountPayload> CODEC = codecOf(
        SyncObtainedAdvancementsCountPayload::encode,
        SyncObtainedAdvancementsCountPayload::decode
    );

    @Override
    public Id<?> getId() {
        return ID;
    }

    private void encode(@NotNull PacketByteBuf buf) {
        buf.writeInt(obtainedAdvancementsCount);
    }

    private static @NotNull SyncObtainedAdvancementsCountPayload decode(@NotNull PacketByteBuf buf) {
        int obtainedAdvancementsCount = buf.readInt();
        return new SyncObtainedAdvancementsCountPayload(obtainedAdvancementsCount);
    }
}
