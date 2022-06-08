package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutKeepAlive;

public class SPacketKeepAlive extends WrappedPacket {

    public SPacketKeepAlive(PacketPlayOutKeepAlive instance) {
        super(instance, PacketPlayOutKeepAlive.class);
    }

    public int getId() {
        return getField("a");
    }


}
