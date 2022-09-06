package dev.anhcraft.vhvl10.objects.lab;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.vhvl10.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class LinearMotionExp {
    @Setting
    @Validation(notNull = true, silent = true)
    private List<MotionPeriod> motionPeriods = new ArrayList<>();

    @Setting
    @Path("vehicle_weight")
    private double vehicleWeight = 1;

    @Setting
    @Path("vehicle_traction")
    private double vehicleTraction = 5;

    @Setting
    @Path("vehicle_origin_speed")
    private double vehicleOriginSpeed = 3;

    @NotNull
    public List<MotionPeriod> getMotionPeriods() {
        return motionPeriods;
    }

    public double getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(double vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }

    public double getVehicleTraction() {
        return vehicleTraction;
    }

    public void setVehicleTraction(double vehicleTraction) {
        this.vehicleTraction = vehicleTraction;
    }

    public double getVehicleOriginSpeed() {
        return vehicleOriginSpeed;
    }

    public void setVehicleOriginSpeed(double vehicleOriginSpeed) {
        this.vehicleOriginSpeed = vehicleOriginSpeed;
    }

    public double calculateAcceleration(MotionPeriod period) {
        // 4 luc: luc nang, luc ma sat, luc keo, luc hap dan
        // Y:  Py - N        = m*a
        //     m*g*sin() - N = 0
        //     N              = m*g*cos()
        double nForce = vehicleWeight * Constants.EARTH_GRAVITY * Math.cos(period.getAngle());
        // X:  -Fms + Fk + Px         = m*a
        //     -ms.N + Fk - m*g*sin() = m*a
        double gravityX = vehicleWeight * Constants.EARTH_GRAVITY * Math.sin(period.getAngle());
        double fx = -period.getFriction() * nForce + vehicleTraction - gravityX;
        /*
        System.out.println(String.format("(%s, %s,) -> (%s, %s) | a = %.2f deg", period.getOriginX(), period.getOriginY(), period.getTargetX(), period.getTargetY(), Math.toDegrees(period.getAngle())));
        System.out.println(String.format("- nForce: %s", nForce));
        System.out.println(String.format("- gravityX: %s", gravityX));
        System.out.println(String.format("- fx = -%s * nForce + %s + gravityX", period.getFriction(), vehicleTraction));
         */
        return fx / vehicleWeight;
    }
}
