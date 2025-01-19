package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

import static net.minecraft.network.packet.CustomPayload.codecOf;

public record SyncAbilitiesConfigurationPayload(
    @NotNull Map<AbilityType, Integer> abilitiesConfiguration
) implements CustomPayload {

    public static final Id<SyncAbilitiesConfigurationPayload> ID = new Id<>(AchieveToDoMod.getIdentifier(
        SyncAbilitiesConfigurationPayload.class.getName().toLowerCase(Locale.ROOT)
    ));

    public static final PacketCodec<PacketByteBuf, SyncAbilitiesConfigurationPayload> CODEC = codecOf(
        SyncAbilitiesConfigurationPayload::encode,
        SyncAbilitiesConfigurationPayload::decode
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void encode(@NotNull PacketByteBuf buf) {
        int abilityTypesSize = abilitiesConfiguration.size();
        buf.writeInt(abilityTypesSize);
        for (var entry : abilitiesConfiguration.entrySet()) {
            buf.writeEnumConstant(entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    private static @NotNull SyncAbilitiesConfigurationPayload decode(@NotNull PacketByteBuf buf) {
        Map<AbilityType, Integer> abilitiesConfiguration = new Object2IntOpenHashMap<>();
        int abilityTypesSize = buf.readInt();
        for (int i = 0; i < abilityTypesSize; i++) {
            AbilityType abilityType = buf.readEnumConstant(AbilityType.class);
            int requiredCount = buf.readInt();
            abilitiesConfiguration.put(abilityType, requiredCount);
        }
        return new SyncAbilitiesConfigurationPayload(abilitiesConfiguration);
    }
}
