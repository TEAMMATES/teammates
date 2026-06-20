package teammates.ui.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.UUID;

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
import teammates.ui.exception.InvalidAuthStateException;
import teammates.ui.loginmethodhandlers.AuthResult;
import teammates.ui.loginmethodhandlers.AuthState;
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link OAuth2CallbackServlet}.
 */
public class OAuth2CallbackServletTest extends BaseTestCase {

    private static final String OAUTH_CALLBACK_URL = "/oauth2callback";
    private OAuth2CallbackServlet servlet;
    private MockHttpServletRequest req;
    private MockHttpServletResponse resp;

    @BeforeMethod
    public void setUpMethod() {
        servlet = new OAuth2CallbackServlet();
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
        servlet = new StubOAuth2CallbackServlet(mockFailingLoginHandler());

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true)) {
            servlet.doGet(req, resp);
        }

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void doGet_callbackHandlerThrows_invalidatesLoginCookie() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE));
        servlet = new StubOAuth2CallbackServlet(mockFailingLoginHandler());

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true)) {
            servlet.doGet(req, resp);
        }

        assertLoginCookieInvalidated(resp);
    }

    @Test
    public void doGet_callbackHandlerThrows_redirectsToDefaultUrl() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE));
        servlet = new StubOAuth2CallbackServlet(mockFailingLoginHandler());

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true)) {
            servlet.doGet(req, resp);
        }

        assertEquals("/", resp.getRedirectUrl());
    }

    @Test
    public void doGet_callbackHandlerReturnsAuthResult_redirectsToNextUrl() throws Exception {
        req.addParam("state", getEncryptedState(LoginMethod.GOOGLE, "/web/instructor/home"));
        LoginMethodHandler loginHandler = mockSuccessfulLoginHandler();
        AccountsLogic accountsLogic = mockSuccessfulAccountsLogic();

        try (MockedStatic<Config> ignored = mockSupportedLoginMethod(LoginMethod.GOOGLE, true);
                MockedStatic<AccountsLogic> mockAccountsLogic = mockStatic(AccountsLogic.class);
                MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            mockAccountsLogic.when(AccountsLogic::inst).thenReturn(accountsLogic);
            mockHibernateUtil.when(HibernateUtil::beginTransaction).thenAnswer(invocation -> null);
            mockHibernateUtil.when(HibernateUtil::commitTransaction).thenAnswer(invocation -> null);
            servlet = new StubOAuth2CallbackServlet(loginHandler);

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

    private static LoginMethodHandler mockFailingLoginHandler() throws Exception {
        LoginMethodHandler loginHandler = mock(LoginMethodHandler.class);
        when(loginHandler.handleCallback(any(), any()))
                .thenThrow(new InvalidAuthStateException("Callback failed"));
        return loginHandler;
    }

    private static LoginMethodHandler mockSuccessfulLoginHandler() throws Exception {
        LoginMethodHandler loginHandler = mock(LoginMethodHandler.class);
        when(loginHandler.handleCallback(any(), any())).thenReturn(
                new AuthResult(Provider.GOOGLE, "google-subject", null, "user@example.com"));
        return loginHandler;
    }

    private static AccountsLogic mockSuccessfulAccountsLogic() {
        Account account = new Account(Provider.GOOGLE, "google-subject", Account.NO_TENANT,
                "Test User", "user@example.com");
        account.setId(UUID.randomUUID());
        AccountsLogic accountsLogic = mock(AccountsLogic.class);
        when(accountsLogic.createOrGetAccount(Provider.GOOGLE, "google-subject", null, "user@example.com"))
                .thenReturn(account);
        return accountsLogic;
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

    private static class StubOAuth2CallbackServlet extends OAuth2CallbackServlet {

        private final LoginMethodHandler loginHandler;

        StubOAuth2CallbackServlet(LoginMethodHandler loginHandler) {
            this.loginHandler = loginHandler;
        }

        @Override
        LoginMethodHandler getLoginHandler(LoginMethod method) {
            return loginHandler;
        }
    }
}
