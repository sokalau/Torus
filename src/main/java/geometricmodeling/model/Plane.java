/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

import geometricmodeling.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Plane {
    private List<Line> lines = new ArrayList<>();
    private Point center;

    public Plane(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Line) {
                Line rib = (Line) object;
                lines.add(rib);
            }
        }

        calculateCenterPoint(lines);
    }

    private void calculateCenterPoint(List<Line> ribs) {
        List<Point> points = new ArrayList<>();

        for (Line rib : ribs) {
            points.addAll(rib.getPoints());
        }

        center = MathUtils.centroid(points);
    }

    public List<Line> getLines() {
        return lines;
    }

    public Point getCenter() {
        return center;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plane that = (Plane) o;
        return Objects.equals(this.lines, that.lines)
                && Objects.equals(this.center, that.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines, center);
    }

    @Override
    public String toString() {
        return "Plane{" +
                "lines=" + lines +
                ", center=" + center +
                "}";
    }
}
