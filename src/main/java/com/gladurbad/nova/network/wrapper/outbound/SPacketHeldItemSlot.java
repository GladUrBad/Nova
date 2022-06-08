package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;

public class SPacketHeldItemSlot extends WrappedPacket {

    public SPacketHeldItemSlot(PacketPlayOutHeldItemSlot instance) {
        super(instance, PacketPlayOutHeldItemSlot.class);
    }

    public int getSlot() {
        return getField("a");
    }
}
