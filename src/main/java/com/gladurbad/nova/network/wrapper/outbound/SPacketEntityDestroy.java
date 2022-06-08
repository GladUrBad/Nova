package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

public class SPacketEntityDestroy extends WrappedPacket {

    public SPacketEntityDestroy(PacketPlayOutEntityDestroy instance) {
        super(instance, PacketPlayOutEntityDestroy.class);
    }

    public int[] getEntities() {
        return getField("a");
    }
}
