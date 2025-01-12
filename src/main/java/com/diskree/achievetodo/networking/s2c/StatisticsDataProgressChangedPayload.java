package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.tracking.TrackedStatisticsDataType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.packet.CustomPayload.codecOf;

public record StatisticsDataProgressChangedPayload(
    @NotNull TrackedStatisticsDataType trackedStatisticsDataType,
    int newProgress
) implements CustomPayload {

    public static final Id<StatisticsDataProgressChangedPayload> ID = new Id<>(AchieveToDoMod.getIdentifier(
        StatisticsDataProgressChangedPayload.class.getName()
    ));

    public static final PacketCodec<PacketByteBuf, StatisticsDataProgressChangedPayload> CODEC = codecOf(
        StatisticsDataProgressChangedPayload::encode,
        StatisticsDataProgressChangedPayload::decode
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void encode(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(trackedStatisticsDataType);
        buf.writeInt(newProgress);
    }

    private static @NotNull StatisticsDataProgressChangedPayload decode(@NotNull PacketByteBuf buf) {
        TrackedStatisticsDataType trackedStatisticsDataType = buf.readEnumConstant(TrackedStatisticsDataType.class);
        int progress = buf.readInt();
        return new StatisticsDataProgressChangedPayload(trackedStatisticsDataType, progress);
    }
}
