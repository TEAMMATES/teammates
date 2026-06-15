package teammates.ui.errorhandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates the matching {@link StartupErrorHandler} for a startup error.
 */
public final class DevServerStartupErrorHandlerFactory {

    private static final List<Class<? extends StartupErrorHandler>> ERROR_HANDLERS = new ArrayList<>();

    static {
        map(SchemaValidationStartupErrorHandler.class);
    }

    private DevServerStartupErrorHandlerFactory() {
        // Utility class
    }

    private static void map(Class<? extends StartupErrorHandler> errorHandlerClass) {
        ERROR_HANDLERS.add(errorHandlerClass);
    }

    /**
     * Returns the matching {@link StartupErrorHandler} for the startup error.
     */
    public static Optional<StartupErrorHandler> getHandler(Throwable t) {
        return ERROR_HANDLERS.stream()
                .map(DevServerStartupErrorHandlerFactory::instantiate)
                .filter(handler -> handler.canHandle(t))
                .findFirst();
    }

    private static StartupErrorHandler instantiate(Class<? extends StartupErrorHandler> errorHandlerClass) {
        try {
            return errorHandlerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            assert false : "Could not create the startup error handler " + errorHandlerClass.getSimpleName();
            return null;
        }
    }

}
