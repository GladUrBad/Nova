package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;

public class CPacketHeldItemSlot extends WrappedPacket {

    public CPacketHeldItemSlot(PacketPlayInHeldItemSlot instance) {
        super(instance, PacketPlayInHeldItemSlot.class);
    }

    public int getSlot() {
        return getField("itemInHandIndex");
    }
}
