package dev.anhcraft.vhvl10.objects.lab;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import org.jetbrains.annotations.NotNull;

@Configurable
public class ProjectileMotionExp {
    @Setting
    @Path("direction")
    @Validation(notNull = true, silent = true)
    private Vector direction = new Vector(0, 24, 30, 45);

    @Setting
    @Path("init_speed")
    private double initialSpeed = 45;

    @NotNull
    public Vector getDirection() {
        return direction;
    }

    public void setDirection(@NotNull Vector direction) {
        this.direction = direction;
    }

    public double getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(double initialSpeed) {
        this.initialSpeed = initialSpeed;
    }
}
