/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.service;

import geometricmodeling.common.Constants;
import geometricmodeling.model.*;
import geometricmodeling.util.MathUtils;
import geometricmodeling.util.TorusUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
    private static final String FRONT = "Front";
    private static final String TOP = "Top";
    private static final String SIDE = "Side";

    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public Drawer(Canvas canvas) {
        this.canvas = canvas;
        graphicsContext = this.canvas.getGraphicsContext2D();
    }

    public void drawView(List<Plane> facets, Projection projection, boolean isLightSelected,
                         Color color, Point lightPosition) {
        Point center = getCanvasCenter();
        drawCoordinateAxes(projection);

        switch (projection) {
            case OBLIQUE:
                drawProjection(facets, center, PlaneType.XOY, Constants.DEFAULT_SCALE, isLightSelected, color,
                        lightPosition);
                break;
            case ORTHOGONAL:
                Point canvasQuarter = getCanvasQuarter();

                double centerX = center.getX();
                double centerY = center.getY();
                double canvasQuarterX = canvasQuarter.getX();
                double canvasQuarterY = canvasQuarter.getY();

                Point frontCenter = new Point(centerX - canvasQuarterX, centerY - canvasQuarterY, 0);
                Point sideCenter = new Point(centerX + canvasQuarterX, centerY - canvasQuarterY, 0);
                Point topCenter = new Point(centerX - canvasQuarterX, centerY + canvasQuarterY, 0);

                double scaleCoefficient = Constants.DEFAULT_SCALE / 2;

                TorusUtils.sortFacets(facets, SortOrder.Z_ASCENDING);
                drawProjection(facets, frontCenter, PlaneType.XOY, scaleCoefficient, isLightSelected, color, lightPosition);

                TorusUtils.sortFacets(facets, SortOrder.X_ASCENDING);
                drawProjection(facets, sideCenter, PlaneType.ZOY, scaleCoefficient, isLightSelected, color, lightPosition);

                TorusUtils.sortFacets(facets, SortOrder.Y_ASCENDING);
                drawProjection(facets, topCenter, PlaneType.XOZ, scaleCoefficient, isLightSelected, color, lightPosition);
                break;
            case AXONOMETRIC:
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

                double canvasCenterX = canvasCenter.getX();
                double canvasCenterY = canvasCenter.getY();
                double canvasQuarterX = canvasQuarter.getX();
                double canvasQuarterY = canvasQuarter.getY();

                Point front = new Point(canvasCenterX - canvasQuarterX, canvasCenterY - canvasQuarterY, 0);
                Point side = new Point(canvasCenterX + canvasQuarterX, canvasCenterY - canvasQuarterY, 0);
                Point top = new Point(canvasCenterX - canvasQuarterX, canvasCenterY + canvasQuarterY, 0);
                double orthogonalTextOffset = 50;

                double frontX = front.getX() - orthogonalTextOffset * 3;
                double frontY = front.getY() - orthogonalTextOffset * 2;
                graphicsContext.strokeText(FRONT, frontX, frontY);

                double topX = top.getX() - orthogonalTextOffset * 3;
                double topY = top.getY() - orthogonalTextOffset * 3;
                graphicsContext.strokeText(TOP, topX, topY);

                double sideX = side.getX() - orthogonalTextOffset * 3;
                double sideY = side.getY() - orthogonalTextOffset * 2;
                graphicsContext.strokeText(SIDE, sideX, sideY);
                break;
            case OBLIQUE:
            case PERSPECTIVE:
            case AXONOMETRIC:
                double textOffset = 10;
                double increment = 50;
                double offset = 100;
                Point start = new Point(offset / 2, offset, offset);
                double startX = start.getX();
                double startY = start.getY();

                graphicsContext.strokeText(Y, startX, startY - increment - textOffset);
                graphicsContext.strokeLine(startX, startY, startX, startY - increment);

                graphicsContext.strokeText(X, startX + increment + textOffset, startY);
                graphicsContext.strokeLine(startX, startY, startX + increment, startY);

                graphicsContext.strokeText(Z, startX + increment + textOffset, startY - increment - textOffset);
                graphicsContext.strokeLine(startX, startY, startX + increment, startY - increment);
                break;
            default:
                throw new IllegalArgumentException("There is no such projection.");
        }
    }

    private void drawProjection(List<Plane> facets, Point center, PlaneType planeType,
                                double scaleCoefficient, boolean isLightSelected, Color color, Point lightPosition) {
        for (Plane facet : facets) {
            drawFacet(facet, center, planeType, scaleCoefficient, isLightSelected, color, lightPosition);
        }
    }

    private void drawFacet(Plane facet, Point center, PlaneType planeType,
                           double scale, boolean isLightSelected, Color color, Point lightPosition) {
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
                xPoints = MathUtils.calculateXPointsByCenterAndScale(
                        facetPoints.toArray(new Point[size]), center, scale);
                yPoints = MathUtils.calculateYPointsByCenterAndScale(
                        facetPoints.toArray(new Point[size]), center, -scale);
                break;
            case XOZ:
                xPoints = MathUtils.calculateXPointsByCenterAndScale(
                        facetPoints.toArray(new Point[size]), center, scale);
                yPoints = MathUtils.calculateZPointsByCenterAndScale(
                        facetPoints.toArray(new Point[size]), center, -scale, ProjectionView.TOP);
                break;
            case ZOY:
                xPoints = MathUtils.calculateZPointsByCenterAndScale(
                        facetPoints.toArray(new Point[size]), center, scale, ProjectionView.SIDE);
                yPoints = MathUtils.calculateYPointsByCenterAndScale(
                        facetPoints.toArray(new Point[size]), center, -scale);
                break;
            default:
                throw new IllegalArgumentException("There is no such plane type.");
        }

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokePolygon(xPoints, yPoints, size);

        if (isLightSelected) {
            double lightPositionX = lightPosition.getX();
            double lightPositionY = lightPosition.getY();
            double lightPositionZ = lightPosition.getZ();

            double lightLength = Math.sqrt(
                    Math.pow((lightPositionX - center.getX()), 2)
                            + Math.pow((lightPositionY - center.getY()), 2)
                            + Math.pow((lightPositionZ - center.getZ()), 2));

            double length = Math
                    .sqrt(Math.pow((lightPositionX - facet.getCenter().getX()), 2)
                            + Math.pow((lightPositionY - facet.getCenter().getY()), 2)
                            + Math.pow((lightPositionZ - facet.getCenter().getZ()), 2));

            double percent = 1 - length / (lightLength <= 0 ? 1 : lightLength);
            percent = Math.abs(percent) > 1 ? 0.2 : Math.abs(percent) * 1.5;
            percent = percent > 1 ? 1 : percent;

            Color newColor = Color.rgb((int) (color.getRed() * 255 * percent),
                    (int) (color.getGreen() * 255 * percent),
                    (int) (color.getBlue() * 255 * percent));

            if (lightLength < length) {
                newColor = Color.rgb((int) (color.getRed() * 255 * 0.2),
                        (int) (color.getGreen() * 255 * 0.2),
                        (int) (color.getBlue() * 255 * 0.2));
            }

            graphicsContext.setFill(newColor);
            graphicsContext.strokePolygon(xPoints, yPoints, size);
            graphicsContext.fillPolygon(xPoints, yPoints, size);
        } else {
            graphicsContext.setFill(color);
            graphicsContext.strokePolygon(xPoints, yPoints, size);
            graphicsContext.fillPolygon(xPoints, yPoints, size);
        }
    }

    public void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
