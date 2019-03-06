package teammates.common.util;

import java.util.logging.Level;

/**
 * Allows any component of the application to log messages at appropriate levels.
 */
public final class Logger {

    private java.util.logging.Logger log;

    private Logger() {
        StackTraceElement logRequester = Thread.currentThread().getStackTrace()[2];
        this.log = java.util.logging.Logger.getLogger(logRequester.getClassName());
    }

    public static Logger getLogger() {
        return new Logger();
    }

    /**
     * Logs a message at FINE level.
     *
     * @see java.util.logging.Logger#fine(String)
     */
    public void fine(String msg) {
        log.fine(msg);
    }

    /**
     * Logs a message at INFO level.
     *
     * @see java.util.logging.Logger#info(String)
     */
    public void info(String msg) {
        log.info(msg);
    }

    /**
     * Logs a message at WARNING level.
     *
     * @see java.util.logging.Logger#warning(String)
     */
    public void warning(String msg) {
        log.warning(msg);
    }

    /**
     * Logs a message at SEVERE level.
     *
     * @see java.util.logging.Logger#severe(String)
     */
    public void severe(String msg) {
        log.severe(msg);
    }

    /**
     * Logs a message at SEVERE level, with associated Throwable information.
     *
     * @see java.util.logging.Logger#log(Level, String, Throwable)
     */
    public void logThrowable(Throwable thrown) {
        log.log(Level.SEVERE, thrown.getMessage(), thrown);
    }
}
