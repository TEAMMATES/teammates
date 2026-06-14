package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.Provider;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.UrlHelper;
import teammates.ui.output.LoginMethod;

import tools.jackson.core.JacksonException;

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
        String redirectUrl = resp.encodeRedirectURL("/devServerLogin?state="
                + UrlHelper.encodeQueryParam(JsonUtils.toCompactJson(state)));
        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to dev server login page");

        resp.sendRedirect(redirectUrl);
    }

    @Override
    public AuthResult getAuthResult(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServerLoginEnabled()) {
            resp.sendError(HttpStatus.SC_FORBIDDEN);
            return null;
        }

        String email = req.getParameter("email");
        String state = req.getParameter("state");
        if (email == null || state == null) {
            log.warning("Missing email or state parameter in dev server login callback");
            resp.sendError(HttpStatus.SC_BAD_REQUEST);
            return null;
        }

        String nextUrl = "/";
        try {
            AuthState authState = JsonUtils.fromJson(StringHelper.decrypt(state), AuthState.class);
            if (authState.getNextUrl() != null) {
                nextUrl = authState.getNextUrl();
            }
        } catch (JacksonException | InvalidParametersException e) {
            log.warning("Failed to parse state object", e);
            return null;
        }

        return new AuthResult(Provider.TEAMMATES_DEV, email, null, email, nextUrl);
    }

}
