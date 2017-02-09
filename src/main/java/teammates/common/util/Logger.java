package teammates.common.util;

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
     * @see java.util.logging.Logger#fine(String)
     */
    public void fine(String msg) {
        log.fine(msg);
    }

    /**
     * @see java.util.logging.Logger#info(String)
     */
    public void info(String msg) {
        log.info(msg);
    }

    /**
     * @see java.util.logging.Logger#warning(String)
     */
    public void warning(String msg) {
        log.warning(msg);
    }

    /**
     * @see java.util.logging.Logger#severe(String)
     */
    public void severe(String msg) {
        log.severe(msg);
    }

}
