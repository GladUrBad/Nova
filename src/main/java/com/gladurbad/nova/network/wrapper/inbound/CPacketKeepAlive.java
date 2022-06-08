package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;

public class CPacketKeepAlive extends WrappedPacket {

    public CPacketKeepAlive(PacketPlayInKeepAlive instance) {
        super(instance, PacketPlayInKeepAlive.class);
    }

    public int getId() {
        return getField("a");
    }
}
