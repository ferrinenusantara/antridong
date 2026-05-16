package com.ferrine.antridong.gui;

import com.ferrine.antridong.AntridongApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlPanel extends Application {
    private ConfigurableApplicationContext springContext;
    private TextArea logArea;
    private Button startBtn, stopBtn, restartBtn;
    private Label statusLabel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void start(Stage primaryStage) {
        // UI Components
        Label titleLabel = new Label("Antridong Control Panel");
        titleLabel.getStyleClass().add("title-label");

        statusLabel = new Label("Status: Stopped");
        statusLabel.getStyleClass().add("status-label");

        startBtn = new Button("START");
        startBtn.getStyleClass().addAll("btn", "btn-start");
        
        stopBtn = new Button("STOP");
        stopBtn.getStyleClass().addAll("btn", "btn-stop");
        stopBtn.setDisable(true);

        restartBtn = new Button("RESTART");
        restartBtn.getStyleClass().addAll("btn", "btn-restart");
        restartBtn.setDisable(true);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPromptText("Warnings and errors will appear here...");
        
        VBox logBox = new VBox(logArea);
        logBox.getStyleClass().add("log-container");
        VBox.setVgrow(logBox, Priority.ALWAYS);

        HBox buttonBox = new HBox(startBtn, stopBtn, restartBtn);
        buttonBox.getStyleClass().add("button-container");
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, titleLabel, statusLabel, buttonBox, logBox);
        root.getStyleClass().add("main-container");

        // Set up Log Redirection
        LogAppender.setLogConsumer(message -> {
            logArea.appendText(message);
        });

        // Button Actions
        startBtn.setOnAction(e -> startServer());
        stopBtn.setOnAction(e -> stopServer());
        restartBtn.setOnAction(e -> restartServer());

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());

        primaryStage.setTitle("Antridong | Server Control");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (springContext != null) {
                springContext.close();
            }
            executor.shutdownNow();
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    private void startServer() {
        if (springContext != null && springContext.isRunning()) return;

        updateStatus("Starting server...", true);
        executor.submit(() -> {
            try {
                springContext = SpringApplication.run(AntridongApplication.class);
                Platform.runLater(() -> {
                    updateStatus("Status: Running", false);
                    startBtn.setDisable(true);
                    stopBtn.setDisable(false);
                    restartBtn.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    logArea.appendText("[ERROR] Failed to start server: " + e.getMessage() + "\n");
                    updateStatus("Status: Error", false);
                    startBtn.setDisable(false);
                });
            }
        });
    }

    private void stopServer() {
        if (springContext == null) return;

        updateStatus("Stopping server...", true);
        executor.submit(() -> {
            springContext.close();
            springContext = null;
            Platform.runLater(() -> {
                updateStatus("Status: Stopped", false);
                startBtn.setDisable(false);
                stopBtn.setDisable(true);
                restartBtn.setDisable(true);
            });
        });
    }

    private void restartServer() {
        updateStatus("Restarting server...", true);
        executor.submit(() -> {
            if (springContext != null) {
                springContext.close();
            }
            try {
                springContext = SpringApplication.run(AntridongApplication.class);
                Platform.runLater(() -> {
                    updateStatus("Status: Running", false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    logArea.appendText("[ERROR] Failed to restart server: " + e.getMessage() + "\n");
                    updateStatus("Status: Error", false);
                    startBtn.setDisable(false);
                    stopBtn.setDisable(true);
                    restartBtn.setDisable(true);
                });
            }
        });
    }

    private void updateStatus(String text, boolean disableButtons) {
        Platform.runLater(() -> {
            statusLabel.setText(text);
            if (disableButtons) {
                startBtn.setDisable(true);
                stopBtn.setDisable(true);
                restartBtn.setDisable(true);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
