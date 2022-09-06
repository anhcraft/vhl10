package dev.anhcraft.vhvl10.objects.lab;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;

import java.util.Objects;

@Configurable
public class Vector {
    @Setting
    @Path("origin_x")
    private int originX;

    @Setting
    @Path("origin_y")
    private int originY;

    @Setting
    @Path("target_x")
    private int targetX;

    @Setting
    @Path("target_y")
    private int targetY;

    public Vector() {
    }

    public Vector(int originX, int originY, int targetX, int targetY) {
        this.originX = originX;
        this.originY = originY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public int getOriginX() {
        return originX;
    }

    public void setOriginX(int originX) {
        this.originX = originX;
    }

    public int getOriginY() {
        return originY;
    }

    public void setOriginY(int originY) {
        this.originY = originY;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    public double getLength() {
        int x = targetX - originX;
        int y = targetY - originY;
        return Math.sqrt(x * x + y * y);
    }

    public double getAngle() {
        int x = targetX - originX;
        int y = targetY - originY;
        return Math.atan2(-y, x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return originX == vector.originX &&
                originY == vector.originY &&
                targetX == vector.targetX &&
                targetY == vector.targetY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originX, originY, targetX, targetY);
    }
}
