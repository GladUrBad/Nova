package com.gladurbad.nova.check.handler;

import com.gladurbad.nova.network.wrapper.WrappedPacket;

public interface PacketHandler {
    // Handles a packet in this check.
    void handle(WrappedPacket packet);
}
