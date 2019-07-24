/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

import java.util.Objects;

public class Point {
    private double x;
    private double y;
    private double z;
    private double w = 1;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setW(double w) {
        this.w = w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point that = (Point) o;
        return Double.compare(this.x, that.x) == 0 &&
                Double.compare(this.y, that.y) == 0 &&
                Double.compare(this.z, that.z) == 0 &&
                Double.compare(this.w, that.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                "}";
    }
}
