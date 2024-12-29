package com.diskree.achievetodo.networking;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.BuildConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public record SyncAbilitiesConfigurationPayload(
    @NotNull Map<AbilityType, Integer> abilitiesConfiguration
) implements CustomPayload {

    public static final Id<SyncAbilitiesConfigurationPayload> ID =
        new CustomPayload.Id<>(Identifier.of(BuildConfig.MOD_ID, "sync_abilities_configuration"));

    public static final PacketCodec<PacketByteBuf, SyncAbilitiesConfigurationPayload> CODEC =
        CustomPayload.codecOf(SyncAbilitiesConfigurationPayload::write, SyncAbilitiesConfigurationPayload::new);

    private SyncAbilitiesConfigurationPayload(@NotNull PacketByteBuf buf) {
        this(readMap(buf));
    }

    private void write(@NotNull PacketByteBuf buf) {
        for (AbilityType abilityType : AbilityType.values()) {
            buf.writeInt(abilitiesConfiguration.getOrDefault(abilityType, 0));
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static @NotNull Map<AbilityType, Integer> readMap(@NotNull PacketByteBuf buf) {
        Map<AbilityType, Integer> map = new EnumMap<>(AbilityType.class);
        for (AbilityType abilityType : AbilityType.values()) {
            map.put(abilityType, buf.readInt());
        }
        return map;
    }
}
