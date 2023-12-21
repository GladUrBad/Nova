package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;

public class CPacketChat extends WrappedPacket {

    public CPacketChat(Packet<?> instance) {
        super(instance, PacketPlayInChat.class);
    }

    public String getMessage() {
        return getField("a");
    }
}
