package com.diskree.achievetodo.networking.c2s;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.AbilityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record DemystifyAbilityPayload(@NotNull AbilityType ability) implements CustomPayload {

    public static final Id<DemystifyAbilityPayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier("demystify_ability"));

    public static final PacketCodec<PacketByteBuf, DemystifyAbilityPayload> CODEC =
        CustomPayload.codecOf(DemystifyAbilityPayload::write, DemystifyAbilityPayload::new);

    private DemystifyAbilityPayload(@NotNull PacketByteBuf buf) {
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
