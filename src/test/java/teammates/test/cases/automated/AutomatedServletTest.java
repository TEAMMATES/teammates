package teammates.test.cases.automated;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.cases.BaseTestCaseWithObjectifyAccess;
import teammates.test.driver.MockHttpServletRequest;
import teammates.test.driver.MockHttpServletResponse;
import teammates.ui.automated.CronJobServlet;

/**
 * SUT: {@link teammates.ui.automated.AutomatedServlet}.
 *
 * <p>Uses {@link CronJobServlet} as the concrete class to test.
 */
public class AutomatedServletTest extends BaseTestCaseWithObjectifyAccess {

    private static final CronJobServlet SERVLET = new CronJobServlet();

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    private void setupMocks(String requestUrl) {
        mockRequest = new MockHttpServletRequest(HttpGet.METHOD_NAME, requestUrl);
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void allTests() {

        ______TS("Typical case: valid action mapping");

        setupMocks(Const.ActionURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatusCode());

        ______TS("Failure case: invalid action mapping");

        setupMocks("/auto/mappingDoesNotExist");

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatusCode());

    }

}
