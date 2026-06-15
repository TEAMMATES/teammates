package teammates.ui.errorhandlers;

/**
 * Handles startup errors that can be formatted with dev-server-friendly messages.
 */
public interface StartupErrorHandler {

    /**
     * Returns whether this handler can format the given startup error.
     */
    boolean canHandle(Throwable t);

    /**
     * Builds the dev-server-friendly message for the given startup error.
     */
    String buildErrorMessage(Throwable t);
}
