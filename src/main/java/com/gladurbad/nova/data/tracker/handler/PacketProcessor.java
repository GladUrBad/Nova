package com.gladurbad.nova.data.tracker.handler;

import com.gladurbad.nova.network.wrapper.WrappedPacket;

public interface PacketProcessor {
    // Processes a packet before the check chain for trackers.
    void process(WrappedPacket packet);
}
