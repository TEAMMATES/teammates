package teammates.ui.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Account;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;
import teammates.ui.exception.AuthException;
import teammates.ui.loginmethodhandlers.AuthResult;
import teammates.ui.loginmethodhandlers.AuthState;
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link OAuth2CallbackServlet}.
 */
public class OAuth2CallbackServletTest extends BaseTestCase {

    private static final String OAUTH_CALLBACK_URL = "/oauth2callback";
    private AccountsLogic accountsLogic;
    private OAuth2CallbackServlet servlet;
    private MockHttpServletRequest req;
    private MockHttpServletResponse resp;

    @BeforeMethod
    public void setUpMethod() {
        accountsLogic = mock(AccountsLogic.class);
        servlet = spy(new OAuth2CallbackServlet(accountsLogic));
        req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        resp = new MockHttpServletResponse();
    }

    @Test
    public void doGet_missingState_returnsBadRequest() throws Exception {
        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_missingState_invalidatesLoginCookie() throws Exception {
        servlet.doGet(req, resp);

        assertLoginCookieInvalidated(resp);
    }

    @Test
    public void doGet_invalidState_returnsBadRequest() throws Exception {
        req.addParam("state", "not-an-encrypted-state");

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_invalidState_invalidatesLoginCookie() throws Exception {
        req.addParam("state", "not-an-encrypted-state");

        servlet.doGet(req, resp);

        assertLoginCookieInvalidated(resp);
    }

    @Test
    public void doGet_nullAuthState_returnsBadRequest() throws Exception {
        req.addParam("state", getEncryptedState("null"));

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_missingSessionId_returnsBadRequest() throws Exception {
        req.addParam("state", getEncryptedState("""
                {"nextUrl":"/","loginMethod":"GOOGLE"}
                """));

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_missingLoginMethod_returnsBadRequest() throws Exception {
        req.addParam("state", getEncryptedState("""
                {"nextUrl":"/","sessionId":"1234"}
                """));

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_invalidLoginMethod_returnsBadRequest() throws Exception {
        req.addParam("state", getEncryptedState("""
                {"nextUrl":"/","sessionId":"1234","loginMethod":"INVALID"}
                """));

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_unsupportedLoginMethod_returnsBadRequest() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE));

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, false)) {
            servlet.doGet(req, resp);
        }

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_unsupportedLoginMethod_invalidatesLoginCookie() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE));

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, false)) {
            servlet.doGet(req, resp);
        }

        assertLoginCookieInvalidated(resp);
    }

    @Test
    public void doGet_callbackHandlerThrows_returnsInternalServerError() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE));
        LoginMethodHandler loginHandler = mockFailingLoginHandler();
        doReturn(loginHandler).when(servlet).getLoginHandler(LoginMethod.GOOGLE);

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true)) {
            servlet.doGet(req, resp);
        }

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void doGet_callbackHandlerThrows_invalidatesLoginCookie() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE));
        LoginMethodHandler loginHandler = mockFailingLoginHandler();
        doReturn(loginHandler).when(servlet).getLoginHandler(LoginMethod.GOOGLE);

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true)) {
            servlet.doGet(req, resp);
        }

        assertLoginCookieInvalidated(resp);
    }

    @Test
    public void doGet_callbackHandlerReturnsAuthResult_redirectsToNextUrl() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE, "/web/instructor/home"));
        LoginMethodHandler loginHandler = mockSuccessfulLoginHandler();
        mockSuccessfulAccountsLogic(accountsLogic);

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true);
                MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            mockHibernateUtil.when(HibernateUtil::beginTransaction).thenAnswer(invocation -> null);
            mockHibernateUtil.when(HibernateUtil::commitTransaction).thenAnswer(invocation -> null);
            doReturn(loginHandler).when(servlet).getLoginHandler(LoginMethod.GOOGLE);

            servlet.doGet(req, resp);
        }

        assertEquals("/web/instructor/home", resp.getRedirectUrl());
    }

    private static MockedStatic<Config> mockSupportedLoginMethod(LoginMethod loginMethod, boolean isSupported) {
        MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        mockConfig.when(() -> Config.isSupportedLoginMethod(loginMethod)).thenReturn(isSupported);
        return mockConfig;
    }

    private static String getEncryptedState(LoginMethod loginMethod) {
        return getEncryptedState(loginMethod, "/");
    }

    private static String getEncryptedState(LoginMethod loginMethod, String nextUrl) {
        AuthState state = new AuthState(nextUrl, "1234", loginMethod);
        return StringHelper.encrypt(JsonUtils.toCompactJson(state));
    }

    private static String getEncryptedState(String state) {
        return StringHelper.encrypt(state);
    }

    private static LoginMethodHandler mockFailingLoginHandler() throws Exception {
        LoginMethodHandler loginHandler = mock(LoginMethodHandler.class);
        when(loginHandler.handleCallback(any(), any()))
                .thenThrow(new AuthException("Callback failed"));
        return loginHandler;
    }

    private static LoginMethodHandler mockSuccessfulLoginHandler() throws Exception {
        LoginMethodHandler loginHandler = mock(LoginMethodHandler.class);
        when(loginHandler.handleCallback(any(), any())).thenReturn(
                new AuthResult(Provider.GOOGLE, "google-subject", null, "user@example.com"));
        return loginHandler;
    }

    private static void mockSuccessfulAccountsLogic(AccountsLogic accountsLogic) {
        Account account = new Account(Provider.GOOGLE, "google-subject", Account.NO_TENANT, "user@example.com");
        when(accountsLogic.createOrGetAccount(Provider.GOOGLE, "google-subject", null, "user@example.com"))
                .thenReturn(account);
    }

    private static void assertLoginCookieInvalidated(MockHttpServletResponse resp) {
        Cookie cookie = resp.getCookies().stream()
                .filter(c -> Const.SecurityConfig.AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(cookie);
        assertEquals("", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(0, cookie.getMaxAge());
    }

}
