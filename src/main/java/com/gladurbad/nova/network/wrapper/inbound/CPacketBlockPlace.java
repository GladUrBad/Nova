package com.gladurbad.nova.network.wrapper.inbound;

import com.gladurbad.nova.network.wrapper.WrappedPacket;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.util.Vector;

public class CPacketBlockPlace extends WrappedPacket {

    public CPacketBlockPlace(PacketPlayInBlockPlace instance) {
        super(instance, PacketPlayInBlockPlace.class);
    }

    public BlockPosition getPosition() {
        return getField("b");
    }

    public int getFace() {
        return getField("c");
    }

    public ItemStack getItemStack() {
        return getField("c");
    }

    public Vector getFaceVector() {
        float x = getField("e");
        float y = getField("f");
        float z = getField("g");

        return new Vector(x, y, z);
    }
}
