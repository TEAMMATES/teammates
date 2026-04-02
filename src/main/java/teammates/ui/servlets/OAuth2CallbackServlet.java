package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.JsonParseException;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.logic.auth.GoogleOidcTokenVerifier;
import teammates.sqllogic.core.AccountsLogic;
import teammates.storage.sqlentity.Account;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthResult authResult;
        if (Config.isUsingFirebase()) {
            authResult = getFirebaseAuthResult(req, resp);
        } else {
            authResult = getGoogleOauth2AuthResult(req, resp);
        }
        if (authResult == null) {
            return;
        }
        Cookie cookie;

        if (authResult.accountId == null) {
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
        } else {
            UserInfoCookie uic = new UserInfoCookie(authResult.accountId);
            cookie = getLoginCookie(uic);
        }

        log.info("Going to redirect to: " + authResult.nextUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Login successful");

        resp.addCookie(cookie);
        resp.sendRedirect(authResult.nextUrl);
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
                log.warning(String.format("Different session ID: expected %s, got %s",
                        sessionId, req.getSession().getId()));
                logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid authorization code");
                return null;
            }
        } catch (JsonParseException | InvalidParametersException e) {
            log.warning("Failed to parse state object", e);
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Bad state object");
            return null;
        }

        String redirectUri = getRedirectUri(req);
        TokenResponse token = getAuthorizationFlow().newTokenRequest(code).setRedirectUri(redirectUri).execute();
        String idTokenString = null;
        if (token instanceof GoogleTokenResponse googleTokenResponse) {
            idTokenString = googleTokenResponse.getIdToken();
        } else {
            log.warning("No id_token in OAuth token response");
            return new AuthResult(null, nextUrl);
        }

        GoogleIdToken idToken = GoogleOidcTokenVerifier.verify(idTokenString);
        if (idToken == null) {
            log.warning("Failed to verify Google id_token");
            return new AuthResult(null, nextUrl);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String iss = payload.getIssuer();
        String sub = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        if (iss == null || sub == null) {
            log.warning("Missing iss or sub in Google id_token");
            return new AuthResult(null, nextUrl);
        }

        try {
            Account account = AccountsLogic.inst().resolveOrCreateAccountFromOidc(iss, sub, email, name, Const.LoginProviders.GOOGLE);
            return new AuthResult(account.getId().toString(), nextUrl);
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            log.warning("Failed to resolve account from Google login", e);
            return new AuthResult(null, nextUrl);
        }
    }

    private AuthResult getFirebaseAuthResult(HttpServletRequest req, HttpServletResponse resp) {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));

        String idToken = req.getParameter("idToken");
        if (idToken == null) {
            return null;
        }
        FirebaseAuth instance = FirebaseAuth.getInstance();
        try {
            FirebaseToken userToken = instance.verifyIdToken(idToken);
            String email = userToken.getEmail();
            String uid = userToken.getUid();
            String projectId = FirebaseApp.getInstance().getOptions().getProjectId();
            String issuer = "https://securetoken.google.com/" + projectId;
            Account account = AccountsLogic.inst().resolveOrCreateAccountFromOidc(
                    issuer, uid, email, email, Const.LoginProviders.GOOGLE);
            return new AuthResult(account.getId().toString(), nextUrl);
        } catch (FirebaseAuthException | InvalidParametersException | EntityAlreadyExistsException e) {
            log.warning("Invalid Firebase ID token or account resolution failed", e);
            return new AuthResult(null, nextUrl);
        }
    }

    private void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().print(message);

        log.request(req, status, message);
    }

    private static final class AuthResult {
        private final String accountId;
        private final String nextUrl;

        private AuthResult(String accountId, String nextUrl) {
            this.accountId = accountId;
            this.nextUrl = nextUrl;
        }
    }
}
