package teammates.ui.loginmethodhandlers;

import static teammates.common.util.HttpResponseHelper.logAndPrintError;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.ui.output.LoginMethod;

/**
 * Login handler for Google login.
 */
public class GoogleLoginHandler implements LoginMethodHandler {

    private static final Logger log = Logger.getLogger();

    private static final MemoryDataStoreFactory DATA_STORE_FACTORY = MemoryDataStoreFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList("openid", "email");

    @Override
    public void handleLogin(HttpServletRequest req, HttpServletResponse resp, String nextUrl) throws IOException {
        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.GOOGLE);
        GoogleAuthorizationCodeRequestUrl authorizationUrl = getGoogleAuthorizationFlow().newAuthorizationUrl();
        authorizationUrl.setRedirectUri(getRedirectUri(req));
        authorizationUrl.setState(StringHelper.encrypt(JsonUtils.toCompactJson(state)));

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to Google sign-in page");

        resp.sendRedirect(authorizationUrl.build());
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, HttpServletResponse resp, AuthState state) throws IOException {
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
        if (code == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Missing authorization code");
            return null;
        }

        String sessionId = state.sessionId();
        if (!sessionId.equals(req.getSession().getId())) {
            // Invalid session ID
            log.warning(String.format("Different session ID: expected %s, got %s",
                    sessionId, req.getSession().getId()));
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid authorization code");
            return null;
        }

        String redirectUri = getRedirectUri(req);
        GoogleTokenResponse token = getGoogleAuthorizationFlow()
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

        return new AuthResult(Provider.GOOGLE, payload.getSubject(), null, payload.getEmail());
    }

    /**
     * Gets the Google authorization code flow to be used across all HTTP servlet requests.
     */
    private GoogleAuthorizationCodeFlow getGoogleAuthorizationFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, Config.OIDC_GOOGLE_CLIENT_ID, Config.OIDC_GOOGLE_CLIENT_SECRET, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
    }

    /**
     * Returns the redirect URI for the given HTTP servlet request.
     */
    private String getRedirectUri(HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString();
        if (Config.isDevServerLoginEnabled()) {
            requestUrl = requestUrl.replaceFirst("^https://", "http://");
        } else {
            requestUrl = requestUrl.replaceFirst("^http://", "https://");
        }
        GenericUrl url = new GenericUrl(requestUrl);
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    /**
     * Returns the Google ID token verifier to be used across all HTTP servlet requests.
     */
    private GoogleIdTokenVerifier getGoogleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
            .setAudience(List.of(Config.OIDC_GOOGLE_CLIENT_ID))
            .build();
    }

}
