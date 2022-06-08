package com.gladurbad.nova.data.tracker.handler;

import com.gladurbad.nova.network.wrapper.WrappedPacket;

public interface PostPacketProcessor {
    // Process a packet after the check chain for a tracker.
    void postProcess(WrappedPacket packet);
}
