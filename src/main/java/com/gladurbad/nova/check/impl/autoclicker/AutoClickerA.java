package com.gladurbad.nova.check.impl.autoclicker;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.SwingHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.swing.ClickAnalysis;

public class AutoClickerA extends Check implements SwingHandler {

    public AutoClickerA(PlayerData data) {
        super(data, "AutoClicker (A)");
    }

    private final Buffer buffer = new Buffer(10);

    @Override
    public void handle(ClickAnalysis analysis) {
        // Not much you can do with auto-clicker checks, really.
        if (analysis.getDeviation() < 0.6 && analysis.getCps() > 11.5) {
            if (buffer.add() > 6) fail();
        } else {
            buffer.reduce(0.5);
        }
    }
}
