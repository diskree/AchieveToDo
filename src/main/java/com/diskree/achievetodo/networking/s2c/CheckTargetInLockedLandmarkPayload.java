package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.DimensionType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.packet.CustomPayload.codecOf;

public record CheckTargetInLockedLandmarkPayload(
    @NotNull DimensionType targetDimensionType,
    @NotNull Box targetBox
) implements CustomPayload {

    public static final Id<CheckTargetInLockedLandmarkPayload> ID = new Id<>(AchieveToDoMod.getIdentifier(
        CheckTargetInLockedLandmarkPayload.class.getName()
    ));

    public static final PacketCodec<PacketByteBuf, CheckTargetInLockedLandmarkPayload> CODEC = codecOf(
        CheckTargetInLockedLandmarkPayload::encode,
        CheckTargetInLockedLandmarkPayload::decode
    );

    @Override
    public Id<?> getId() {
        return ID;
    }

    private void encode(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(targetDimensionType);
        double minX = targetBox.minX;
        double minY = targetBox.minY;
        double minZ = targetBox.minZ;
        double maxX = targetBox.maxX;
        double maxY = targetBox.maxY;
        double maxZ = targetBox.maxZ;
        buf.writeDouble(minX);
        buf.writeDouble(minY);
        buf.writeDouble(minZ);
        buf.writeDouble(maxX);
        buf.writeDouble(maxY);
        buf.writeDouble(maxZ);
    }

    private static @NotNull CheckTargetInLockedLandmarkPayload decode(@NotNull PacketByteBuf buf) {
        DimensionType dimensionType = buf.readEnumConstant(DimensionType.class);
        double minX = buf.readDouble();
        double minY = buf.readDouble();
        double minZ = buf.readDouble();
        double maxX = buf.readDouble();
        double maxY = buf.readDouble();
        double maxZ = buf.readDouble();
        Box box = new Box(
            minX,
            minY,
            minZ,
            maxX,
            maxY,
            maxZ
        );
        return new CheckTargetInLockedLandmarkPayload(dimensionType, box);
    }
}
