package teammates.ui.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.HibernateException;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SchemaValidationStartupError}.
 */
public class SchemaValidationStartupErrorTest extends BaseTestCase {

    private static final String SCHEMA_VALIDATION_MESSAGE =
            "Schema-validation: missing column [institute_id] in table [account_requests]";

    @Test
    public void testIsSchemaValidationFailure_directSchemaManagementException_returnsTrue() {
        SchemaManagementException exception = new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE);

        assertTrue(SchemaValidationStartupError.isSchemaValidationFailure(exception));
    }

    @Test
    public void testIsSchemaValidationFailure_wrappedSchemaManagementException_returnsTrue() {
        RuntimeException exception = new RuntimeException("Failed to build session factory",
                new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE));

        assertTrue(SchemaValidationStartupError.isSchemaValidationFailure(exception));
    }

    @Test
    public void testIsSchemaValidationFailure_schemaValidationMessageInWrapper_returnsTrue() {
        RuntimeException exception = new RuntimeException(SCHEMA_VALIDATION_MESSAGE,
                new HibernateException("wrapped db error"));

        assertTrue(SchemaValidationStartupError.isSchemaValidationFailure(exception));
    }

    @Test
    public void testIsSchemaValidationFailure_nonSchemaValidationException_returnsFalse() {
        RuntimeException exception = new RuntimeException("Failed to connect to database");

        assertFalse(SchemaValidationStartupError.isSchemaValidationFailure(exception));
    }

    @Test
    public void testGetSchemaValidationMessage_wrappedSchemaManagementException_returnsExactSchemaMessage() {
        RuntimeException exception = new RuntimeException("Failed to build session factory",
                new SchemaManagementException("  " + SCHEMA_VALIDATION_MESSAGE + "  "));

        assertEquals(SCHEMA_VALIDATION_MESSAGE, SchemaValidationStartupError.getSchemaValidationMessage(exception));
    }

    @Test
    public void testBuildDevServerMessage_schemaValidationException_includesCommandAndExactError() {
        SchemaManagementException exception = new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE);

        String message = SchemaValidationStartupError.buildDevServerMessage(exception);

        assertTrue(message.contains(SCHEMA_VALIDATION_MESSAGE));
        assertTrue(message.contains("./gradlew liquibaseUpdate"));
    }

    @Test
    public void testHandleDevServerStartupFailure_schemaValidationException_printsFriendlyStacklessMessage()
            throws Exception {
        SchemaManagementException exception = new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE);

        DevServerStartupException startupException = SchemaValidationStartupError.handleDevServerStartupFailure(exception);

        String expectedMessage = SchemaValidationStartupError.buildDevServerMessage(exception);
        assertEquals(expectedMessage, startupException.getMessage());
        assertEquals(0, startupException.getStackTrace().length);
        assertNull(startupException.getCause());
    }

}
