package com.ferrine.antridong.gui;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import java.util.function.Consumer;

public class LogAppender extends AppenderBase<ILoggingEvent> {
    private static Consumer<String> logConsumer;

    public static void setLogConsumer(Consumer<String> consumer) {
        logConsumer = consumer;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (logConsumer != null) {
            String message = String.format("[%s] %s - %s\n", 
                eventObject.getLevel(), 
                eventObject.getLoggerName(), 
                eventObject.getFormattedMessage());
            
            // Only capture WARN and ERROR/SEVERE
            if (eventObject.getLevel().isGreaterOrEqual(ch.qos.logback.classic.Level.WARN)) {
                Platform.runLater(() -> logConsumer.accept(message));
            }
        }
    }
}
