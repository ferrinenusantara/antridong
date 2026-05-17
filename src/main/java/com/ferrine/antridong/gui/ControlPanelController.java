package com.ferrine.antridong.gui;

import com.ferrine.antridong.AntridongApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlPanelController {
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu setupMenu;
	@FXML
	private Label statusLabel;
	@FXML
	private Button startBtn;
	@FXML
	private Button stopBtn;
	@FXML
	private Button restartBtn;
	@FXML
	private TextArea logArea;

	private ConfigurableApplicationContext springContext;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	@FXML
	public void initialize() {
		LogAppender.setLogConsumer(message -> {
			logArea.appendText(message);
		});
		// Setup is ENABLED when server is STOPPED (which is the initial state)
		setupMenu.setDisable(false);
	}

	@FXML
	private void startServer() {
		if (springContext != null && springContext.isRunning())
			return;

		updateStatus("Starting server...", true);
		executor.submit(() -> {
			try {
				springContext = SpringApplication.run(AntridongApplication.class);
				Platform.runLater(() -> {
					updateStatus("Status: Running", false);
					startBtn.setDisable(true);
					stopBtn.setDisable(false);
					restartBtn.setDisable(false);
					setupMenu.setDisable(true); // Disable Setup when running
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					logArea.appendText("[ERROR] Failed to start server: " + e.getMessage() + "\n");
					updateStatus("Status: Error", false);
					startBtn.setDisable(false);
					setupMenu.setDisable(false);
				});
			}
		});
	}

	@FXML
	private void stopServer() {
		if (springContext == null)
			return;

		updateStatus("Stopping server...", true);
		executor.submit(() -> {
			springContext.close();
			springContext = null;
			Platform.runLater(() -> {
				updateStatus("Status: Stopped", false);
				startBtn.setDisable(false);
				stopBtn.setDisable(true);
				restartBtn.setDisable(true);
				setupMenu.setDisable(false); // Enable Setup when stopped
			});
		});
	}

	@FXML
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
					setupMenu.setDisable(true); // Disable Setup when running
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					logArea.appendText("[ERROR] Failed to restart server: " + e.getMessage() + "\n");
					updateStatus("Status: Error", false);
					startBtn.setDisable(false);
					stopBtn.setDisable(true);
					restartBtn.setDisable(true);
					setupMenu.setDisable(false);
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
				setupMenu.setDisable(true);
			}
		});
	}

	@FXML
	private void openSetupUser() {
		openSetupWindow("/gui/SetupUser.fxml", "Setup User");
	}

	@FXML
	private void openSetupKategori() {
		openSetupWindow("/gui/SetupKategori.fxml", "Setup Kategori Antrian");
	}

	@FXML
	private void openSetupCounter() {
		openSetupWindow("/gui/SetupCounter.fxml", "Setup Counter");
	}

	private void openSetupWindow(String fxmlPath, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(loader.load()));
			stage.sizeToScene();
			stage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
			logArea.appendText("[ERROR] Failed to open " + title + ": " + e.getMessage() + "\n");
		}
	}

	public void shutdown() {
		if (springContext != null) {
			springContext.close();
		}
		executor.shutdownNow();
	}
}
