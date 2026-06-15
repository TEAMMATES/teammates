package teammates.ui.errorhandlers;

import java.util.Optional;

import teammates.ui.exception.DevServerStartupException;

/**
 * Handles startup errors with dev-server-friendly messages where possible.
 */
public final class DevServerStartupErrorHandler {

    private DevServerStartupErrorHandler() {
        // Utility class
    }

    /**
     * Throws a dev-server-friendly exception if a handler matches.
     */
    public static void throwIfHandled(Throwable t) {
        Optional<StartupErrorHandler> handler = DevServerStartupErrorHandlerFactory.getHandler(t);
        if (handler.isEmpty()) {
            return;
        }

        throw new DevServerStartupException(handler.get().buildErrorMessage(t));
    }

}
