package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SyncLandmarkTypesUnlockedPayload(List<LandmarkType> landmarks) implements CustomPayload {

    public static final Id<SyncLandmarkTypesUnlockedPayload> ID =
        new Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_landmark_types_unlocked"));

    public static final PacketCodec<PacketByteBuf, SyncLandmarkTypesUnlockedPayload> CODEC =
        CustomPayload.codecOf(SyncLandmarkTypesUnlockedPayload::write, SyncLandmarkTypesUnlockedPayload::new);

    private SyncLandmarkTypesUnlockedPayload(@NotNull PacketByteBuf buf) {
        this(readList(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(landmarks.size());
        for (LandmarkType landmark : landmarks) {
            buf.writeEnumConstant(landmark);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull List<LandmarkType> readList(@NotNull PacketByteBuf buf) {
        int size = buf.readInt();
        List<LandmarkType> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(buf.readEnumConstant(LandmarkType.class));
        }
        return list;
    }
}
