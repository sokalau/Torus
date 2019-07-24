/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private Point startPoint;
    private Point endPoint;
    private List<Point> points = new ArrayList<>();

    public Line(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        points.add(startPoint);
        points.add(endPoint);
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line that = (Line) o;
        return Objects.equals(this.startPoint, that.startPoint) &&
                Objects.equals(this.endPoint, that.endPoint) &&
                Objects.equals(this.points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPoint, endPoint, points);
    }

    @Override
    public String toString() {
        return "Line{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", points=" + points +
                "}";
    }
}
