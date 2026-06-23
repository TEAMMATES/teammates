package teammates.ui.loginmethodhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link GoogleLoginHandler}.
 */
public class GoogleLoginHandlerTest extends BaseTestCase {

    private static final String LOGIN_URL = "http://localhost:8080/login";
    private static final String OAUTH_CALLBACK_URL = "http://localhost:8080/oauth2callback";

    private GoogleLoginHandler googleLoginHandler;

    @BeforeMethod
    public void setUpMethod() {
        googleLoginHandler = new GoogleLoginHandler();
    }

    @Test
    public void handleLogin_validRequest_returnsValidRedirectUrl() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = googleLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals("https", url.getScheme());
        assertEquals("accounts.google.com", url.getHost());
        assertEquals("/o/oauth2/auth", url.getRawPath());
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidClientId() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = googleLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals(Config.OIDC_GOOGLE_CLIENT_ID, getQueryParam(url, "client_id"));
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidRedirectUri() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = googleLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals(OAUTH_CALLBACK_URL, getQueryParam(url, "redirect_uri"));
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidResponseType() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = googleLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals("code", getQueryParam(url, "response_type"));
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidScope() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = googleLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        String scope = getQueryParam(url, "scope");
        assertTrue(scope.contains("openid"));
        assertTrue(scope.contains("email"));
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidState() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = googleLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        String encryptedState = getQueryParam(url, "state");
        AuthState state = JsonUtils.fromJson(StringHelper.decrypt(encryptedState), AuthState.class);
        assertEquals("/web/instructor/home", state.nextUrl());
        assertEquals("1234", state.sessionId());
        assertEquals(LoginMethod.GOOGLE, state.loginMethod());
    }

    @Test
    public void handleCallback_validResponse_returnsValidAuthResult() throws Exception {
        GoogleLoginHandler loginHandler = spy(new GoogleLoginHandler());
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse().setIdToken("id-token");
        doReturn(tokenResponse).when(loginHandler)
                .requestToken(eq("authorization-code"), eq(OAUTH_CALLBACK_URL));
        doReturn(createGoogleIdToken()).when(loginHandler).verifyIdToken(eq("id-token"));
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.setQueryString("code=authorization-code");
        AuthState state = new AuthState("/", "1234", LoginMethod.GOOGLE);

        AuthResult result = loginHandler.handleCallback(req, state);

        assertEquals(Provider.GOOGLE, result.provider());
        assertEquals("google-subject", result.subject());
        assertEquals("user@example.com", result.email());
    }

    @Test
    public void handleCallback_errorResponse_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.setQueryString("error=access_denied");
        AuthState state = new AuthState("/", "1234", LoginMethod.GOOGLE);

        assertThrows(AuthException.class,
                () -> googleLoginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_invalidCallbackUrl_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.GOOGLE);

        assertThrows(AuthException.class,
                () -> googleLoginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_differentSessionId_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.setQueryString("code=authorization-code");
        AuthState state = new AuthState("/", "different-session-id", LoginMethod.GOOGLE);

        assertThrows(AuthException.class,
                () -> googleLoginHandler.handleCallback(req, state));
    }

    private static String getQueryParam(GenericUrl url, String name) {
        Object value = url.get(name);
        if (value instanceof List<?>) {
            return (String) ((List<?>) value).get(0);
        }
        return (String) value;
    }

    private static GoogleIdToken createGoogleIdToken() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setSubject("google-subject");
        payload.setEmail("user@example.com");
        return new GoogleIdToken(new Header(), payload, new byte[0], new byte[0]);
    }
}
