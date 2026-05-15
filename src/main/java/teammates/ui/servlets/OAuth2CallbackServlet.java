package teammates.ui.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponse;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.HttpRequest;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Account;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthResult authResult;
        if (Config.isDevServerLoginEnabled()) {
            authResult = getDevServerAuthResult(req);
        } else {
            authResult = getGoogleOauth2AuthResult(req, resp);
        }

        if (authResult == null) {
            return;
        }

        Cookie cookie;
        if (authResult.email == null) {
            // invalid email
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
        } else {
            Account account;
            try {
                HibernateUtil.beginTransaction();
                account = accountsLogic.createOrGetAccountForEmail(authResult.email);
                HibernateUtil.commitTransaction();
            } catch (Exception e) {
                HibernateUtil.rollbackTransaction();
                log.warning("Failed to create or get account for " + authResult.email, e);
                req.getSession().invalidate();

                cookie = getLoginInvalidationCookie();
                resp.addCookie(cookie);
                resp.sendRedirect(authResult.nextUrl);
                return;
            }

            UserInfoCookie uic = new UserInfoCookie(authResult.email, account.getId());
            cookie = getLoginCookie(uic);
        }

        log.info("Going to redirect to: " + authResult.nextUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Login successful");

        resp.addCookie(cookie);
        resp.sendRedirect(authResult.nextUrl);
    }

    private AuthResult getDevServerAuthResult(HttpServletRequest req) {
        String email = req.getParameter("email");
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        return new AuthResult(email, nextUrl);
    }

    private AuthResult getGoogleOauth2AuthResult(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuffer buf = req.getRequestURL();
        if (req.getQueryString() != null) {
            buf.append('?').append(req.getQueryString());
        }
        AuthorizationCodeResponseUrl responseUrl =
                new AuthorizationCodeResponseUrl(buf.toString().replaceFirst("^http://", "https://"));
        if (responseUrl.getError() != null) {
            logAndPrintError(req, resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, responseUrl.getError());
            return null;
        }
        String code = responseUrl.getCode();
        String state = responseUrl.getState();
        if (code == null || state == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Missing authorization code");
            return null;
        }

        String nextUrl = "/";
        try {
            AuthState authState = JsonUtils.fromJson(StringHelper.decrypt(state), AuthState.class);
            if (authState.getNextUrl() != null) {
                nextUrl = authState.getNextUrl();
            }
            String sessionId = authState.getSessionId();
            if (!sessionId.equals(req.getSession().getId())) {
                // Invalid session ID
                log.warning(String.format("Different session ID: expected %s, got %s",
                        sessionId, req.getSession().getId()));
                logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid authorization code");
                return null;
            }
        } catch (JacksonException | InvalidParametersException e) {
            log.warning("Failed to parse state object", e);
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Bad state object");
            return null;
        }

        String redirectUri = getRedirectUri(req);
        TokenResponse token = getAuthorizationFlow().newTokenRequest(code).setRedirectUri(redirectUri).execute();
        String email = null;
        try {
            String userInfoResponse = HttpRequest.executeGetRequest(
                    new URI("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="
                            + token.getAccessToken()));

            Map<String, Object> parsedResponse =
                    JsonUtils.fromJson(userInfoResponse, new TypeReference<>(){});
            if (parsedResponse.containsKey("email")) {
                email = String.valueOf(parsedResponse.get("email"));
            }
        } catch (URISyntaxException | IOException | JacksonException e) {
            // if any of the operation fail, googleId is kept at null
            log.warning("Failed to get Google ID", e);
        }
        return new AuthResult(email, nextUrl);
    }

    private void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().print(message);

        log.request(req, status, message);
    }

    private static final class AuthResult {
        private final String email;
        private final String nextUrl;

        private AuthResult(String email, String nextUrl) {
            this.email = email;
            this.nextUrl = nextUrl;
        }
    }

}
