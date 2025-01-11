package com.diskree.achievetodo.networking.c2s;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record DemystifyRandomAdvancementCriterionPayload(@NotNull Identifier advancementId) implements CustomPayload {

    public static final Id<DemystifyRandomAdvancementCriterionPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier(DemystifyRandomAdvancementCriterionPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, DemystifyRandomAdvancementCriterionPayload> CODEC =
        CustomPayload.codecOf(
            DemystifyRandomAdvancementCriterionPayload::write,
            DemystifyRandomAdvancementCriterionPayload::new
        );

    private DemystifyRandomAdvancementCriterionPayload(@NotNull PacketByteBuf buf) {
        this(buf.readIdentifier());
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeIdentifier(advancementId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
