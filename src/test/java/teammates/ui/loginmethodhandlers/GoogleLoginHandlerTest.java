package teammates.ui.loginmethodhandlers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link GoogleLoginHandler}.
 */
public class GoogleLoginHandlerTest extends BaseTestCase {

    private static final String OAUTH_CALLBACK_URL = "http://localhost:8080/oauth2callback";

    private GoogleLoginHandler googleLoginHandler;

    @BeforeMethod
    public void setUpMethod() {
        googleLoginHandler = new GoogleLoginHandler();
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
}
