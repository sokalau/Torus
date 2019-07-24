/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.service;

import geometricmodeling.model.Line;
import geometricmodeling.model.Plane;
import geometricmodeling.model.Point;
import geometricmodeling.model.Torus;
import geometricmodeling.util.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TorusBuilder {
    private double minorRadius;
    private double majorRadius;
    private double minorAngle;
    private double majorAngle;

    private int minorRadiusApproximationPower;
    private int majorRadiusApproximationPower;

    public TorusBuilder(double minorRadius, double minorAngle, double majorRadius, double majorAngle) {
        this.minorRadius = minorRadius;
        this.minorAngle = minorAngle;
        this.majorRadius = majorRadius;
        this.majorAngle = majorAngle;
        this.minorRadiusApproximationPower = 360 / (int) minorAngle;
        this.majorRadiusApproximationPower = 360 / (int) majorAngle;
    }

    public Torus build() throws Exception {
        List<Plane> facets;

        try {
            List<Point> points = buildPoints();
            List<Line> ribs = buildRibs(points);
            facets = buildFacets(ribs);
        } catch (Exception e) {
            throw new Exception(e);
        }

        return new Torus.Builder()
                .minorRadius(this.minorRadius)
                .minorAngle(this.minorAngle)
                .majorRadius(this.majorRadius)
                .majorAngle(this.majorAngle)
                .facets(facets)
                .build();
    }

    private List<Point> buildPoints() {
        List<Point> points = new ArrayList<>();

        double minorAngleInRadians = Math.toRadians(minorAngle);
        double majorAngleInRadians = Math.toRadians(majorAngle);

        double R = majorRadius;
        double r = minorRadius;

        for (int major = 0; major < majorRadiusApproximationPower; major++) {
            for (int minor = 0; minor < minorRadiusApproximationPower; minor++) {
                double x = (R + r * Math.cos(minor * minorAngleInRadians)) * Math.cos(major * majorAngleInRadians);
                double z = (R + r * Math.cos(minor * minorAngleInRadians)) * Math.sin(major * majorAngleInRadians);
                double y = r * Math.sin(minor * minorAngleInRadians);
                points.add(new Point(x, y, z));
            }
        }

        return points;
    }

    private List<Line> buildRibs(List<Point> points) {
        List<Line> ribs = new ArrayList<>();
        List<Line> longitudinalRibs = new ArrayList<>();
        List<Line> crossedRibs = new ArrayList<>();
        int multipliedApproximationPower = minorRadiusApproximationPower * majorRadiusApproximationPower;

        for (int i = 0; i < multipliedApproximationPower; i++) {
            int firstInCurrentSection = (i / minorRadiusApproximationPower) * minorRadiusApproximationPower;
            int minor = i % minorRadiusApproximationPower;
            Point longitudinalStartPoint = points.get(firstInCurrentSection + minor);
            Point longitudinalEndPoint = points.get(firstInCurrentSection + (minor + 1) % minorRadiusApproximationPower);
            longitudinalRibs.add(new Line(longitudinalStartPoint, longitudinalEndPoint));
        }

        for (int i = 0; i < multipliedApproximationPower; i++) {
            int firstInCurrentSection = (i / minorRadiusApproximationPower) * minorRadiusApproximationPower;
            int minor = i % minorRadiusApproximationPower;
            Point crossedStartPoint = points.get(firstInCurrentSection + minor);
            Point crossedEndPoint = points.get((firstInCurrentSection + minor + minorRadiusApproximationPower)
                    % (multipliedApproximationPower));
            crossedRibs.add(new Line(crossedStartPoint, crossedEndPoint));
        }

        ribs.addAll(longitudinalRibs);
        ribs.addAll(crossedRibs);

        return ribs;
    }

    private List<Plane> buildFacets(List<Line> ribs) {
        List<Plane> facets = new ArrayList<>();
        int multipliedApproximationPower = majorRadiusApproximationPower * minorRadiusApproximationPower;

        for (int i = 0; i < multipliedApproximationPower; i++) {
            int minor = i % minorRadiusApproximationPower;
            List<Line> currentPlane = Arrays.asList(
                    ribs.get(i),
                    ribs.get((i / minorRadiusApproximationPower) * minorRadiusApproximationPower + (minor + 1)
                            % minorRadiusApproximationPower + multipliedApproximationPower),
                    MathUtils.rotate(ribs.get((i + minorRadiusApproximationPower) % (multipliedApproximationPower))),
                    MathUtils.rotate(ribs.get(i + multipliedApproximationPower))
            );
            facets.add(new Plane(currentPlane.toArray()));
        }

        return facets;
    }
}
