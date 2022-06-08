package com.gladurbad.nova.check;

import com.gladurbad.nova.check.impl.aim.AimA;
import com.gladurbad.nova.check.impl.aim.AimB;
import com.gladurbad.nova.check.impl.aim.AimC;
import com.gladurbad.nova.check.impl.autoclicker.AutoClickerA;
import com.gladurbad.nova.check.impl.fly.FlyA;
import com.gladurbad.nova.check.impl.fly.FlyB;
import com.gladurbad.nova.check.impl.hitbox.HitboxA;
import com.gladurbad.nova.check.impl.invalid.InvalidA;
import com.gladurbad.nova.check.impl.invalid.InvalidB;
import com.gladurbad.nova.check.impl.invalid.InvalidC;
import com.gladurbad.nova.check.impl.killaura.KillAuraA;
import com.gladurbad.nova.check.impl.reach.ReachA;
import com.gladurbad.nova.check.impl.speed.SpeedA;
import com.gladurbad.nova.check.impl.timer.TimerA;
import com.gladurbad.nova.check.impl.timer.TimerB;
import com.gladurbad.nova.check.impl.velocity.VelocityA;
import com.gladurbad.nova.check.impl.velocity.VelocityB;
import com.gladurbad.nova.data.PlayerData;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.Collection;

public class CheckManager {

    private final ClassToInstanceMap<Check> checkMap;

    public CheckManager(PlayerData data) {
        // Register checks here.
        this.checkMap = new ImmutableClassToInstanceMap.Builder<Check>()
                .put(AimA.class, new AimA(data))
                .put(AimB.class, new AimB(data))
                .put(AimC.class, new AimC(data))
                .put(AutoClickerA.class, new AutoClickerA(data))
                .put(FlyA.class, new FlyA(data))
                .put(FlyB.class, new FlyB(data))
                .put(HitboxA.class, new HitboxA(data))
                .put(InvalidA.class, new InvalidA(data))
                .put(InvalidB.class, new InvalidB(data))
                .put(InvalidC.class, new InvalidC(data))
                .put(KillAuraA.class, new KillAuraA(data))
                .put(ReachA.class, new ReachA(data))
                .put(SpeedA.class, new SpeedA(data))
                .put(TimerA.class, new TimerA(data))
                .put(TimerB.class, new TimerB(data))
                .put(VelocityA.class, new VelocityA(data))
                .put(VelocityB.class, new VelocityB(data))
                .build();
    }

    public <T extends Check> T getCheck(Class<T> klass) {
        return checkMap.getInstance(klass);
    }

    public Collection<Check> getChecks() {
        return checkMap.values();
    }
}
