/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.service;

import geometricmodeling.common.Constants;
import geometricmodeling.model.*;
import geometricmodeling.util.MathUtils;
import geometricmodeling.util.TorusUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
    private Canvas canvas;

    public Drawer(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawView(List<Plane> facets, Projection projection, boolean isLightSelected, Color color, Point lightPosition) {
        Point center = getCanvasCenter();
        drawCoordinateAxes(projection);

        switch (projection) {
            case OBLIQUE:
                drawProjection(facets, center, PlaneType.XOY, Constants.DEFAULT_SCALE, isLightSelected, color, lightPosition);
                break;
            case ORTHOGONAL:
                Point quarter = getCanvasQuarter();
                Point frontCenter = new Point(center.getX() - quarter.getX(), center.getY() - quarter.getY(), 0);
                Point sideCenter = new Point(center.getX() + quarter.getX(), center.getY() - quarter.getY(), 0);
                Point topCenter = new Point(center.getX() - quarter.getX(), center.getY() + quarter.getY(), 0);

                TorusUtils.sortFacets(facets, SortOrder.Z_ASCENDING);
                drawProjection(facets, frontCenter, PlaneType.XOY, Constants.DEFAULT_SCALE / 2, isLightSelected, color, lightPosition);

                TorusUtils.sortFacets(facets, SortOrder.X_ASCENDING);
                drawProjection(facets, sideCenter, PlaneType.ZOY, Constants.DEFAULT_SCALE / 2, isLightSelected, color, lightPosition);

                TorusUtils.sortFacets(facets, SortOrder.Y_ASCENDING);
                drawProjection(facets, topCenter, PlaneType.XOZ, Constants.DEFAULT_SCALE / 2, isLightSelected, color, lightPosition);
                break;
            case AXONOMETRIC:
                TorusUtils.sortFacets(facets, SortOrder.Z_ASCENDING);
                drawProjection(facets, center, PlaneType.XOY, Constants.DEFAULT_SCALE, isLightSelected, color, lightPosition);
                break;
            case PERSPECTIVE:
                TorusUtils.sortFacets(facets, SortOrder.Z_ASCENDING);
                drawProjection(facets, center, PlaneType.XOY, Constants.DEFAULT_SCALE, isLightSelected, color, lightPosition);
                break;
            default:
                throw new IllegalArgumentException("There is no such projection.");
        }
    }

    private void drawCoordinateAxes(Projection projection) {
        switch (projection) {
            case ORTHOGONAL:
                Point canvasCenter = getCanvasCenter();
                Point canvasQuarter = getCanvasQuarter();

                Point front = new Point(canvasCenter.getX() - canvasQuarter.getX(), canvasCenter.getY() - canvasQuarter.getY(), 0);
                Point side = new Point(canvasCenter.getX() + canvasQuarter.getX(), canvasCenter.getY() - canvasQuarter.getY(), 0);
                Point top = new Point(canvasCenter.getX() - canvasQuarter.getX(), canvasCenter.getY() + canvasQuarter.getY(), 0);
                double orthogonalTextOffset = 50;

                canvas.getGraphicsContext2D()
                        .strokeText(Constants.FRONT,
                                front.getX() - orthogonalTextOffset * 3,
                                front.getY() - orthogonalTextOffset * 2);
                canvas.getGraphicsContext2D()
                        .strokeText(Constants.TOP,
                                top.getX() - orthogonalTextOffset * 3,
                                top.getY() - orthogonalTextOffset * 3);
                canvas.getGraphicsContext2D()
                        .strokeText(Constants.SIDE,
                                side.getX() - orthogonalTextOffset * 3,
                                side.getY() - orthogonalTextOffset * 2);
                break;
            case OBLIQUE:
            case PERSPECTIVE:
            case AXONOMETRIC:
                double textOffset = 10;
                double increment = 50;
                double offset = 100;
                Point start = new Point(offset / 2, offset, offset);

                canvas.getGraphicsContext2D().strokeText(Constants.Y, start.getX(), start.getY() - increment - textOffset);
                canvas.getGraphicsContext2D().strokeLine(start.getX(), start.getY(), start.getX(), start.getY() - increment);

                canvas.getGraphicsContext2D()
                        .strokeText(Constants.X, start.getX() + increment + textOffset, start.getY());
                canvas.getGraphicsContext2D()
                        .strokeLine(start.getX(), start.getY(), start.getX() + increment, start.getY());

                canvas.getGraphicsContext2D().strokeText(Constants.Z,
                        start.getX() + increment + textOffset,
                        start.getY() - increment - textOffset);

                canvas.getGraphicsContext2D().strokeLine(start.getX(), start.getY(),
                        start.getX() + increment,
                        start.getY() - increment);
                break;
            default:
                throw new IllegalArgumentException("There is no such projection.");
        }
    }

    private void drawProjection(List<Plane> facets, Point center, PlaneType planeType,
                                double scaleCoefficient, boolean withLight, Color color, Point lightPosition) {
        for (Plane facet : facets) {
            drawFacet(facet, center, planeType, scaleCoefficient, withLight, color, lightPosition);
        }
    }

    private void drawFacet(Plane facet, Point center, PlaneType planeType,
                           double scale, boolean withLight, Color color, Point lightPosition) {
        List<Line> ribs = facet.getLines();
        List<Point> facetPoints = new ArrayList<>();

        for (Line rib : ribs) {
            facetPoints.addAll(rib.getPoints());
        }

        int size = facetPoints.size();

        double[] xPoints;
        double[] yPoints;

        switch (planeType) {
            case XOY:
                xPoints = MathUtils.getXValues(facetPoints.toArray(new Point[size]), center, scale);
                yPoints = MathUtils.getYValues(facetPoints.toArray(new Point[size]), center, -scale);
                break;
            case XOZ:
                xPoints = MathUtils.getXValues(facetPoints.toArray(new Point[size]), center, scale);
                yPoints = MathUtils.getZValues(facetPoints.toArray(new Point[size]), center, -scale, ProjectionView.TOP);
                break;
            case ZOY:
                xPoints = MathUtils.getZValues(facetPoints.toArray(new Point[size]), center, scale, ProjectionView.SIDE);
                yPoints = MathUtils.getYValues(facetPoints.toArray(new Point[size]), center, -scale);
                break;
            default:
                throw new IllegalArgumentException("There is no such plane type.");
        }

        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokePolygon(xPoints, yPoints, size);

        if (withLight) {
            double lightLength = Math
                    .sqrt(Math.pow((lightPosition.getX() - center.getX()), 2)
                            + Math.pow((lightPosition.getY() - center.getY()), 2)
                            + Math.pow((lightPosition.getZ() - center.getZ()), 2));

            double length = Math
                    .sqrt(Math.pow((lightPosition.getX() - facet.getCenter().getX()), 2)
                            + Math.pow((lightPosition.getY() - facet.getCenter().getY()), 2)
                            + Math.pow((lightPosition.getZ() - facet.getCenter().getZ()), 2));

            double persent = 1 - length / (lightLength <= 0 ? 1 : lightLength);
            persent = Math.abs(persent) > 1 ? 0.2 : Math.abs(persent) * 1.5;
            persent = persent > 1 ? 1 : persent;

            Color newColor = Color.rgb((int) (color.getRed() * 255 * persent),
                    (int) (color.getGreen() * 255 * persent),
                    (int) (color.getBlue() * 255 * persent));

            if (lightLength < length) {
                newColor = Color.rgb((int) (color.getRed() * 255 * 0.2),
                        (int) (color.getGreen() * 255 * 0.2),
                        (int) (color.getBlue() * 255 * 0.2));
            }

            canvas.getGraphicsContext2D().setFill(newColor);
            canvas.getGraphicsContext2D().strokePolygon(xPoints, yPoints, size);
            canvas.getGraphicsContext2D().fillPolygon(xPoints, yPoints, size);
        } else {
            canvas.getGraphicsContext2D().setFill(color);
            canvas.getGraphicsContext2D().strokePolygon(xPoints, yPoints, size);
            canvas.getGraphicsContext2D().fillPolygon(xPoints, yPoints, size);
        }
    }

    public void clear() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private Point getCanvasCenter() {
        double x = canvas.getWidth() / 2;
        double y = canvas.getHeight() / 2;
        return new Point(x, y, 0);
    }

    private Point getCanvasQuarter() {
        double x = canvas.getWidth() / 4;
        double y = canvas.getHeight() / 4;
        return new Point(x, y, 0);
    }
}
