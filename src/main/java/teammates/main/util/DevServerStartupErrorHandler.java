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
     * A list of builders that can recognize startup errors and provide a dev-server-friendly message.
     */
    private static final List<Function<Throwable, Optional<String>>> ERROR_MESSAGE_BUILDERS = List.of(
            DevServerStartupErrorHandler::buildPendingDatabaseMigrationsMessage);

    private DevServerStartupErrorHandler() {
        // Utility class
    }

    /**
     * Transforms a recognized startup error into a dev-server-friendly exception.
     * Returns the original exception when no builder recognizes it.
     */
    public static Exception transform(Exception e) {
        return ERROR_MESSAGE_BUILDERS.stream()
                .map(builder -> builder.apply(e))
                .flatMap(optional -> optional.stream())
                .findFirst()
                .<Exception>map(message -> new DevServerStartupException(message))
                .orElse(e);
    }

    /**
     * Builds a dev-server-friendly message for a pending database migrations error.
     */
    private static Optional<String> buildPendingDatabaseMigrationsMessage(Throwable error) {
        if (!(error instanceof PendingDatabaseMigrationsException)) {
            return Optional.empty();
        }
        PendingDatabaseMigrationsException pendingMigrationsFailure = (PendingDatabaseMigrationsException) error;
        return Optional.of(String.format(SCHEMA_OUT_OF_DATE_MESSAGE, pendingMigrationsFailure.getMigrationStatus()));
    }

}
