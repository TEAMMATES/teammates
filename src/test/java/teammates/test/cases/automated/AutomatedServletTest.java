package teammates.test.cases.automated;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.InvalidHttpParameterException;
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

        setupMocks(Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("\"Successful\" case: invalid action mapping");

        setupMocks("/auto/mappingDoesNotExist");

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("\"Successful\" case: NullHttpParameterException");

        setupMocks(Const.CronJobURIs.AUTOMATED_EXCEPTION_TEST);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("\"Successful\" case: InvalidHttpParameterException");

        setupMocks(Const.CronJobURIs.AUTOMATED_EXCEPTION_TEST);
        mockRequest.addParam(Const.ParamsNames.ERROR, InvalidHttpParameterException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("Failure case: DeadlineExceededException");

        setupMocks(Const.CronJobURIs.AUTOMATED_EXCEPTION_TEST);
        mockRequest.addParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, mockResponse.getStatus());

        ______TS("Failure case: DatastoreTimeoutException");

        setupMocks(Const.CronJobURIs.AUTOMATED_EXCEPTION_TEST);
        mockRequest.addParam(Const.ParamsNames.ERROR, DatastoreTimeoutException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, mockResponse.getStatus());

        ______TS("Failure case: NullPointerException");

        setupMocks(Const.CronJobURIs.AUTOMATED_EXCEPTION_TEST);
        mockRequest.addParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

        ______TS("Failure case: AssertionError");

        setupMocks(Const.CronJobURIs.AUTOMATED_EXCEPTION_TEST);
        mockRequest.addParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

    }

}
