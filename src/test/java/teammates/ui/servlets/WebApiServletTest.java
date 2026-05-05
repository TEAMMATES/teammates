package teammates.ui.servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.HibernateUtil;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;
import teammates.ui.exception.ActionMappingException;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link WebApiServlet}.
 */
public class WebApiServletTest extends BaseTestCase {

    private static final WebApiServlet SERVLET = new WebApiServlet();

    private static MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeClass
    public static void classSetup() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterClass
    public static void classTeardown() {
        mockHibernateUtil.close();
    }

    @Test
    public void testSuccessfulRequest_returnsOk() throws Exception {
        Action mockAction = mock(Action.class);
        doNothing().when(mockAction).init(any(HttpServletRequest.class));
        doNothing().when(mockAction).checkAccessControl();
        when(mockAction.execute()).thenReturn(new JsonResult("Test output"));
        when(mockAction.hasDefinedRequestBody()).thenReturn(false);
        when(mockAction.getUserInfoForLogging()).thenReturn(new RequestLogUser());

        try (MockedStatic<ActionFactory> actionFactory = mockStatic(ActionFactory.class)) {
            actionFactory.when(() -> ActionFactory.getAction(any(HttpServletRequest.class), anyString()))
                    .thenReturn(mockAction);

            MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, "/webapi/course");
            MockHttpServletResponse resp = new MockHttpServletResponse();

            SERVLET.doGet(req, resp);
            assertEquals(HttpStatus.SC_OK, resp.getStatus());
        }
    }

    @Test
    public void testActionMappingNotFound_returns404() throws Exception {
        try (MockedStatic<ActionFactory> actionFactory = mockStatic(ActionFactory.class)) {
            actionFactory.when(() -> ActionFactory.getAction(any(HttpServletRequest.class), anyString()))
                    .thenThrow(new ActionMappingException("not found", HttpStatus.SC_NOT_FOUND));

            MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, "/webapi/nonexistent");
            MockHttpServletResponse resp = new MockHttpServletResponse();

            SERVLET.doGet(req, resp);
            assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
        }
    }

    @Test
    public void testActionMappingMethodNotAllowed_returns405() throws Exception {
        try (MockedStatic<ActionFactory> actionFactory = mockStatic(ActionFactory.class)) {
            actionFactory.when(() -> ActionFactory.getAction(any(HttpServletRequest.class), anyString()))
                    .thenThrow(new ActionMappingException("method not allowed", HttpStatus.SC_METHOD_NOT_ALLOWED));

            MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, "/webapi/course");
            MockHttpServletResponse resp = new MockHttpServletResponse();

            SERVLET.doGet(req, resp);
            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, resp.getStatus());
        }
    }

}
