package swp.com.fxapp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class ImageLogger {
    private static final Logger LOGGER = Logger.getLogger(ImageLogger.class.getName());
    private static final String LOG_FILE = "image_operations.log";
    private static boolean initialized = false;

    static {
        setupLogger();
    }

    private static void setupLogger() {
        if (!initialized) {
            try {
                FileHandler fileHandler = new FileHandler(LOG_FILE, true);
                fileHandler.setFormatter(new SimpleFormatter() {
                    @Override
                    public String format(LogRecord record) {
                        return String.format("[%s] %s%n",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                record.getMessage());
                    }
                });
                LOGGER.addHandler(fileHandler);
                LOGGER.setLevel(Level.ALL);
                initialized = true;
            } catch (IOException e) {
                System.err.println("Failed to initialize logger: " + e.getMessage());
            }
        }
    }

    public static void logImageOperation(String operation, String details) {
        String logMessage = String.format("Operation: %s | Details: %s",
                operation, details);
        LOGGER.info(logMessage);
    }

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logWarning(String message) {
        LOGGER.warning(message);
    }

    public static void logError(String message, Throwable t) {
        LOGGER.log(Level.SEVERE, message, t);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}