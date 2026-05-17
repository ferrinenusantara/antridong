package com.ferrine.antridong;

import com.ferrine.antridong.config.LogConfig;
import com.ferrine.antridong.gui.ControlPanel;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication
public class AntridongApplication {

	public static void main(String[] args) {
		// Initialize java.util.logging with rotation and file output
		LogConfig.setupLogging();

		if (args.length > 0 && args[0].equalsIgnoreCase("--headless")) {
			org.springframework.boot.SpringApplication.run(AntridongApplication.class, args);
		} else {
			// Launch the JavaFX GUI instead of starting Spring directly
			Application.launch(ControlPanel.class, args);
		}
	}

}
