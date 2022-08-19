package com.gladurbad.nova.check.impl.autoclicker;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.SwingHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.swing.ClickAnalysis;

public class AutoClickerB extends Check implements SwingHandler {

    public AutoClickerB(PlayerData data) {
        super(data, "AutoClicker (B)");
    }

    private int streak;

    @Override
    public void handle(ClickAnalysis analysis) {
        // Basic CPS autoclicker check. Lots of people can click over 20 but not too consistently.
        if (analysis.getCps() > 20.5) {
            if (++streak > 3) fail();
        } else {
            streak = 0;
        }
    }
}
