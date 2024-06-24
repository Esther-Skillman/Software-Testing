package org.example.frontendapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class FrontendAppApplication {

	public static void main(String[] args) {
		if (!GraphicsEnvironment.isHeadless()) {
			SwingUtilities.invokeLater((new Runnable() {
				@Override
				public void run() {
					AdmissionsForSpecificPatientGUI gui = new AdmissionsForSpecificPatientGUI();
					gui.setVisible(true);
				}
			}));
		}

	}


}

