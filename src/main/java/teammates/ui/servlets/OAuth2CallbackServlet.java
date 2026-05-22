package teammates.ui.servlets;

import java.io.IOException;
import java.security.GeneralSecurityException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Account;

import tools.jackson.core.JacksonException;

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
        String logMessage;
        if (authResult.isValid()) {
            try {
                HibernateUtil.beginTransaction();
                Account account = accountsLogic.createOrGetAccount(
                        authResult.issuer, authResult.subject, authResult.email);
                HibernateUtil.commitTransaction();

                UserInfoCookie uic = new UserInfoCookie(account.getId());
                cookie = getLoginCookie(uic);
                logMessage = "Login successful";
            } catch (Exception e) {
                HibernateUtil.rollbackTransaction();
                log.warning("Failed to create or get account for " + authResult.email, e);
                req.getSession().invalidate();

                cookie = getLoginInvalidationCookie();
                logMessage = "Login failed";
            }
        } else {
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
            logMessage = "Login failed";
        }

        String redirectUrl = resp.encodeRedirectURL(getSanitizedRedirectUrl(authResult.nextUrl));
        log.info("Going to redirect to: " + redirectUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, logMessage);

        resp.addCookie(cookie);
        resp.sendRedirect(redirectUrl);
    }

    private AuthResult getDevServerAuthResult(HttpServletRequest req) {
        String email = req.getParameter("email");
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        return new AuthResult(Const.OidcIssuers.DEV_SERVER, email, email, nextUrl);
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
        GoogleTokenResponse token = (GoogleTokenResponse) getGoogleAuthorizationFlow()
                .newTokenRequest(code).setRedirectUri(redirectUri).execute();

        Payload payload;
        try {
            GoogleIdToken idToken = getGoogleIdTokenVerifier().verify(token.getIdToken());
            if (idToken == null) {
                logAndPrintError(req, resp, HttpStatus.SC_UNAUTHORIZED, "Invalid ID token");
                return null;
            }
            payload = idToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            log.warning("Failed to verify ID token", e);
            logAndPrintError(req, resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Failed to verify ID token");
            return null;
        }

        return new AuthResult(payload.getIssuer(), payload.getSubject(), payload.getEmail(), nextUrl);
    }

    private void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().print(message);

        log.request(req, status, message);
    }

    private static final class AuthResult {
        private final String issuer;
        private final String subject;
        private final String email;
        private final String nextUrl;

        private AuthResult(String issuer, String subject, String email, String nextUrl) {
            this.issuer = issuer;
            this.subject = subject;
            this.email = email;
            this.nextUrl = nextUrl;
        }

        public boolean isValid() {
            boolean hasEmail = email != null;
            boolean hasSubject = subject != null;
            boolean isOidcIssuerValid = FieldValidator.getInvalidityInfoForOidcIssuer(
                    issuer, Config.isDevServerLoginEnabled()).isEmpty();
            return hasEmail && hasSubject && isOidcIssuerValid;
        }
    }

}
