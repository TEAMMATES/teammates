package teammates.ui.servlets;

import static org.mockito.Mockito.mockStatic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.cloud.datastore.DatastoreException;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.UnauthorizedAccessException;

/**
 * SUT: {@link WebApiServlet}.
 */
public class WebApiServletTest extends BaseTestCase {

    private static final WebApiServlet SERVLET = new WebApiServlet();

    private static MockedStatic<HibernateUtil> mockHibernateUtil;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @BeforeClass
    public static void classSetup() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterClass
    public static void classTeardown() {
        mockHibernateUtil.close();
    }

    private void setupMocks(String method, String requestUrl) {
        mockRequest = new MockHttpServletRequest(method, requestUrl);
        mockResponse = new MockHttpServletResponse();
    }

    private void setupMocksFromGaeQueue(String method, String requestUrl) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-AppEngine-QueueName", Collections.singletonList("queuename"));
        mockRequest = new MockHttpServletRequest(method, requestUrl, headers);
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void testUserInvokedRequests() throws Exception {

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

        ______TS("Failure case: DatastoreException");

        setupMocks(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, DatastoreException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

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

    @Test
    public void testGaeQueueInvokedRequests() throws Exception {

        ______TS("Typical case: valid action mapping");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, "NoException");

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("\"Successful\" case: invalid action mapping");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, "nonexistent");

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_ACCEPTED, mockResponse.getStatus());

        setupMocksFromGaeQueue(HttpPost.METHOD_NAME, Const.ResourceURIs.EXCEPTION);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_ACCEPTED, mockResponse.getStatus());

        ______TS("\"Successful\" case: NullHttpParameterException");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_ACCEPTED, mockResponse.getStatus());

        ______TS("\"Successful\" case: InvalidHttpParameterException");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, InvalidHttpParameterException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_ACCEPTED, mockResponse.getStatus());

        ______TS("Failure case: DatastoreTimeoutException");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, DatastoreException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

        ______TS("Failure case: UnauthorizedAccessException");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        ______TS("Failure case: EntityNotFoundException");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, EntityNotFoundException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_NOT_FOUND, mockResponse.getStatus());

        ______TS("Failure case: NullPointerException");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

        ______TS("Failure case: AssertionError");

        setupMocksFromGaeQueue(HttpGet.METHOD_NAME, Const.ResourceURIs.EXCEPTION);
        mockRequest.addParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName());

        SERVLET.doGet(mockRequest, mockResponse);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, mockResponse.getStatus());

    }

}
