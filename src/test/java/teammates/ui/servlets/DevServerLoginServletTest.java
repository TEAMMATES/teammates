package teammates.ui.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link DevServerLoginServlet}.
 */
public class DevServerLoginServletTest extends BaseTestCase {

    private static final String DEV_SERVER_LOGIN_URL = "/devServerLogin";
    private DevServerLoginServlet servlet;

    @BeforeMethod
    public void setUpMethod() {
        servlet = new DevServerLoginServlet();
    }

    @Test
    public void doGet_devServerLoginDisabled_returnsForbidden() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, DEV_SERVER_LOGIN_URL);
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(false)) {
            servlet.doGet(req, resp);
        }

        assertEquals(HttpStatus.SC_FORBIDDEN, resp.getStatus());
    }

    @Test
    public void doGet_devServerLoginEnabled_returnsOk() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, DEV_SERVER_LOGIN_URL);
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            servlet.doGet(req, resp);
        }

        assertEquals(HttpStatus.SC_OK, resp.getStatus());
    }

    @Test
    public void doPost_devServerLoginDisabled_returnsForbidden() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpPost.METHOD_NAME, DEV_SERVER_LOGIN_URL);
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(false)) {
            servlet.doPost(req, resp);
        }

        assertEquals(HttpStatus.SC_FORBIDDEN, resp.getStatus());
    }

    @Test
    public void doPost_missingEmail_doesNotRedirect() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpPost.METHOD_NAME, DEV_SERVER_LOGIN_URL);
        req.addParam("state", "encrypted-state");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            servlet.doPost(req, resp);
        }

        assertNull(resp.getRedirectUrl());
    }

    @Test
    public void doPost_missingState_doesNotRedirect() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpPost.METHOD_NAME, DEV_SERVER_LOGIN_URL);
        req.addParam("email", "student@example.com");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            servlet.doPost(req, resp);
        }

        assertNull(resp.getRedirectUrl());
    }

    @Test
    public void doPost_requiredParams_redirectsToCallback() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpPost.METHOD_NAME, DEV_SERVER_LOGIN_URL);
        req.addParam("email", "student+test@example.com");
        req.addParam("state", "encrypted state");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            servlet.doPost(req, resp);
        }

        assertEquals("/oauth2callback?email=student%2Btest%40example.com&state=encrypted+state", resp.getRedirectUrl());
    }

    private static MockedStatic<Config> mockDevServerLoginEnabled(boolean isEnabled) {
        MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        mockConfig.when(Config::isDevServerLoginEnabled).thenReturn(isEnabled);
        return mockConfig;
    }
}
