package teammates.ui.devserverstartuperrorhandlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.ui.exception.DevServerStartupException;

/**
 * SUT: {@link DevServerStartupErrorHandler}.
 */
public class DevServerStartupErrorHandlerTest extends BaseTestCase {

    private static final String SCHEMA_VALIDATION_MESSAGE =
            "Schema-validation: missing column [institute_id] in table [account_requests]";

    @Test
    public void testThrowIfHandled_schemaValidationException_throwsDevServerStartupException() {
        SchemaManagementException exception = new SchemaManagementException(SCHEMA_VALIDATION_MESSAGE);
        SchemaValidationStartupErrorHandler handler = new SchemaValidationStartupErrorHandler();

        DevServerStartupException thrown = assertThrows(DevServerStartupException.class, () -> {
            DevServerStartupErrorHandler.throwIfHandled(exception);
        });

        assertEquals(handler.buildErrorMessage(exception), thrown.getMessage());
    }

    @Test
    public void testGetMessage_nonSchemaValidationException_returnsEmpty() {
        RuntimeException exception = new RuntimeException("Failed to connect to database");

        assertDoesNotThrow(() -> {
            DevServerStartupErrorHandler.throwIfHandled(exception);
        });
    }

}
