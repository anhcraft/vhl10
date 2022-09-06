package dev.anhcraft.vhvl10.objects.lab;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;

@Configurable
public class MotionPeriod extends Vector {
    @Setting
    @Path("friction")
    private double friction;

    public MotionPeriod() {
    }

    public MotionPeriod(int originX, int originY, int targetX, int targetY, double friction) {
        super(originX, originY, targetX, targetY);
        this.friction = friction;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }
}
