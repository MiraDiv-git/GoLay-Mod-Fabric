package dev.miradiv.golay.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LayPacket(boolean laying) implements CustomPayload
{
    public static final Id<LayPacket> ID = new Id<>(Identifier.of("golay", "lay_state"));
    public static final PacketCodec<PacketByteBuf, LayPacket> CODEC =
            PacketCodec.of(
                    (value, buf) -> buf.writeBoolean(value.laying()),
                    buf -> new LayPacket(buf.readBoolean())
            );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
