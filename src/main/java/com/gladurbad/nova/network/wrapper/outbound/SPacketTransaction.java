package com.gladurbad.nova.network.wrapper.outbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;

public class SPacketTransaction extends WrappedPacket {

    public SPacketTransaction(PacketPlayOutTransaction instance) {
        super(instance, PacketPlayOutTransaction.class);
    }

    public int getWindowId() {
        return getField("a");
    }

    public short getActionNumber() {
        return getField("b");
    }

    public boolean isAccepted() {
        return getField("c");
    }
}
