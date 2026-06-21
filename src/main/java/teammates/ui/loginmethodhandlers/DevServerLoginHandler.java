package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.UrlHelper;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * Login handler for dev server login
 * This is intended for local development and testing purposes only.
 */
public class DevServerLoginHandler implements LoginMethodHandler {

    private static final Logger log = Logger.getLogger();

    @Override
    public String handleLogin(HttpServletRequest req, String nextUrl) throws IOException, AuthException {
        if (!Config.isDevServerLoginEnabled()) {
            throw new AuthException("Dev server login is not enabled");
        }

        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.DEV_SERVER);
        String encryptedState = StringHelper.encrypt(JsonUtils.toCompactJson(state));
        String redirectUrl = String.format("/devServerLogin?state=%s",
                UrlHelper.encodeQueryParam(encryptedState));

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to dev server login page");

        return redirectUrl;
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, AuthState state) throws IOException, AuthException {
        if (!Config.isDevServerLoginEnabled()) {
            throw new AuthException("Dev server login is not enabled");
        }

        String email = req.getParameter("email");
        if (email == null) {
            throw new AuthException("Missing email parameter in dev server login callback");
        }

        String sessionId = state.sessionId();
        if (!sessionId.equals(req.getSession().getId())) {
            String message = String.format("Different session ID: expected %s, got %s",
                    sessionId, req.getSession().getId());
            throw new AuthException(message);
        }

        return new AuthResult(Provider.TEAMMATES_DEV, email, null, email);
    }

}
