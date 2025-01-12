package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.packet.CustomPayload.codecOf;

public record ScoreProgressChangedPayload(
    @NotNull TrackedScoreType progressType,
    int progress
) implements CustomPayload {

    public static final Id<ScoreProgressChangedPayload> ID = new Id<>(AchieveToDoMod.getIdentifier(
        ScoreProgressChangedPayload.class.getName()
    ));

    public static final PacketCodec<PacketByteBuf, ScoreProgressChangedPayload> CODEC = codecOf(
        ScoreProgressChangedPayload::encode,
        ScoreProgressChangedPayload::decode
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void encode(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(progressType);
        buf.writeInt(progress);
    }

    private static @NotNull ScoreProgressChangedPayload decode(@NotNull PacketByteBuf buf) {
        TrackedScoreType trackedScoreType = buf.readEnumConstant(TrackedScoreType.class);
        int progress = buf.readInt();
        return new ScoreProgressChangedPayload(trackedScoreType, progress);
    }
}
