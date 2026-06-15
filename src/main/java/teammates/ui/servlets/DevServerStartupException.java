package teammates.ui.servlets;

/**
 * Startup exception for local development failures that are already reported with a friendly console message.
 */
public class DevServerStartupException extends RuntimeException {

    public DevServerStartupException(String message) {
        super(message, null, false, false);
    }

}
