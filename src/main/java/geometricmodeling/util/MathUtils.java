/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.util;

import geometricmodeling.model.Line;
import geometricmodeling.model.Point;
import geometricmodeling.model.ProjectionView;

import java.util.List;

public final class MathUtils {
    private MathUtils() {
    }

    public static Point centroid(List<Point> points) {
        double centroidX = 0;
        double centroidY = 0;
        double centroidZ = 0;

        for (Point point : points) {
            centroidX += point.getX();
            centroidY += point.getY();
            centroidZ += point.getZ();
        }

        int size = points.size();
        double x = centroidX / size;
        double y = centroidY / size;
        double z = centroidZ / size;

        return new Point(x, y, z);
    }

    public static Line rotate(Line line) {
        return new Line(line.getEndPoint(), line.getStartPoint());
    }

    public static double[][] toVector(Point point) {
        return new double[][]{
                {point.getX(), point.getY(), point.getZ(), point.getW()}
        };
    }

    public static double[] calculateXPointsByCenterAndScale(Point[] points, Point center, double scale) {
        double[] xPoints = new double[points.length];

        for (int i = 0; i < points.length; i++) {
            xPoints[i] = center.getX() + points[i].getX() * scale;
        }

        return xPoints;
    }

    public static double[] calculateYPointsByCenterAndScale(Point[] points, Point center, double scale) {
        double[] yPoints = new double[points.length];

        for (int i = 0; i < points.length; i++) {
            yPoints[i] = center.getY() + points[i].getY() * scale;
        }

        return yPoints;
    }

    public static double[] calculateZPointsByCenterAndScale(Point[] points, Point center, double scale,
                                                            ProjectionView projectionView) {
        double[] zPoints = new double[points.length];
        double zCenter;

        switch (projectionView) {
            case TOP:
                zCenter = center.getY();
                break;
            case SIDE:
                zCenter = center.getX();
                break;
            default:
                throw new IllegalArgumentException("There is no such projection view.");
        }

        for (int i = 0; i < points.length; i++) {
            zPoints[i] = zCenter + points[i].getZ() * scale;
        }

        return zPoints;
    }

    public static double[][] multiply(double[][] matrixA, double[][] matrixB) {
        int matrixARows = matrixA.length;
        int matrixAColumns = matrixA[0].length;
        int matrixBRows = matrixB.length;
        int matrixBColumns = matrixB[0].length;

        if (matrixAColumns != matrixBRows) {
            throw new IllegalArgumentException("Columns in matrix a: " + matrixAColumns
                    + " didn't match rows in matrix b: " + matrixBRows);
        }

        double[][] matrixC = new double[matrixARows][matrixBColumns];

        for (int i = 0; i < matrixARows; i++) {
            for (int j = 0; j < matrixBColumns; j++) {
                for (int k = 0; k < matrixAColumns; k++) {
                    matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return matrixC;
    }

    public static double[][] getRotationMatrix(double rX, double rY, double rZ) {
        double[][] xRotationMatrix = getXRotationMatrix(rX);
        double[][] yRotationMatrix = getYRotationMatrix(rY);
        double[][] zRotationMatrix = getZRotationMatrix(rZ);

        return multiply(multiply(xRotationMatrix, yRotationMatrix), zRotationMatrix);
    }

    private static double[][] getXRotationMatrix(double rX) {
        return new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, Math.cos(Math.toRadians(rX)), Math.sin(Math.toRadians(rX)), 0.0},
                {0.0, -Math.sin(Math.toRadians(rX)), Math.cos(Math.toRadians(rX)), 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    private static double[][] getYRotationMatrix(double rY) {
        return new double[][]{
                {Math.cos(Math.toRadians(rY)), 0.0, -Math.sin(Math.toRadians(rY)), 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {Math.sin(Math.toRadians(rY)), 0.0, Math.cos(Math.toRadians(rY)), 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    private static double[][] getZRotationMatrix(double rZ) {
        return new double[][]{
                {Math.cos(Math.toRadians(rZ)), Math.sin(Math.toRadians(rZ)), 0.0, 0.0},
                {-Math.sin(Math.toRadians(rZ)), Math.cos(Math.toRadians(rZ)), 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    public static double[][] getScalingMatrix(double sX, double sY, double sZ) {
        return new double[][]{
                {sX, 0.0, 0.0, 0.0},
                {0.0, sY, 0.0, 0.0},
                {0.0, 0.0, sZ, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    public static double[][] getTranslationMatrix(double dX, double dY, double dZ) {
        return new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {dX, dY, dZ, 1.0}
        };
    }

    public static double[][] getPerspectiveMatrix(double d) {
        if (Math.abs(d) < 0.1) d = 0.1;

        return new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 1.0 / d},
                {0.0, 0.0, 0.0, 0.0}
        };
    }

    private static double[][] getIdentityMatrix() {
        return new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    public static double[][] getObliqueMatrix(double l, double alpha) {
        return new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {l * Math.cos(Math.toRadians(alpha)), l * Math.sin(Math.toRadians(alpha)), 0.0, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    public static double[][] getViewTransformationMatrix(double rho, double phi, double theta) {
        return new double[][]{
                {-Math.sin(theta), -Math.cos(phi) * Math.cos(theta), -Math.sin(phi) * Math.cos(theta), 0.0},
                {Math.cos(theta), -Math.cos(phi) * Math.sin(phi), -Math.sin(phi) * Math.sin(theta), 0.0},
                {0.0, Math.sin(phi), -Math.cos(phi), 0.0},
                {0.0, 0.0, rho, 1.0}
        };
    }
}
