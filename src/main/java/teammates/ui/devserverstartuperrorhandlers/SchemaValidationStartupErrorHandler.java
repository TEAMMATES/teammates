package teammates.ui.devserverstartuperrorhandlers;

import org.hibernate.tool.schema.spi.SchemaManagementException;

/**
 * Formats Hibernate schema validation failures for local development startup.
 */
public final class SchemaValidationStartupErrorHandler implements StartupErrorHandler {

    private static final String SCHEMA_VALIDATION_PREFIX = "Schema-validation:";
    private static final String MESSAGE = String.join(System.lineSeparator(),
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

    @Override
    public boolean canHandle(Throwable t) {
        return findSchemaValidationFailure(t) != null;
    }

    @Override
    public String buildErrorMessage(Throwable t) {
        return String.format(MESSAGE, getSchemaValidationMessage(t));
    }

    private static String getSchemaValidationMessage(Throwable t) {
        Throwable schemaValidationFailure = findSchemaValidationFailure(t);
        if (schemaValidationFailure == null) {
            return t.getMessage();
        }
        return schemaValidationFailure.getMessage().trim();
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
