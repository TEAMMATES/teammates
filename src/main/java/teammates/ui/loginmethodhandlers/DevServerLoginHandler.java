package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.HttpResponseHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.UrlHelper;
import teammates.ui.output.LoginMethod;

/**
 * Login handler for dev server login
 * This is intended for local development and testing purposes only.
 */
public class DevServerLoginHandler implements LoginMethodHandler {

    private static final Logger log = Logger.getLogger();

    @Override
    public void handleLogin(HttpServletRequest req, HttpServletResponse resp, String nextUrl) throws IOException {
        if (!Config.isDevServerLoginEnabled()) {
            resp.sendError(HttpStatus.SC_FORBIDDEN);
            return;
        }

        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.DEV_SERVER);
        String encryptedState = StringHelper.encrypt(JsonUtils.toCompactJson(state));
        String redirectUrl = resp.encodeRedirectURL("/devServerLogin?state="
                + UrlHelper.encodeQueryParam(encryptedState));
        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to dev server login page");

        resp.sendRedirect(redirectUrl);
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, HttpServletResponse resp, AuthState state) throws IOException {
        if (!Config.isDevServerLoginEnabled()) {
            resp.sendError(HttpStatus.SC_FORBIDDEN);
            return null;
        }

        String email = req.getParameter("email");
        if (email == null) {
            log.warning("Missing email parameter in dev server login callback");
            resp.sendError(HttpStatus.SC_BAD_REQUEST);
            return null;
        }

        String sessionId = state.sessionId();
        if (!sessionId.equals(req.getSession().getId())) {
            // Invalid session ID
            log.warning(String.format("Different session ID: expected %s, got %s",
                    sessionId, req.getSession().getId()));
            HttpResponseHelper.logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid session ID");
            return null;
        }

        return new AuthResult(Provider.TEAMMATES_DEV, email, null, email);
    }

}
