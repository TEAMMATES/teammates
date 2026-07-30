package teammates.ui.loginmethodhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.client.http.GenericUrl;

import teammates.common.datatransfer.Provider;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.core.MagicLinksLogic;
import teammates.storage.entity.MagicLink;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link EmailLoginHandler}.
 */
public class EmailLoginHandlerTest extends BaseTestCase {

    private static final String LOGIN_URL = "http://localhost:8080/login";
    private static final String OAUTH_CALLBACK_URL = "http://localhost:8080/oauth2callback";

    private EmailLoginHandler emailLoginHandler;
    private MagicLinksLogic magicLinksLogic;

    @BeforeMethod
    public void setUpMethod() {
        magicLinksLogic = mock(MagicLinksLogic.class);
        emailLoginHandler = new EmailLoginHandler(magicLinksLogic);
    }

    @Test
    public void handleLogin_validRequest_returnsRedirectUrlWithValidState() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, LOGIN_URL);

        String loginUrl = emailLoginHandler.handleLogin(req, "/web/instructor/home");

        GenericUrl url = new GenericUrl(loginUrl);
        assertEquals("/web/email-login", url.getRawPath());
        String encryptedState = getQueryParam(url, Const.ParamsNames.AUTH_STATE);
        AuthState state = JsonUtils.fromJson(StringHelper.decrypt(encryptedState), AuthState.class);
        assertEquals("/web/instructor/home", state.nextUrl());
        assertEquals("1234", state.sessionId());
        assertEquals(LoginMethod.EMAIL, state.loginMethod());
    }

    @Test
    public void handleCallback_validToken_returnsValidAuthResult() throws Exception {
        String token = "raw-token";
        MagicLink magicLink = new MagicLink("user@example.com", "token-hash", Instant.now());
        when(magicLinksLogic.consumeMagicLink(token)).thenReturn(magicLink);
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam(Const.ParamsNames.TOKEN, token);
        AuthState state = new AuthState("/", "1234", LoginMethod.EMAIL);

        AuthResult result = emailLoginHandler.handleCallback(req, state);

        assertEquals(Provider.EMAIL, result.provider());
        assertEquals("user@example.com", result.subject());
        assertEquals("user@example.com", result.email());
        verify(magicLinksLogic, times(1)).consumeMagicLink(token);
    }

    @Test
    public void handleCallback_missingToken_throwsInvalidAuthStateException() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        AuthState state = new AuthState("/", "1234", LoginMethod.EMAIL);

        assertThrows(AuthException.class, () -> emailLoginHandler.handleCallback(req, state));
        verify(magicLinksLogic, never()).consumeMagicLink("raw-token");
    }

    @Test
    public void handleCallback_differentSessionId_throwsInvalidAuthStateException() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam(Const.ParamsNames.TOKEN, "raw-token");
        AuthState state = new AuthState("/", "different-session-id", LoginMethod.EMAIL);

        assertThrows(AuthException.class, () -> emailLoginHandler.handleCallback(req, state));
        verify(magicLinksLogic, never()).consumeMagicLink("raw-token");
    }

    @Test
    public void handleCallback_magicLinkDoesNotExist_throwsInvalidAuthStateException() throws Exception {
        String token = "raw-token";
        when(magicLinksLogic.consumeMagicLink(token))
                .thenThrow(new EntityDoesNotExistException("Magic link does not exist for the given token."));
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam(Const.ParamsNames.TOKEN, token);
        AuthState state = new AuthState("/", "1234", LoginMethod.EMAIL);

        assertThrows(AuthException.class, () -> emailLoginHandler.handleCallback(req, state));
    }

    @Test
    public void handleCallback_magicLinkIsExpired_throwsInvalidAuthStateException() throws Exception {
        String token = "raw-token";
        when(magicLinksLogic.consumeMagicLink(token))
                .thenThrow(new InvalidParametersException("Invalid or expired magic link."));
        MockHttpServletRequest req = new MockHttpServletRequest(HttpGet.METHOD_NAME, OAUTH_CALLBACK_URL);
        req.addParam(Const.ParamsNames.TOKEN, token);
        AuthState state = new AuthState("/", "1234", LoginMethod.EMAIL);

        assertThrows(AuthException.class, () -> emailLoginHandler.handleCallback(req, state));
    }

    private static String getQueryParam(GenericUrl url, String name) {
        Object value = url.get(name);
        if (value instanceof List<?>) {
            return (String) ((List<?>) value).get(0);
        }
        return (String) value;
    }
}
