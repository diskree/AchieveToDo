package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record NotifyLandmarkTypesUnlockedPayload(Set<LandmarkType> landmarkTypes) implements CustomPayload {

    public static final Id<NotifyLandmarkTypesUnlockedPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier(NotifyLandmarkTypesUnlockedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, NotifyLandmarkTypesUnlockedPayload> CODEC =
        CustomPayload.codecOf(NotifyLandmarkTypesUnlockedPayload::write, NotifyLandmarkTypesUnlockedPayload::new);

    private NotifyLandmarkTypesUnlockedPayload(@NotNull PacketByteBuf buf) {
        this(readList(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(landmarkTypes.size());
        for (LandmarkType landmarkType : landmarkTypes) {
            buf.writeEnumConstant(landmarkType);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull Set<LandmarkType> readList(@NotNull PacketByteBuf buf) {
        int size = buf.readInt();
        Set<LandmarkType> list = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            list.add(buf.readEnumConstant(LandmarkType.class));
        }
        return list;
    }
}
