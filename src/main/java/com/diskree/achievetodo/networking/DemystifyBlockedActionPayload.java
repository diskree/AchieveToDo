package com.diskree.achievetodo.networking;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record DemystifyBlockedActionPayload(
    @NotNull BlockedActionType blockedAction
) implements CustomPayload {

    public static final Id<DemystifyBlockedActionPayload> ID =
        new CustomPayload.Id<>(Identifier.of(BuildConfig.MOD_ID, "demystify_blocked_action"));

    public static final PacketCodec<PacketByteBuf, DemystifyBlockedActionPayload> CODEC =
        CustomPayload.codecOf(DemystifyBlockedActionPayload::write, DemystifyBlockedActionPayload::new);

    private DemystifyBlockedActionPayload(@NotNull PacketByteBuf buf) {
        this(buf.readEnumConstant(BlockedActionType.class));
    }

    private void write(@NotNull PacketByteBuf buf) {
        buf.writeEnumConstant(blockedAction);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
