package teammates.ui.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HttpRequest;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String provider = determineAuthProvider(req);
        if (provider == null) {
            log.warning("Failed to determine OAuth2 provider from state parameter.");
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid state parameter");
            return;
        }

        AuthResult authResult;
        switch (provider) {
        case Const.AuthProviderTypes.FIREBASE:
            authResult = getFirebaseAuthResult(req, resp);
            break;
        case Const.AuthProviderTypes.MICROSOFT_ENTRA:
            authResult = getMicrosoftEntraAuthResult(req, resp);
            break;
        case Const.AuthProviderTypes.GOOGLE:
            authResult = getGoogleOauth2AuthResult(req, resp);
            break;
        default:
            log.warning("Unknown auth provider: " + provider);
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Unknown auth provider: " + provider);
            return;
        }

        if (authResult == null) {
            return;
        }

        Cookie cookie;
        if (authResult.email == null) {
            // invalid google ID
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
        } else {
            UserInfoCookie uic = new UserInfoCookie(authResult.email);
            cookie = getLoginCookie(uic);
        }

        log.info("Going to redirect to: " + authResult.nextUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Login successful");

        resp.addCookie(cookie);
        resp.sendRedirect(authResult.nextUrl);
    }

    private String determineAuthProvider(HttpServletRequest req) {
        // Firebase flow posts idToken directly without a state parameter
        if (req.getParameter("idToken") != null) {
            return Const.AuthProviderTypes.FIREBASE;
        }

        String state = req.getParameter("state");
        if (state == null) {
            log.warning("Missing state parameter in OAuth2 callback");
            return null;
        }

        try {
            AuthState authState = JsonUtils.fromJson(StringHelper.decrypt(state), AuthState.class);
            if (authState.getProvider() != null) {
                return authState.getProvider();
            }
        } catch (Exception e) {
            log.warning("Failed to extract provider from state, falling back to global config");
        }
        return null;
    }

    private AuthResult getMicrosoftEntraAuthResult(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String state = req.getParameter("state");
        String error = req.getParameter("error");
        if (error != null) {
            String errorDescription = req.getParameter("error_description");
            String message = errorDescription == null ? error : error + ": " + errorDescription;
            logAndPrintError(req, resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
            return null;
        }

        if (code == null || state == null) {
            log.warning("Missing Microsoft authorization code or state. Query string: " + req.getQueryString());
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

        String email = null;
        try {
            AuthorizationCodeParameters params = AuthorizationCodeParameters
                    .builder(code, new URI(getMicrosoftRedirectUri(req)))
                    .scopes(Set.of("openid", "email"))
                    .build();
            IAuthenticationResult result = getMicrosoftClient().acquireToken(params).get();
            // username() returns the verified email/UPN from the validated ID token
            email = result.account().username();
        } catch (URISyntaxException | ExecutionException e) {
            log.warning("Failed to acquire Microsoft token", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warning("Microsoft token acquisition interrupted", e);
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
        } catch (JsonParseException | InvalidParametersException e) {
            log.warning("Failed to parse state object", e);
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Bad state object");
            return null;
        }

        String redirectUri = getRedirectUri(req);
        TokenResponse token = getGoogleAuthorizationFlow().newTokenRequest(code).setRedirectUri(redirectUri).execute();
        String email = null;
        try {
            String userInfoResponse = HttpRequest.executeGetRequest(
                    new URI("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="
                            + token.getAccessToken()));

            Map<String, Object> parsedResponse =
                    JsonUtils.fromJson(userInfoResponse, new TypeToken<Map<String, Object>>(){}.getType());
            if (parsedResponse.containsKey("email")) {
                email = String.valueOf(parsedResponse.get("email"));
            }
        } catch (URISyntaxException | IOException | JsonSyntaxException e) {
            // if any of the operation fail, googleId is kept at null
            log.warning("Failed to get Google ID", e);
        }
        return new AuthResult(email, nextUrl);
    }

    private AuthResult getFirebaseAuthResult(HttpServletRequest req, HttpServletResponse resp) {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));

        String email = null;
        String idToken = req.getParameter("idToken");
        if (idToken == null) {
            return null;
        } else {
            FirebaseAuth instance = FirebaseAuth.getInstance();
            try {
                FirebaseToken userToken = instance.verifyIdToken(idToken);
                email = userToken.getEmail();
                // Delete the user immediately as we do not need to keep user info
                instance.deleteUser(userToken.getUid());
            } catch (FirebaseAuthException e) {
                log.warning("Invalid user ID token", e);
            }
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
