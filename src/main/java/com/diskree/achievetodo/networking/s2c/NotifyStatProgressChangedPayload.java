package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.tracking.TrackedStatType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record NotifyStatProgressChangedPayload(TrackedStatType statType, int progress) implements CustomPayload {

    public static final Id<NotifyStatProgressChangedPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier(NotifyStatProgressChangedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, NotifyStatProgressChangedPayload> CODEC =
        CustomPayload.codecOf(NotifyStatProgressChangedPayload::write, NotifyStatProgressChangedPayload::new);

    private NotifyStatProgressChangedPayload(@NotNull PacketByteBuf buf) {
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
