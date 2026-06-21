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
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link DevServerLoginHandler}.
 */
public class DevServerLoginHandlerTest extends BaseTestCase {

    private static final String OAUTH_CALLBACK_URL = "http://localhost:8080/oauth2callback";

    private DevServerLoginHandler devServerLoginHandler;

    @BeforeMethod
    public void setUpMethod() {
        devServerLoginHandler = new DevServerLoginHandler();
    }

    @Test
    public void handleLogin_loginDisabled_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, "/login");

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(false)) {
            assertThrows(AuthException.class,
                    () -> devServerLoginHandler.handleLogin(req, "/"));
        }
    }

    @Test
    public void handleCallback_loginDisabled_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(false)) {
            assertThrows(AuthException.class,
                    () -> devServerLoginHandler.handleCallback(req, state));
        }
    }

    @Test
    public void handleCallback_missingEmail_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            assertThrows(AuthException.class,
                    () -> devServerLoginHandler.handleCallback(req, state));
        }
    }

    @Test
    public void handleCallback_differentSessionId_throwsInvalidAuthStateException() {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam("email", "student@example.com");
        AuthState state = new AuthState("/", "different-session-id", LoginMethod.DEV_SERVER);

        try (MockedStatic<Config> ignored = mockDevServerLoginEnabled(true)) {
            assertThrows(AuthException.class,
                    () -> devServerLoginHandler.handleCallback(req, state));
        }
    }

    @Test
    public void handleCallback_validCallback_returnsAuthResult() throws Exception {
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
