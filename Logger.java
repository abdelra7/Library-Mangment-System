package com.library.app.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logger class for the application.
 */
public class Logger {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String LOG_FILE = "library_app.log";
    
    private final String className;
    private static boolean logToFile = true;
    private static boolean logToConsole = true;
    
    /**
     * Creates a new logger for the specified class.
     * 
     * @param className The name of the class
     */
    public Logger(String className) {
        this.className = className;
    }
    
    /**
     * Logs an informational message.
     * 
     * @param message The message to log
     */
    public void info(String message) {
        log("INFO", message, null);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message The message to log
     */
    public void warn(String message) {
        log("WARN", message, null);
    }
    
    /**
     * Logs a warning message with an exception.
     * 
     * @param message The message to log
     * @param e The exception to log
     */
    public void warn(String message, Exception e) {
        log("WARN", message, e);
    }
    
    /**
     * Logs an error message.
     * 
     * @param message The message to log
     */
    public void error(String message) {
        log("ERROR", message, null);
    }
    
    /**
     * Logs an error message with an exception.
     * 
     * @param message The message to log
     * @param e The exception to log
     */
    public void error(String message, Exception e) {
        log("ERROR", message, e);
    }
    
    /**
     * Logs a debug message.
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        log("DEBUG", message, null);
    }
    
    /**
     * Sets whether to log to a file.
     * 
     * @param logToFile Whether to log to a file
     */
    public static void setLogToFile(boolean logToFile) {
        Logger.logToFile = logToFile;
    }
    
    /**
     * Sets whether to log to the console.
     * 
     * @param logToConsole Whether to log to the console
     */
    public static void setLogToConsole(boolean logToConsole) {
        Logger.logToConsole = logToConsole;
    }
    
    /**
     * Internal method to log a message.
     * 
     * @param level The log level
     * @param message The message to log
     * @param e The exception to log, or null if none
     */
    private void log(String level, String message, Exception e) {
        String timestamp;
        synchronized (DATE_FORMAT) {
            timestamp = DATE_FORMAT.format(new Date());
        }
        
        String logEntry = String.format("[%s] [%s] [%s] %s", 
                                        timestamp, level, className, message);
        
        if (logToConsole) {
            if ("ERROR".equals(level)) {
                System.err.println(logEntry);
                if (e != null) {
                    e.printStackTrace(System.err);
                }
            } else {
                System.out.println(logEntry);
                if (e != null) {
                    e.printStackTrace(System.out);
                }
            }
        }
        
        if (logToFile) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(logEntry);
                if (e != null) {
                    e.printStackTrace(writer);
                }
            } catch (IOException ex) {
                System.err.println("Error writing to log file: " + ex.getMessage());
            }
        }
    }
}
