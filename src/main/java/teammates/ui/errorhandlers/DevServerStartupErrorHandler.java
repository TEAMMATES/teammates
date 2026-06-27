package teammates.ui.errorhandlers;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.hibernate.tool.schema.spi.SchemaManagementException;

import teammates.ui.exception.DevServerStartupException;

/**
 * Handles startup errors with dev-server-friendly messages where possible.
 */
public final class DevServerStartupErrorHandler {

    private static final String SCHEMA_VALIDATION_PREFIX = "Schema-validation:";
    private static final String SCHEMA_VALIDATION_MESSAGE = String.join(System.lineSeparator(),
            "",
            "============================================================",
            "Database schema validation failed:",
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
     * Add new common startup errors here in order of specificity.
     */
    private static final List<Function<Throwable, Optional<String>>> ERROR_MESSAGE_PROVIDERS = List.of(
            DevServerStartupErrorHandler::buildSchemaValidationMessage);

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

    private static Optional<String> buildSchemaValidationMessage(Throwable error) {
        Throwable schemaValidationFailure = findSchemaValidationFailure(error);
        if (schemaValidationFailure == null) {
            return Optional.empty();
        }

        String details = Optional.ofNullable(schemaValidationFailure.getMessage())
                .map(String::trim)
                .filter(message -> !message.isEmpty())
                .orElse("Hibernate reported a schema validation error.");
        return Optional.of(String.format(SCHEMA_VALIDATION_MESSAGE, details));
    }

    private static Throwable findSchemaValidationFailure(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (current instanceof SchemaManagementException
                    || message != null && message.trim().startsWith(SCHEMA_VALIDATION_PREFIX)) {
                return current;
            }
            current = current.getCause();
        }
        return null;
    }

}
