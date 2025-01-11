package com.diskree.achievetodo.networking.c2s;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record DemystifyAbilityTypePayload(@NotNull AbilityType ability) implements CustomPayload {

    public static final Id<DemystifyAbilityTypePayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier(DemystifyAbilityTypePayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, DemystifyAbilityTypePayload> CODEC =
        CustomPayload.codecOf(DemystifyAbilityTypePayload::write, DemystifyAbilityTypePayload::new);

    private DemystifyAbilityTypePayload(@NotNull PacketByteBuf buf) {
        this(buf.readEnumConstant(AbilityType.class));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(ability);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
