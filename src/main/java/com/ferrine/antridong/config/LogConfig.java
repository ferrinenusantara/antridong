package com.ferrine.antridong.config;

import java.io.File;
import java.util.logging.*;

public class LogConfig {
    public static void setupLogging() {
        try {
            // Ensure logs directory exists
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }

            // Set system property for standard log formatting
            // Format: [YYYY-MM-DD HH:MM:SS] [LEVEL] Message
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "[%1$tF %1$tT] [%4$-7s] %5$s%6$s%n");

            // Configure root logger
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            
            // Limit to 2MB (2 * 1024 * 1024 bytes), with 5 rolling files, append mode
            FileHandler fileHandler = new FileHandler("logs/antridong.log", 2 * 1024 * 1024, 5, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            
            rootLogger.addHandler(fileHandler);
            
            // Set root logger level
            rootLogger.setLevel(Level.INFO);
            
            rootLogger.info("java.util.logging (JUL) initialized successfully. Logs are saved to 'logs/antridong.log' with a 2MB rotation limit.");
        } catch (Exception e) {
            System.err.println("Failed to initialize java.util.logging: " + e.getMessage());
        }
    }
}
