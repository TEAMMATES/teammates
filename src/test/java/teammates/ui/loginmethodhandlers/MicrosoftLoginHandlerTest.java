package teammates.ui.loginmethodhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.client.http.GenericUrl;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link MicrosoftLoginHandler}.
 */
public class MicrosoftLoginHandlerTest extends BaseTestCase {

    private static final String LOGIN_URL = "http://localhost:8080/login";
    private static final String OAUTH_CALLBACK_URL = "http://localhost:8080/oauth2callback";

    private MicrosoftLoginHandler microsoftLoginHandler;

    @BeforeMethod
    public void setUpMethod() {
        microsoftLoginHandler = spy(new MicrosoftLoginHandler());
    }

    @Test
    public void handleLogin_validRequest_returnsValidRedirectUrl() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);
        doReturn(createAuthorizationRequestUrl("state")).when(microsoftLoginHandler)
                .getAuthorizationRequestUrl(eq(OAUTH_CALLBACK_URL), anyString());

        String loginUrl = microsoftLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals("https", url.getScheme());
        assertEquals("login.microsoftonline.com", url.getHost());
        assertEquals("/" + Config.OIDC_MICROSOFT_TENANT_ID + "/oauth2/v2.0/authorize", url.getRawPath());
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidClientId() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);
        doReturn(createAuthorizationRequestUrl("state")).when(microsoftLoginHandler)
                .getAuthorizationRequestUrl(eq(OAUTH_CALLBACK_URL), anyString());

        String loginUrl = microsoftLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals(Config.OIDC_MICROSOFT_CLIENT_ID, getQueryParam(url, "client_id"));
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidState() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);
        doAnswer(invocation -> createAuthorizationRequestUrl(invocation.getArgument(1))).when(microsoftLoginHandler)
                .getAuthorizationRequestUrl(eq(OAUTH_CALLBACK_URL), anyString());

        String loginUrl = microsoftLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        String encryptedState = getQueryParam(url, "state");
        AuthState state = JsonUtils.fromJson(StringHelper.decrypt(encryptedState), AuthState.class);
        assertEquals("/web/instructor/home", state.nextUrl());
        assertEquals("1234", state.sessionId());
        assertEquals(LoginMethod.MICROSOFT, state.loginMethod());
    }

    @Test
    public void handleCallback_validResponse_returnsValidAuthResult() throws Exception {
        MicrosoftLoginHandler loginHandler = spy(new MicrosoftLoginHandler("actual-tenant-id"));
        IAuthenticationResult tokenResponse = mock(IAuthenticationResult.class);
        doReturn(createIdToken()).when(tokenResponse).idToken();
        doReturn(tokenResponse).when(loginHandler).requestToken(eq("authorization-code"), eq(OAUTH_CALLBACK_URL));
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("code", "authorization-code");
        AuthState state = new AuthState("/", "1234", LoginMethod.MICROSOFT);

        AuthResult result = loginHandler.handleCallback(req, state);

        assertEquals(Provider.MICROSOFT, result.provider());
        assertEquals("microsoft-subject", result.subject());
        assertEquals("actual-tenant-id", result.tenantId());
        assertEquals("user@example.com", result.email());
    }

    @Test
    public void handleCallback_missingEmailClaim_throwsAuthException() throws Exception {
        MicrosoftLoginHandler loginHandler = spy(new MicrosoftLoginHandler("actual-tenant-id"));
        IAuthenticationResult tokenResponse = mock(IAuthenticationResult.class);
        doReturn(createIdToken(Map.of(
                "sub", "microsoft-subject",
                "tid", "actual-tenant-id"))).when(tokenResponse).idToken();
        doReturn(tokenResponse).when(loginHandler).requestToken(eq("authorization-code"), eq(OAUTH_CALLBACK_URL));
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("code", "authorization-code");
        AuthState state = new AuthState("/", "1234", LoginMethod.MICROSOFT);

        assertThrows(AuthException.class,
                () -> loginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_errorResponse_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("error", "access_denied");
        AuthState state = new AuthState("/", "1234", LoginMethod.MICROSOFT);

        assertThrows(AuthException.class,
                () -> microsoftLoginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_missingCode_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.MICROSOFT);

        assertThrows(AuthException.class,
                () -> microsoftLoginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_differentSessionId_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("code", "authorization-code");
        AuthState state = new AuthState("/", "different-session-id", LoginMethod.MICROSOFT);

        assertThrows(AuthException.class,
                () -> microsoftLoginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_invalidTenantId_throwsInvalidAuthStateException() throws Exception {
        MicrosoftLoginHandler loginHandler = spy(new MicrosoftLoginHandler("actual-tenant-id"));
        IAuthenticationResult tokenResponse = mock(IAuthenticationResult.class);
        doReturn(createIdToken("invalid-tenant-id")).when(tokenResponse).idToken();
        doReturn(tokenResponse).when(loginHandler).requestToken(eq("authorization-code"), eq(OAUTH_CALLBACK_URL));
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("code", "authorization-code");
        AuthState state = new AuthState("/", "1234", LoginMethod.MICROSOFT);

        assertThrows(AuthException.class,
                () -> loginHandler.handleCallback(req, state));
    }

    private static String getQueryParam(GenericUrl url, String name) {
        Object value = url.get(name);
        if (value instanceof List<?>) {
            return (String) ((List<?>) value).get(0);
        }
        return (String) value;
    }

    private static String createAuthorizationRequestUrl(String state) {
        GenericUrl url = new GenericUrl("https://login.microsoftonline.com/"
                + Config.OIDC_MICROSOFT_TENANT_ID + "/oauth2/v2.0/authorize");
        url.set("client_id", Config.OIDC_MICROSOFT_CLIENT_ID);
        url.set("state", state);
        return url.build();
    }

    private static String createIdToken() {
        return createIdToken("actual-tenant-id");
    }

    private static String createIdToken(String tenantId) {
        return createIdToken(Map.of(
                "sub", "microsoft-subject",
                "tid", tenantId,
                "email", "user@example.com"));
    }

    private static String createIdToken(Map<String, String> claimsMap) {
        String claims = JsonUtils.toCompactJson(claimsMap);
        String encodedClaims = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(claims.getBytes(StandardCharsets.UTF_8));
        return "header." + encodedClaims + ".signature";
    }
}
