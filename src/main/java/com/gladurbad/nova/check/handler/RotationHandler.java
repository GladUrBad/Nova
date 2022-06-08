package com.gladurbad.nova.check.handler;

import com.gladurbad.nova.util.location.PlayerLocation;

public interface RotationHandler {
    // Current and last rotation for rotation checks.
    void handle(PlayerLocation to, PlayerLocation from);
}
