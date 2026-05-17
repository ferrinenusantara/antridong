package com.ferrine.antridong.gui;

import com.ferrine.antridong.database.DatabaseManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControlPanel extends Application {

    private ControlPanelController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize Database independently of Spring Boot
        DatabaseManager.init();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ControlPanel.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        
        controller = loader.getController();

        primaryStage.setTitle("Antridong | Server Control");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (controller != null) {
                controller.shutdown();
            }
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
