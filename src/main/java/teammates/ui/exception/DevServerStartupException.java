package teammates.ui.exception;

/**
 * Exception thrown when an error occurs during dev server startup.
 */
public class DevServerStartupException extends RuntimeException {
    public DevServerStartupException(String message) {
        super(message, null, true, false);
    }
}
