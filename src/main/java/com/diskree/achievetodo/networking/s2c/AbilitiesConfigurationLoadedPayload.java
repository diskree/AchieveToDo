package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record AbilitiesConfigurationLoadedPayload(
    @NotNull Map<AbilityType, Integer> abilitiesConfiguration
) implements CustomPayload {

    public static final Id<AbilitiesConfigurationLoadedPayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier(AbilitiesConfigurationLoadedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, AbilitiesConfigurationLoadedPayload> CODEC =
        CustomPayload.codecOf(AbilitiesConfigurationLoadedPayload::write, AbilitiesConfigurationLoadedPayload::new);

    private AbilitiesConfigurationLoadedPayload(@NotNull PacketByteBuf buf) {
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
        int size = buf.readInt();
        Map<AbilityType, Integer> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(
                buf.readEnumConstant(AbilityType.class),
                buf.readInt()
            );
        }
        return map;
    }
}
