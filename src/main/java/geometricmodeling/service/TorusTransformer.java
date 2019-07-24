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
import java.util.List;

public class TorusTransformer {
    private Torus torus;

    public TorusTransformer(Torus torus) {
        this.torus = torus;
    }

    public Torus rotate(double rotationX, double rotationY, double rotationZ) {
        List<Plane> newFacets = new ArrayList<>();
        for (Plane facet : torus.getFacets()) {

            List<Line> newRibs = new ArrayList<>();
            for (Line rib : facet.getLines()) {

                List<Point> newPoints = new ArrayList<>();
                for (Point point : rib.getPoints()) {
                    newPoints.add(rotatePoint(point, rotationX, rotationY, rotationZ));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point rotatePoint(Point point, double rX, double rY, double rZ) {
        double[][] vector = MathUtils.toVector(point);
        double[][] rotationMatrix = MathUtils.getRotationMatrix(rX, rY, rZ);
        double[][] newMatrix = MathUtils.multiply(vector, rotationMatrix);
        return new Point(newMatrix[0][0], newMatrix[0][1], newMatrix[0][2]);
    }

    public Torus scale(double sX, double sY, double sZ) {
        List<Plane> newFacets = new ArrayList<>();
        for (Plane facet : torus.getFacets()) {

            List<Line> newRibs = new ArrayList<>();
            for (Line rib : facet.getLines()) {

                List<Point> newPoints = new ArrayList<>();
                for (Point point : rib.getPoints()) {
                    newPoints.add(scalePoint(point, sX, sY, sZ));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point scalePoint(Point point, double sX, double sY, double sZ) {
        double[][] vector = MathUtils.toVector(point);
        double[][] scalingMatrix = MathUtils.getScalingMatrix(sX, sY, sZ);
        double[][] newMatrix = MathUtils.multiply(vector, scalingMatrix);
        return new Point(newMatrix[0][0], newMatrix[0][1], newMatrix[0][2]);
    }

    public Torus translate(double dX, double dY, double dZ) {
        List<Plane> newFacets = new ArrayList<>();

        for (Plane facet : torus.getFacets()) {
            List<Line> newRibs = new ArrayList<>();

            for (Line rib : facet.getLines()) {
                List<Point> newPoints = new ArrayList<>();

                for (Point point : rib.getPoints()) {
                    newPoints.add(translatePoint(point, dX, dY, dZ));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point translatePoint(Point point, double dX, double dY, double dZ) {
        double[][] vector = MathUtils.toVector(point);
        double[][] translationMatrix = MathUtils.getTranslationMatrix(dX, dY, dZ);
        double[][] newMatrix = MathUtils.multiply(vector, translationMatrix);
        return new Point(newMatrix[0][0], newMatrix[0][1], newMatrix[0][2]);
    }

    public Torus perspective(double d) {
        List<Plane> newFacets = new ArrayList<>();

        for (Plane facet : torus.getFacets()) {
            List<Line> newRibs = new ArrayList<>();

            for (Line rib : facet.getLines()) {
                List<Point> newPoints = new ArrayList<>();

                for (Point point : rib.getPoints()) {
                    newPoints.add(perspectivePoint(point, d));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point perspectivePoint(Point point, double d) {
        double z = Math.abs(point.getZ()) <= 0.1 ? 0.1 : Math.abs(point.getZ());
        double x = point.getX() * d / z;
        double y = point.getY() * d / z;
        z = d;
        return new Point(x, y, z);
    }

    public Torus axonometric(double rX, double rY, double rZ) {
        List<Plane> newFacets = new ArrayList<>();

        for (Plane facet : torus.getFacets()) {
            List<Line> newRibs = new ArrayList<>();

            for (Line rib : facet.getLines()) {
                List<Point> newPoints = new ArrayList<>();

                for (Point point : rib.getPoints()) {
                    newPoints.add(axonometricPoint(point, rX, rY, rZ));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point axonometricPoint(Point point, double rX, double rY, double rZ) {
        return rotatePoint(point, rX, rY, rZ);
    }

    public Torus oblique(double l, double alpha) {
        List<Plane> newFacets = new ArrayList<>();
        for (Plane facet : torus.getFacets()) {

            List<Line> newRibs = new ArrayList<>();
            for (Line rib : facet.getLines()) {

                List<Point> newPoints = new ArrayList<>();
                for (Point point : rib.getPoints()) {
                    newPoints.add(obliquePoint(point, l, alpha));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point obliquePoint(Point point, double l, double alpha) {
        double[][] vector = MathUtils.toVector(point);
        double[][] rotationMatrix = MathUtils.getObliqueMatrix(l, alpha);

        double[][] newMatrix = MathUtils.multiply(vector, rotationMatrix);
        return new Point(newMatrix[0][0], newMatrix[0][1], newMatrix[0][2]);
    }

    public Torus viewTransform(double rho, double phi, double theta) {
        List<Plane> newFacets = new ArrayList<>();
        for (Plane facet : torus.getFacets()) {

            List<Line> newRibs = new ArrayList<>();
            for (Line rib : facet.getLines()) {

                List<Point> newPoints = new ArrayList<>();
                for (Point point : rib.getPoints()) {
                    newPoints.add(viewTransformPoint(point, rho, phi, theta));
                }

                newRibs.add(new Line(newPoints.get(0), newPoints.get(newPoints.size() - 1)));
            }

            newFacets.add(new Plane(newRibs.toArray()));
        }

        return new Torus.Builder()
                .minorRadius(torus.getMinorRadius())
                .minorAngle(torus.getMinorAngle())
                .majorRadius(torus.getMajorRadius())
                .majorAngle(torus.getMajorAngle())
                .facets(newFacets)
                .build();
    }

    private Point viewTransformPoint(Point point, double rho, double phi, double theta) {
        double[][] vector = MathUtils.toVector(point);
        double[][] viewTransformationMatrix = MathUtils.getViewTransformationMatrix(rho, phi, theta);
        double[][] newMatrix = MathUtils.multiply(vector, viewTransformationMatrix);
        return new Point(newMatrix[0][0], newMatrix[0][1], newMatrix[0][2]);
    }
}
