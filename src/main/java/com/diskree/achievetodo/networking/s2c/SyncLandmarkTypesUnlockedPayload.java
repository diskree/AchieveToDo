package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record SyncLandmarkTypesUnlockedPayload(Set<LandmarkType> landmarks) implements CustomPayload {

    public static final Id<SyncLandmarkTypesUnlockedPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier("sync_landmark_types_unlocked"));

    public static final PacketCodec<PacketByteBuf, SyncLandmarkTypesUnlockedPayload> CODEC =
        CustomPayload.codecOf(SyncLandmarkTypesUnlockedPayload::write, SyncLandmarkTypesUnlockedPayload::new);

    private SyncLandmarkTypesUnlockedPayload(@NotNull PacketByteBuf buf) {
        this(readList(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(landmarks.size());
        for (LandmarkType landmarkType : landmarks) {
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
