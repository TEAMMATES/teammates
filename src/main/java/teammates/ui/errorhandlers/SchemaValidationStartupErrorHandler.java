package teammates.ui.errorhandlers;

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
    public boolean canHandle(Exception e) {
        return findSchemaValidationFailure(e) != null;
    }

    @Override
    public String buildErrorMessage(Exception e) {
        return String.format(MESSAGE, getSchemaValidationMessage(e));
    }

    private static String getSchemaValidationMessage(Exception e) {
        Exception schemaValidationFailure = findSchemaValidationFailure(e);
        if (schemaValidationFailure == null) {
            return e.getMessage();
        }
        return schemaValidationFailure.getMessage() != null
                ? schemaValidationFailure.getMessage().trim() : null;
    }

    private static Exception findSchemaValidationFailure(Exception e) {
        Exception current = e;
        while (current != null) {
            String message = current.getMessage();
            if (current instanceof SchemaManagementException
                    || message != null && message.trim().startsWith(SCHEMA_VALIDATION_PREFIX)) {
                return current;
            }
            current = (Exception) current.getCause();
        }
        return null;
    }

}
