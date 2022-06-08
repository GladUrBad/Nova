package com.gladurbad.nova.check.impl.invalid;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.RotationHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.location.PlayerLocation;

public class InvalidB extends Check implements RotationHandler {

    public InvalidB(PlayerData data) {
        super(data, "Invalid (B)");
    }

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Pitch cannot go over 90 degrees.
        if (Math.abs(to.getPitch()) > 90F) fail();
    }
}
