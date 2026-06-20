package teammates.ui.loginmethodhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import org.apache.http.client.methods.HttpGet;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.InvalidAuthStateException;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link DevServerLoginHandler} and {@link GoogleLoginHandler}.
 */
public class LoginMethodHandlersTest extends BaseTestCase {

    private static final String OAUTH_CALLBACK_URL = "http://localhost:8080/oauth2callback";
    private DevServerLoginHandler devServerLoginHandler;
    private GoogleLoginHandler googleLoginHandler;

    @BeforeMethod
    public void setUpMethod() {
        devServerLoginHandler = new DevServerLoginHandler();
        googleLoginHandler = new GoogleLoginHandler();
    }

    @Test
    public void devServerHandleLogin_loginDisabled_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, "/login");

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(false)) {
            assertThrows(InvalidAuthStateException.class,
                    () -> devServerLoginHandler.handleLogin(req, "/"));
        }
    }

    @Test
    public void devServerHandleCallback_loginDisabled_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(false)) {
            assertThrows(InvalidAuthStateException.class,
                    () -> devServerLoginHandler.handleCallback(req, state));
        }
    }

    @Test
    public void devServerHandleCallback_missingEmail_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            assertThrows(InvalidAuthStateException.class,
                    () -> devServerLoginHandler.handleCallback(req, state));
        }
    }

    @Test
    public void devServerHandleCallback_differentSessionId_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("email", "student@example.com");
        AuthState state = new AuthState("/", "different-session-id", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            assertThrows(InvalidAuthStateException.class,
                    () -> devServerLoginHandler.handleCallback(req, state));
        }
    }

    @Test
    public void googleHandleCallback_errorResponse_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.setQueryString("error=access_denied");
        AuthState state = new AuthState("/", "1234", LoginMethod.GOOGLE);

        assertThrows(InvalidAuthStateException.class,
                () -> googleLoginHandler.handleCallback(req, state));
    }

    @Test
    public void googleHandleCallback_invalidCallbackUrl_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.GOOGLE);

        assertThrows(InvalidAuthStateException.class,
                () -> googleLoginHandler.handleCallback(req, state));
    }

    @Test
    public void googleHandleCallback_differentSessionId_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.setQueryString("code=authorization-code");
        AuthState state = new AuthState("/", "different-session-id", LoginMethod.GOOGLE);

        assertThrows(InvalidAuthStateException.class,
                () -> googleLoginHandler.handleCallback(req, state));
    }

    @Test
    public void devServerHandleCallback_validCallback_returnsAuthResult() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("email", "student@example.com");
        AuthState state = new AuthState("/", "1234", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            AuthResult authResult = devServerLoginHandler.handleCallback(req, state);

            assertEquals(new AuthResult(Provider.TEAMMATES_DEV,
                    "student@example.com", null, "student@example.com"), authResult);
        }
    }

    private static MockedStatic<Config> mockDevServerLoginEnabled(boolean isEnabled) {
        MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        mockConfig.when(Config::isDevServerLoginEnabled).thenReturn(isEnabled);
        return mockConfig;
    }
}
