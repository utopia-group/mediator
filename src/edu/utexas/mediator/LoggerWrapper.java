package edu.utexas.mediator;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerWrapper {

  private final static boolean DEBUG_MODE = true;

  private static Logger instance = null;

  /**
   * Get the default singleton logger.
   */
  public static synchronized Logger getInstance() {
    if (instance == null) {
      instance = getStderrLogger();
      instance.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
    }
    return instance;
  }

  /**
   * Set the logger instance.
   */
  public static synchronized void setInstance(Logger logger) {
    instance = logger;
  }

  /**
   * Get a new logger that logs in stderr for a class.
   */
  public static Logger getStderrLogger() {
    Logger logger = Logger.getGlobal();
    try {
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
      consoleHandler.setFormatter(new CustomFormatter());
      logger.addHandler(consoleHandler);
      logger.setUseParentHandlers(false);
    } catch (SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException("Logger error");
    }
    return logger;
  }

  /**
   * Custom log formatter.
   */
  private static class CustomFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(record.getLevel()).append(": ");
      buffer.append(record.getMessage());
      buffer.append(System.lineSeparator());
      return buffer.toString();
    }
  }
}
