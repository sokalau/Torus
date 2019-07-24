/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling;

import geometricmodeling.common.Constants;
import geometricmodeling.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeometricModeling extends Application {
    private static final Logger logger = LogManager.getLogger();
    private static final String GEOMETRIC_MODELING = "Geometric modeling";
    private static final String RESOURCE_NAME = "fxml/GeometricModeling.fxml";

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource(RESOURCE_NAME));
            Parent root = loader.load();

            StackPane canvasHolder = new StackPane();
            Canvas canvas = new Canvas(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
            canvasHolder.getChildren().add(canvas);
            canvasHolder.setLayoutX(Constants.CANVAS_LAYOUT);
            AnchorPane anchorPane = (AnchorPane) root;
            anchorPane.getChildren().add(canvasHolder);

            Controller controller = loader.getController();
            controller.initializeDrawer(canvas);

            primaryStage.setTitle(GEOMETRIC_MODELING);
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("An exception occurred while starting JavaFX application: ", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}