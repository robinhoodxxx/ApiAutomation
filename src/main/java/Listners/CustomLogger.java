package Listners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CustomLogger {


    private BufferedWriter writer;
    private static final String LOGFILE = "./src/test/resources/Reports/logs";
    private static final StringBuilder logData = new StringBuilder();


    public enum LogType {
        INFO,
        WARNING,
        ERROR
    }

    // Private constructor to prevent instantiation
    private CustomLogger() {

        try {

            // Generate log file name with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File file = new File(LOGFILE);
            if (!file.exists()) {
                file.mkdir();
            }

            String logFilePath = LOGFILE + "/log_" + timestamp + ".txt"; // Log directory

            writer = new BufferedWriter(new FileWriter(logFilePath, true)); // Open log file for appending
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHelper {
        private static final CustomLogger INSTANCE = new CustomLogger();
    }


    // Method to get the singleton instance
    public static CustomLogger getInstance() {
        return  SingletonHelper.INSTANCE;
    }

    // Info logging method
    public void info(String message) {
        log(LogType.INFO, message);
    }

    // Warning logging method
    public void warning(String message) {
        log(LogType.WARNING, message);

    }

    public void warning(String message,Throwable e) {

        log(LogType.WARNING, message);
       e.printStackTrace();
    }

    public void error(String message) {

        log(LogType.ERROR, message);
        close();
        throw new RunStopException(message);
    }


    // Error logging method
    public void error(String message,Throwable e) {

        log(LogType.ERROR, message);
        close();
        throw new RunStopException(e);
    }

    // Private log method to format and write the log
    private void log(LogType type, String message) {
        String className = getCallingClassName(); // Get the calling class name
        String formattedMessage = formatMessage(type, className, message);
        logData.append(formattedMessage);
        logData.append("\n");
        System.out.println(formattedMessage); // Print to console
    }

    // Get the calling class name
    private String getCallingClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3].getClassName(); // Get the name of the calling class
    }

    // Format message
    private String formatMessage(LogType type, String className, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM : HH:mm:ss"));
        return String.format("[%s] [%s]  [%s] \t:%s", timestamp, className, type.name(), message);
    }


    // Close the logger and the BufferedWriter
    public void close() {
        try {
            if (writer != null) {
                writer.append(logData);
                writer.close();
            }
        } catch (Exception e) {
            throw new RunStopException("log Write failed:" , e);
        }
    }

    // Example usage
    public static void main(String[] args) {
        CustomLogger logger = CustomLogger.getInstance();
        logger.info("Application started.");
        logger.warning("This is a warning message.");

    }
}
