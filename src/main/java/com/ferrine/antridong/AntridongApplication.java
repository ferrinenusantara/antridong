package com.ferrine.antridong;

import com.ferrine.antridong.gui.ControlPanel;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication
public class AntridongApplication {

	public static void main(String[] args) {
		// Launch the JavaFX GUI instead of starting Spring directly
		Application.launch(ControlPanel.class, args);
	}

}
