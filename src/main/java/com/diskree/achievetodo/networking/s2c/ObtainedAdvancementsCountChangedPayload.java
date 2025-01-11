package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record ObtainedAdvancementsCountChangedPayload(int count) implements CustomPayload {

    public static final Id<ObtainedAdvancementsCountChangedPayload> ID =
        new CustomPayload.Id<>(AchieveToDoMod.getIdentifier(ObtainedAdvancementsCountChangedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, ObtainedAdvancementsCountChangedPayload> CODEC =
        CustomPayload.codecOf(ObtainedAdvancementsCountChangedPayload::write, ObtainedAdvancementsCountChangedPayload::new);

    private ObtainedAdvancementsCountChangedPayload(@NotNull PacketByteBuf buf) {
        this(buf.readInt());
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeInt(count);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
