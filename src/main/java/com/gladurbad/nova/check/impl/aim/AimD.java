package com.gladurbad.nova.check.impl.aim;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.RotationContextHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.mouse.MouseSnapshot;

public class AimD extends Check implements RotationContextHandler {

    public AimD(PlayerData data) {
        super(data, "Aim (D)");
    }

    @Override
    public void handle(MouseSnapshot mouseSnapshot) {

    }
}
