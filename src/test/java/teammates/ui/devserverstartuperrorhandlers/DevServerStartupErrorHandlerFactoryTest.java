package teammates.ui.devserverstartuperrorhandlers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.ui.errorhandlers.DevServerStartupErrorHandlerFactory;
import teammates.ui.errorhandlers.SchemaValidationStartupErrorHandler;
import teammates.ui.errorhandlers.StartupErrorHandler;

/**
 * SUT: {@link DevServerStartupErrorHandlerFactory}.
 */
public class DevServerStartupErrorHandlerFactoryTest extends BaseTestCase {

    @Test
    public void testGetHandler_schemaValidationException_returnsSchemaValidationHandler() {
        SchemaManagementException exception = new SchemaManagementException(
                "Schema-validation: missing column [institute_id] in table [account_requests]");

        StartupErrorHandler startupErrorHandler = DevServerStartupErrorHandlerFactory.getHandler(exception)
                .orElseThrow();

        assertTrue(startupErrorHandler instanceof SchemaValidationStartupErrorHandler);
    }

    @Test
    public void testGetHandler_unknownException_returnsEmpty() {
        RuntimeException exception = new RuntimeException("Failed to connect to database");

        assertTrue(DevServerStartupErrorHandlerFactory.getHandler(exception).isEmpty());
    }

}
