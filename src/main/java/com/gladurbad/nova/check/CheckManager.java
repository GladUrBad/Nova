package com.gladurbad.nova.check;

import com.gladurbad.nova.check.handler.*;
import com.gladurbad.nova.check.impl.aim.AimA;
import com.gladurbad.nova.check.impl.aim.AimB;
import com.gladurbad.nova.check.impl.aim.AimC;
import com.gladurbad.nova.check.impl.aim.AimD;
import com.gladurbad.nova.check.impl.autoclicker.AutoClickerA;
import com.gladurbad.nova.check.impl.autoclicker.AutoClickerB;
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
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CheckManager {

    private final Set<PacketHandler> packetChecks;
    private final Set<PositionHandler> positionChecks;
    private final Set<RotationHandler> rotationChecks;
    private final Set<RotationContextHandler> rotationContextChecks;
    private final Set<SwingHandler> swingChecks;

    public CheckManager(PlayerData data) {
        // Register checks here.
        ClassToInstanceMap<Check> checkMap = new ImmutableClassToInstanceMap.Builder<Check>()
                .put(AimA.class, new AimA(data))
                .put(AimB.class, new AimB(data))
                .put(AimC.class, new AimC(data))
                .put(AimD.class, new AimD(data))
                .put(AutoClickerA.class, new AutoClickerA(data))
                .put(AutoClickerB.class, new AutoClickerB(data))
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

        this.packetChecks = new HashSet<>();
        this.positionChecks = new HashSet<>();
        this.rotationChecks = new HashSet<>();
        this.rotationContextChecks = new HashSet<>();
        this.swingChecks = new HashSet<>();

        for (Check check : checkMap.values()) {
            // Checks can have multiple handlers.
            if (check instanceof PacketHandler) packetChecks.add((PacketHandler) check);
            if (check instanceof PositionHandler) positionChecks.add((PositionHandler) check);
            if (check instanceof RotationHandler) rotationChecks.add((RotationHandler) check);
            if (check instanceof RotationContextHandler) rotationContextChecks.add((RotationContextHandler) check);
            if (check instanceof SwingHandler) swingChecks.add((SwingHandler) check);
        }
    }
}
