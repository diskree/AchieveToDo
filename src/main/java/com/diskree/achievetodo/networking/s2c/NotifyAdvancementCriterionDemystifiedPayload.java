package com.diskree.achievetodo.networking.s2c;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record NotifyAdvancementCriterionDemystifiedPayload(
    @NotNull Identifier advancementId,
    @NotNull String criterionName
) implements CustomPayload {

    public static final Id<NotifyAdvancementCriterionDemystifiedPayload> ID =
        new Id<>(AchieveToDoMod.getIdentifier(NotifyAdvancementCriterionDemystifiedPayload.class.getName()));

    public static final PacketCodec<PacketByteBuf, NotifyAdvancementCriterionDemystifiedPayload> CODEC =
        CustomPayload.codecOf(
            NotifyAdvancementCriterionDemystifiedPayload::write,
            NotifyAdvancementCriterionDemystifiedPayload::new
        );

    private NotifyAdvancementCriterionDemystifiedPayload(@NotNull PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readString());
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeIdentifier(advancementId);
        buf.writeString(criterionName);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
