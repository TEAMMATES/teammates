package teammates.ui.devserverstartuperrorhandlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.ui.errorhandlers.SchemaValidationStartupErrorHandler;

/**
 * SUT: {@link SchemaValidationStartupErrorHandler}.
 */
public class SchemaValidationStartupErrorHandlerTest extends BaseTestCase {

    private static final String SCHEMA_VALIDATION_MESSAGE =
            "Schema-validation: missing column [institute_id] in table [account_requests]";

    private final SchemaValidationStartupErrorHandler handler = new SchemaValidationStartupErrorHandler();

    @Test
    public void testCanHandle_directSchemaManagementException_returnsTrue() {
        SchemaManagementException exception = new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE);

        assertTrue(handler.canHandle(exception));
    }

    @Test
    public void testCanHandle_wrappedSchemaManagementException_returnsTrue() {
        RuntimeException exception = new RuntimeException("Failed to build session factory",
                new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE));

        assertTrue(handler.canHandle(exception));
    }

    @Test
    public void testCanHandle_nonSchemaValidationException_returnsFalse() {
        RuntimeException exception = new RuntimeException("Failed to connect to database");

        assertFalse(handler.canHandle(exception));
    }

    @Test
    public void testBuildErrorMessage_schemaValidationException_includesCommandDocsAndExactError() {
        SchemaManagementException exception = new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE);

        String message = handler.buildErrorMessage(exception);

        assertTrue(message.contains(SCHEMA_VALIDATION_MESSAGE));
        assertTrue(message.contains("./gradlew liquibaseUpdate"));
        assertTrue(message.contains("docs/how-to/schema-migration.md"));
    }

}
