package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record NotifyScoreProgressChangedPayload(TrackedScoreType progressType, int progress) implements CustomPayload {

    public static final Id<NotifyScoreProgressChangedPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier(NotifyScoreProgressChangedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, NotifyScoreProgressChangedPayload> CODEC =
        CustomPayload.codecOf(NotifyScoreProgressChangedPayload::write, NotifyScoreProgressChangedPayload::new);

    private NotifyScoreProgressChangedPayload(@NotNull PacketByteBuf buf) {
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
