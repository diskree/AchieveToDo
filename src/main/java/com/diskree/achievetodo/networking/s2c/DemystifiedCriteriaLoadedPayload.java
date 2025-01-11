package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record DemystifiedCriteriaLoadedPayload(
    @NotNull Map<Identifier, Set<String>> demystifiedCriteria
) implements CustomPayload {

    public static final Id<DemystifiedCriteriaLoadedPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier(DemystifiedCriteriaLoadedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, DemystifiedCriteriaLoadedPayload> CODEC =
        CustomPayload.codecOf(DemystifiedCriteriaLoadedPayload::write, DemystifiedCriteriaLoadedPayload::new);

    private DemystifiedCriteriaLoadedPayload(@NotNull PacketByteBuf buf) {
        this(readMap(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(demystifiedCriteria.size());
        for (var entry : demystifiedCriteria.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            Set<String> criterionNames = entry.getValue();
            buf.writeInt(criterionNames.size());
            for (String criterionName : criterionNames) {
                buf.writeString(criterionName);
            }
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull Map<Identifier, Set<String>> readMap(@NotNull PacketByteBuf buf) {
        int mapSize = buf.readInt();
        Map<Identifier, Set<String>> map = new HashMap<>(mapSize);
        for (int mapIndex = 0; mapIndex < mapSize; mapIndex++) {
            Identifier identifier = buf.readIdentifier();
            int listSize = buf.readInt();
            Set<String> criterionNames = new HashSet<>(listSize);
            for (int listIndex = 0; listIndex < listSize; listIndex++) {
                criterionNames.add(buf.readString());
            }
            map.put(identifier, criterionNames);
        }
        return map;
    }
}
