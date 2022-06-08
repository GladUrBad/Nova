package com.gladurbad.nova.check.handler;

import com.gladurbad.nova.util.location.PlayerLocation;

public interface PositionHandler {
    // Current and last location for position checks.
    void handle(PlayerLocation to, PlayerLocation from);
}
