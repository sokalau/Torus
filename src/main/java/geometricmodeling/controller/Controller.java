/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.controller;

import geometricmodeling.common.Constants;
import geometricmodeling.model.*;
import geometricmodeling.service.Drawer;
import geometricmodeling.service.TorusBuilder;
import geometricmodeling.service.TorusTransformer;
import geometricmodeling.util.TorusUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    private static final Logger logger = LogManager.getLogger();
    private static final String EMPTY = "";

    @FXML
    private TextField minorRadiusTextField;
    @FXML
    private TextField minorAngleTextField;
    @FXML
    private TextField majorRadiusTextField;
    @FXML
    private TextField majorAngleTextField;

    @FXML
    private TextField rotationXTextField;
    @FXML
    private TextField rotationYTextField;
    @FXML
    private TextField rotationZTextField;

    @FXML
    private TextField scalingXTextField;
    @FXML
    private TextField scalingYTextField;
    @FXML
    private TextField scalingZTextField;

    @FXML
    private TextField translationXTextField;
    @FXML
    private TextField translationYTextField;
    @FXML
    private TextField translationZTextField;

    @FXML
    private TextField phiTextField;
    @FXML
    private TextField thetaTextField;
    @FXML
    private TextField rhoTextField;
    @FXML
    private TextField dTextField;

    @FXML
    private TextField lTextField;
    @FXML
    private TextField alphaTextField;

    @FXML
    private TextField axonometricRotationXTextField;
    @FXML
    private TextField axonometricRotationYTextField;
    @FXML
    private TextField axonometricRotationZTextField;

    @FXML
    private ComboBox<String> projectionsComboBox;
    @FXML
    private TabPane projectionsTabPane;

    @FXML
    private CheckBox viewTransformationCheckBox;

    @FXML
    private CheckBox lightCheckBox;
    @FXML
    private ColorPicker lightColorPicker;
    @FXML
    private TextField lightPositionXTextField;
    @FXML
    private TextField lightPositionYTextField;
    @FXML
    private TextField lightPositionZTextField;

    @FXML
    private ColorPicker modelColorPicker;

    @FXML
    private ObservableList<String> projections = FXCollections.observableArrayList(
            Projection.AXONOMETRIC.toString(),
            Projection.PERSPECTIVE.toString(),
            Projection.OBLIQUE.toString(),
            Projection.ORTHOGONAL.toString()
    );

    private Drawer drawer;
    private Torus torus;
    private TorusTransformer torusTransformer;
    private Projection projection = Projection.AXONOMETRIC;

    public void initializeDrawer(Canvas canvas) {
        drawer = new Drawer(canvas);
    }

    @FXML
    private void initialize() {
        projectionsComboBox.setItems(projections);
    }

    @FXML
    private void handleClearButton() {
        drawer.clear();
    }

    @FXML
    private void handleRotateButton() {
        try {
            double rX = parseDouble(rotationXTextField.getText().trim());
            double rY = parseDouble(rotationYTextField.getText().trim());
            double rZ = parseDouble(rotationZTextField.getText().trim());

            if (rX != 0 || rY != 0 || rZ != 0) {
                torus = torusTransformer.rotate(rX, rY, rZ);
                logger.debug("Torus has been rotated.");
                handleDrawButton();
            }
        } catch (Exception e) {
            logger.error("Torus hasn't been rotated.");
        }
    }

    @FXML
    private void handleScaleButton() {
        try {
            double sX = parseDouble(scalingXTextField.getText().trim());
            double sY = parseDouble(scalingYTextField.getText().trim());
            double sZ = parseDouble(scalingZTextField.getText().trim());

            if (sX != 0 && sY != 0 && sZ != 0) {
                torus = torusTransformer.scale(sX, sY, sZ);
                logger.debug("Torus has been scaled.");
                handleDrawButton();
            }
        } catch (Exception e) {
            logger.error("Torus hasn't been scaled.");
        }
    }

    @FXML
    private void handleTranslateButton() {
        try {
            double dX = parseDouble(translationXTextField.getText().trim());
            double dY = parseDouble(translationYTextField.getText().trim());
            double dZ = parseDouble(translationZTextField.getText().trim());

            if (dX != 0 || dY != 0 || dZ != 0) {
                drawer.clear();
                torus = torusTransformer.translate(dX, dY, dZ);
                logger.debug("Torus has been translated.");
                handleDrawButton();
            }
        } catch (Exception e) {
            logger.error("Torus hasn't been translated.");
        }
    }

    @FXML
    private void handleBuildButton() {
        drawer.clear();

        int minorRadius = parseInt(minorRadiusTextField.getText().trim());
        int minorAngle = parseInt(minorAngleTextField.getText().trim());
        int majorRadius = parseInt(majorRadiusTextField.getText().trim());
        int majorAngle = parseInt(majorAngleTextField.getText().trim());

        if (minorRadius != 0 && minorAngle != 0 && majorRadius != 0 && majorAngle != 0) {
            TorusBuilder torusBuilder = new TorusBuilder(minorRadius, minorAngle, majorRadius, majorAngle);

            try {
                torus = torusBuilder.build();
                torusTransformer = new TorusTransformer(torus);
                logger.debug("Torus model has been built.");
                boolean isLightSelected = isLightSelected();
                Color color = getColor();
                Point lightPosition = getLightPosition();
                drawer.drawView(torus.getFacets(), projection, isLightSelected, color, lightPosition);
            } catch (Exception e) {
                logger.error("Torus model hasn't been built.");
            }
        }
    }

    private Map<String, Double> getViewParameters(Projection projection, boolean isViewTransformationSelected) {
        Map<String, Double> viewParameters = new HashMap<>();

        switch (projection) {
            case PERSPECTIVE:
                Map<String, Double> perspectiveParameters = getPerspectiveParameters();
                viewParameters.putAll(perspectiveParameters);
                break;
            case AXONOMETRIC:
                Map<String, Double> axonometricParameters = getAxonometricParameters();
                viewParameters.putAll(axonometricParameters);
                break;
            case ORTHOGONAL:
                break;
            case OBLIQUE:
                Map<String, Double> obliqueParameters = getObliqueParameters();
                viewParameters.putAll(obliqueParameters);
                break;
        }

        if (isViewTransformationSelected) {
            Map<String, Double> viewTransformationParameters = getViewTransformationParameters();
            viewParameters.putAll(viewTransformationParameters);
        }

        return viewParameters;
    }

    private Map<String, Double> getPerspectiveParameters() {
        Map<String, Double> perspectiveParameters = new HashMap<>();

        double phi = parseDouble(phiTextField.getText().trim());
        perspectiveParameters.put(Constants.PHI, phi);

        double rho = parseDouble(rhoTextField.getText().trim());
        perspectiveParameters.put(Constants.RHO, rho);

        double theta = parseDouble(thetaTextField.getText().trim());
        perspectiveParameters.put(Constants.THETA, theta);

        double d = parseDouble(dTextField.getText().trim());
        perspectiveParameters.put(Constants.D, d);

        return perspectiveParameters;
    }

    private Map<String, Double> getAxonometricParameters() {
        Map<String, Double> axonometricParameters = new HashMap<>();

        double rXAxonometric = parseDouble(axonometricRotationXTextField.getText().trim());
        axonometricParameters.put(Constants.RX_AXONOMETRIC, rXAxonometric);

        double rYAxonometric = parseDouble(axonometricRotationYTextField.getText().trim());
        axonometricParameters.put(Constants.RY_AXONOMETRIC, rYAxonometric);

        double rZAxonometric = parseDouble(axonometricRotationZTextField.getText().trim());
        axonometricParameters.put(Constants.RZ_AXONOMETRIC, rZAxonometric);

        return axonometricParameters;
    }

    private Map<String, Double> getObliqueParameters() {
        Map<String, Double> obliqueParameters = new HashMap<>();

        double l = parseDouble(lTextField.getText().trim());
        obliqueParameters.put(Constants.L, l);

        double alpha = parseDouble(alphaTextField.getText().trim());
        obliqueParameters.put(Constants.ALPHA, alpha);

        return obliqueParameters;
    }

    private Map<String, Double> getViewTransformationParameters() {
        Map<String, Double> viewTransformationParameters = new HashMap<>();

        double rho = parseDouble(rhoTextField.getText().trim());
        viewTransformationParameters.put(Constants.RHO, rho);

        double phi = parseDouble(phiTextField.getText().trim());
        viewTransformationParameters.put(Constants.PHI, phi);

        double theta = parseDouble(thetaTextField.getText().trim());
        viewTransformationParameters.put(Constants.THETA, theta);

        return viewTransformationParameters;
    }

    @FXML
    private void handleDrawButton() {
        drawer.clear();
        boolean isViewTransformationSelected = isViewTransformationSelected();
        Color color = getColor();
        boolean isLightSelected = isLightSelected();
        Point lightPosition = getLightPosition();
        Map<String, Double> parameters = getViewParameters(projection, isViewTransformationSelected);
        Torus newTorus = getModelView(torus, projection, parameters, isViewTransformationSelected);
        drawer.drawView(newTorus.getFacets(), projection, isLightSelected, color, lightPosition);
    }

    @FXML
    private void onProjectionComboBoxClicked() {
        defineProjection();
        disableTabs(this.projection);
    }

    private void defineProjection() {
        Projection projection;

        try {
            String selectedItem = projectionsComboBox.getSelectionModel().getSelectedItem();
            projection = Projection.valueOf(selectedItem);
        } catch (Exception e) {
            projection = Projection.AXONOMETRIC;
        }

        this.projection = projection;
    }

    private void disableTabs(Projection projection) {
        ObservableList<Tab> tabs = projectionsTabPane.getTabs();

        for (Tab tab : tabs) {
            if (projection.toString().equals(tab.getText().toUpperCase())) {
                tab.setDisable(false);
            } else {
                tab.setDisable(true);
            }
        }
    }

    private Torus getModelView(Torus torus, Projection projection, Map<String, Double> parameters, boolean isViewTransformation) {
        Torus newTorus = torus;

        switch (projection) {
            case AXONOMETRIC:
                double axonometricRotationX = parameters.get(Constants.RX_AXONOMETRIC);
                double axonometricRotationY = parameters.get(Constants.RY_AXONOMETRIC);
                double axonometricRotationZ = parameters.get(Constants.RZ_AXONOMETRIC);
                newTorus = torusTransformer.axonometric(axonometricRotationX, axonometricRotationY, axonometricRotationZ);
                break;
            case ORTHOGONAL:
                break;
            case OBLIQUE:
                double l = parameters.get(Constants.L);
                double alpha = parameters.get(Constants.ALPHA);
                if (alpha % 360 >= 0 && alpha % 360 <= 90)
                    TorusUtils.sortFacets(torus.getFacets(), SortOrder.Z_DESCENDING);
                else if (alpha % 360 > 90 && alpha % 360 <= 180)
                    TorusUtils.sortFacets(torus.getFacets(), SortOrder.Z_DESCENDING);
                else if (alpha % 360 > 180 && alpha % 360 <= 270)
                    TorusUtils.sortFacets(torus.getFacets(), SortOrder.Z_DESCENDING);
                else if (alpha % 360 > 270 && alpha % 360 <= 360)
                    TorusUtils.sortFacets(torus.getFacets(), SortOrder.Z_DESCENDING);
                newTorus = torusTransformer.oblique(l, alpha);
                break;
            case PERSPECTIVE:
                double d = parameters.get(Constants.D);
                TorusUtils.sortFacets(newTorus.getFacets(), SortOrder.Z_ASCENDING);
                newTorus = torusTransformer.perspective(d);
                break;
            default:
                throw new IllegalArgumentException("There is no such projection.");
        }

        if (isViewTransformation) {
            double rho = parameters.get(Constants.RHO);
            double phi = parameters.get(Constants.PHI);
            double theta = parameters.get(Constants.THETA);

            newTorus = torusTransformer.viewTransform(rho, phi, theta);
        }

        for (Plane facet : torus.getFacets()) {
            for (Line rib : facet.getLines()) {
                for (Point point : rib.getPoints()) {
                    double x = (point.getX() >= Constants.MAX_APPROXIMATION)
                            ? Constants.MAX_APPROXIMATION
                            : (point.getX() <= -Constants.MAX_APPROXIMATION)
                            ? Constants.MAX_APPROXIMATION
                            : point.getX();
                    double y = (point.getY() >= Constants.MAX_APPROXIMATION)
                            ? Constants.MAX_APPROXIMATION
                            : (point.getY() <= -Constants.MAX_APPROXIMATION)
                            ? Constants.MAX_APPROXIMATION
                            : point.getY();
                    double z = (point.getZ() >= Constants.MAX_APPROXIMATION)
                            ? Constants.MAX_APPROXIMATION
                            : (point.getZ() <= -Constants.MAX_APPROXIMATION)
                            ? Constants.MAX_APPROXIMATION
                            : point.getZ();

                    point.setX(x);
                    point.setY(y);
                    point.setZ(z);
                }
            }
        }

        return newTorus;
    }

    private boolean isViewTransformationSelected() {
        return viewTransformationCheckBox.isSelected();
    }

    private boolean isLightSelected() {
        return lightCheckBox.isSelected();
    }

    private Point getLightPosition() {
        String xValue = lightPositionXTextField.getText().trim();
        double x = xValue.equals(EMPTY) ? 0.0 : Double.valueOf(xValue);

        String yValue = lightPositionXTextField.getText().trim();
        double y = xValue.equals(EMPTY) ? 0.0 : Double.valueOf(yValue);

        String zValue = lightPositionXTextField.getText().trim();
        double z = xValue.equals(EMPTY) ? 0.0 : Double.valueOf(zValue);

        return new Point(x, y, z);
    }

    private Color getColor() {
        return modelColorPicker.getValue();
    }

    private double parseDouble(String s) {
        return s.equals(EMPTY) ? 0 : Double.parseDouble(s);
    }

    private int parseInt(String s) {
        return s.equals(EMPTY) ? 0 : Integer.parseInt(s);
    }
}
