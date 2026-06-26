package teammates.ui.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
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
import teammates.common.datatransfer.UserInfoCookie;
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
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link LoginServlet}.
 */
public class LoginServletTest extends BaseTestCase {

    private static final String LOGIN_URL = "/login";

    private AccountsLogic accountsLogic;
    private LoginMethodHandler loginHandler;
    private LoginServlet servlet;
    private MockHttpServletRequest req;
    private MockHttpServletResponse resp;

    @BeforeMethod
    public void setUpMethod() {
        accountsLogic = mock(AccountsLogic.class);
        loginHandler = mock(LoginMethodHandler.class);
        servlet = spy(new LoginServlet(accountsLogic));
        doReturn(loginHandler).when(servlet).getLoginHandler(any(LoginMethod.class));
        req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);
        resp = new MockHttpServletResponse();
    }

    @Test
    public void doGet_noAuthCookie_redirectsToLoginHandlerUrl() throws Exception {
        req.addParam("loginMethod", LoginMethod.GOOGLE.getMethod());
        when(loginHandler.handleLogin(any(), eq("/"))).thenReturn("/oauthLogin");

        servlet.doGet(req, resp);

        assertEquals("/oauthLogin", resp.getRedirectUrl());
    }

    @Test
    public void doGet_loginHandlerThrows_returnsInternalServerError() throws Exception {
        req.addParam("loginMethod", LoginMethod.GOOGLE.getMethod());
        when(loginHandler.handleLogin(any(), eq("/")))
                .thenThrow(new AuthException("Login failed"));

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void doGet_validCookieForExistingAccount_redirectsToNextUrl() throws Exception {
        Account account = new Account(Provider.GOOGLE, "google-subject", Account.NO_TENANT, "user@example.com");
        req.addCookie(getAuthCookie(account.getId()));
        req.addParam("nextUrl", "/web/instructor/home");
        when(accountsLogic.getAccount(any())).thenReturn(account);

        try (MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            mockHibernateUtil.when(HibernateUtil::beginTransaction).thenAnswer(invocation -> null);
            mockHibernateUtil.when(HibernateUtil::commitTransaction).thenAnswer(invocation -> null);

            servlet.doGet(req, resp);
        }

        assertEquals("/web/instructor/home", resp.getRedirectUrl());
    }

    @Test
    public void doGet_validCookieForMissingAccount_redirectsToLoginHandlerUrl() throws Exception {
        req.addCookie(getAuthCookie(UUID.randomUUID()));
        req.addParam("loginMethod", LoginMethod.GOOGLE.getMethod());
        when(accountsLogic.getAccount(any())).thenReturn(null);
        when(loginHandler.handleLogin(any(), eq("/"))).thenReturn("/oauthLogin");

        try (MockedStatic<Config> ignored = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
                MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            mockHibernateUtil.when(HibernateUtil::beginTransaction).thenAnswer(invocation -> null);
            mockHibernateUtil.when(HibernateUtil::commitTransaction).thenAnswer(invocation -> null);

            servlet.doGet(req, resp);
        }

        assertEquals("/oauthLogin", resp.getRedirectUrl());
    }

    @Test
    public void doGet_invalidCookie_redirectsToLoginHandlerUrl() throws Exception {
        req.addCookie(new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME, "not-an-auth-cookie"));
        req.addParam("loginMethod", LoginMethod.GOOGLE.getMethod());
        when(loginHandler.handleLogin(any(), eq("/"))).thenReturn("/oauthLogin");

        servlet.doGet(req, resp);

        assertEquals("/oauthLogin", resp.getRedirectUrl());
    }

    @Test
    public void doGet_invalidLoginMethod_returnsBadRequest() throws Exception {
        req.addParam("loginMethod", "invalid-method");

        servlet.doGet(req, resp);

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void doGet_unsupportedLoginMethod_returnsBadRequest() throws Exception {
        req.addParam("loginMethod", LoginMethod.GOOGLE.getMethod());

        try (MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            mockConfig.when(() -> Config.isSupportedLoginMethod(LoginMethod.GOOGLE)).thenReturn(false);

            servlet.doGet(req, resp);
        }

        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    private static Cookie getAuthCookie(UUID accountId) {
        UserInfoCookie uic = new UserInfoCookie(accountId);
        String cookieValue = StringHelper.encrypt(JsonUtils.toCompactJson(uic));
        return new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME, cookieValue);
    }

}
