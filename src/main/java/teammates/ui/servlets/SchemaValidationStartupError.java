package teammates.ui.servlets;

import org.hibernate.tool.schema.spi.SchemaManagementException;

/**
 * Formats Hibernate schema validation failures for local development startup.
 */
final class SchemaValidationStartupError {

    private static final String SCHEMA_VALIDATION_PREFIX = "Schema-validation:";

    private SchemaValidationStartupError() {
        // Utility class
    }

    static boolean isSchemaValidationFailure(Throwable t) {
        return findSchemaValidationFailure(t) != null;
    }

    static String getSchemaValidationMessage(Throwable t) {
        Throwable schemaValidationFailure = findSchemaValidationFailure(t);
        if (schemaValidationFailure == null) {
            return t.getMessage();
        }
        return schemaValidationFailure.getMessage().trim();
    }

    static String buildDevServerMessage(Throwable t) {
        return String.join(System.lineSeparator(),
                "",
                "============================================================",
                "Database schema validation failed:",
                getSchemaValidationMessage(t),
                "",
                "Your local database schema is out of date.",
                "Run: ./gradlew liquibaseUpdate",
                "Then restart the local server.",
                "============================================================",
                "");
    }

    static DevServerStartupException handleDevServerStartupFailure(Throwable t) {
        String message = buildDevServerMessage(t);
        return new DevServerStartupException(message);
    }

    private static Throwable findSchemaValidationFailure(Throwable t) {
        Throwable current = t;
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
