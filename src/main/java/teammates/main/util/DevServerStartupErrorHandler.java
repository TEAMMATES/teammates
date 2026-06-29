package teammates.main.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import teammates.common.exception.PendingDatabaseMigrationsException;
import teammates.main.exception.DevServerStartupException;

/**
 * Handles startup errors with dev-server-friendly messages where possible.
 */
public final class DevServerStartupErrorHandler {

    private static final String SCHEMA_OUT_OF_DATE_MESSAGE = String.join(System.lineSeparator(),
            "",
            "============================================================",
            "Database schema is not up to date:",
            "%s",
            "",
            "Your local database schema is out of date.",
            "Run: ./gradlew liquibaseUpdate",
            "Then restart the local server.",
            "",
            "For more details, see docs/how-to/schema-migration.md",
            "============================================================",
            "");

    /**
     * A list of providers that can recognize startup errors and provide a dev-server-friendly message.
     */
    private static final List<Function<Throwable, Optional<String>>> ERROR_MESSAGE_PROVIDERS = List.of(
            DevServerStartupErrorHandler::buildPendingDatabaseMigrationsMessage);

    private DevServerStartupErrorHandler() {
        // Utility class
    }

    /**
     * Transforms a recognized startup error into a dev-server-friendly exception.
     * Returns the original exception when no provider recognizes it.
     */
    public static Exception transform(Exception e) {
        return ERROR_MESSAGE_PROVIDERS.stream()
                .map(provider -> provider.apply(e))
                .flatMap(Optional::stream)
                .findFirst()
                .<Exception>map(message -> new DevServerStartupException(message))
                .orElse(e);
    }

    private static Optional<String> buildPendingDatabaseMigrationsMessage(Throwable error) {
        PendingDatabaseMigrationsException pendingMigrationsFailure = findPendingDatabaseMigrationsFailure(error);
        return pendingMigrationsFailure == null
                ? Optional.empty()
                : Optional.of(String.format(SCHEMA_OUT_OF_DATE_MESSAGE, pendingMigrationsFailure.getMigrationStatus()));
    }

    private static PendingDatabaseMigrationsException findPendingDatabaseMigrationsFailure(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof PendingDatabaseMigrationsException) {
                return (PendingDatabaseMigrationsException) current;
            }
            current = current.getCause();
        }
        return null;
    }

}
