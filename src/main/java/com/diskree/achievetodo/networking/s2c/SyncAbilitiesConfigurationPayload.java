package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public record SyncAbilitiesConfigurationPayload(
    @NotNull Map<AbilityType, Integer> abilitiesConfiguration
) implements CustomPayload {

    public static final Id<SyncAbilitiesConfigurationPayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier("sync_abilities_configuration"));

    public static final PacketCodec<PacketByteBuf, SyncAbilitiesConfigurationPayload> CODEC =
        CustomPayload.codecOf(SyncAbilitiesConfigurationPayload::write, SyncAbilitiesConfigurationPayload::new);

    private SyncAbilitiesConfigurationPayload(@NotNull PacketByteBuf buf) {
        this(readMap(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(abilitiesConfiguration.size());
        for (var entry : abilitiesConfiguration.entrySet()) {
            buf.writeEnumConstant(entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull Map<AbilityType, Integer> readMap(@NotNull PacketByteBuf buf) {
        Map<AbilityType, Integer> map = new EnumMap<>(AbilityType.class);
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(
                buf.readEnumConstant(AbilityType.class),
                buf.readInt()
            );
        }
        return map;
    }
}
