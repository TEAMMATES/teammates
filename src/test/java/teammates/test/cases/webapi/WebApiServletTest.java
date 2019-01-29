package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCaseWithObjectifyAccess;
import teammates.test.driver.MockHttpServletRequest;
import teammates.test.driver.MockHttpServletResponse;
import teammates.ui.webapi.action.WebApiServlet;

/**
 * SUT: {@link WebApiServlet}.
 */
public class WebApiServletTest extends BaseTestCaseWithObjectifyAccess {

    private static final WebApiServlet SERVLET = new WebApiServlet();

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    private void setupMocks(String method, String requestUrl) {
        mockRequest = new MockHttpServletRequest(method, Const.ResourceURIs.URI_PREFIX + requestUrl);
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void allTests() throws Exception {

        ______TS("Typical case: valid action mapping");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, "NoException");

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("Failure case: invalid action mapping");

        setupMocks(HttpGet.METHOD_NAME, "nonexistent");

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_NOT_FOUND, mockResponse.getStatus());

        setupMocks(HttpPost.METHOD_NAME, Const.ResourceURIs.EXCEPTION);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, mockResponse.getStatus());

        ______TS("Failure case: NullHttpParameterException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());

        ______TS("Failure case: InvalidHttpParameterException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, InvalidHttpParameterException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());

        ______TS("Failure case: DeadlineExceededException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, mockResponse.getStatus());

        ______TS("Failure case: DatastoreTimeoutException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, DatastoreTimeoutException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, mockResponse.getStatus());

        ______TS("Failure case: UnauthorizedAccessException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        ______TS("Failure case: EntityNotFoundException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, EntityNotFoundException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_NOT_FOUND, mockResponse.getStatus());

        ______TS("Failure case: NullPointerException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

        ______TS("Failure case: AssertionError");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

    }

}
