package teammates.ui.loginmethodhandlers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

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
import teammates.ui.exception.AuthException;
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
    public String handleLogin(HttpServletRequest req, String nextUrl) throws IOException, AuthException {
        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.GOOGLE);
        GoogleAuthorizationCodeRequestUrl authorizationUrl = getGoogleAuthorizationFlow().newAuthorizationUrl();
        authorizationUrl.setRedirectUri(getRedirectUri(req));
        authorizationUrl.setState(StringHelper.encrypt(JsonUtils.toCompactJson(state)));

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to Google sign-in page");

        return authorizationUrl.build();
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, AuthState state) throws IOException, AuthException {
        StringBuffer buf = req.getRequestURL();
        if (req.getQueryString() != null) {
            buf.append('?').append(req.getQueryString());
        }

        AuthorizationCodeResponseUrl responseUrl;
        try {
            responseUrl = new AuthorizationCodeResponseUrl(
                    buf.toString().replaceFirst("^http://", "https://"));
        } catch (IllegalArgumentException e) {
            // Malformed Google callback URL.
            throw new AuthException("Invalid Google callback URL", e);
        }

        if (responseUrl.getError() != null) {
            throw new AuthException("Error in Google OAuth2 callback: " + responseUrl.getError());
        }

        String code = responseUrl.getCode();
        if (code == null) {
            // Should not happen if there is no error, but just in case.
            throw new AuthException("Missing authorization code");
        }

        String sessionId = state.sessionId();
        if (!sessionId.equals(req.getSession().getId())) {
            String message = String.format("Different session ID: expected %s, got %s",
                    sessionId, req.getSession().getId());
            throw new AuthException(message);
        }

        String redirectUri = getRedirectUri(req);
        GoogleTokenResponse token = requestToken(code, redirectUri);

        Payload payload;
        try {
            GoogleIdToken idToken = verifyIdToken(token.getIdToken());
            if (idToken == null) {
                throw new AuthException("Invalid ID token");
            }
            payload = idToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new AuthException("Failed to verify ID token", e);
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

    GoogleTokenResponse requestToken(String code, String redirectUri) throws IOException {
        return getGoogleAuthorizationFlow()
                .newTokenRequest(code).setRedirectUri(redirectUri).execute();
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

    GoogleIdToken verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        return getGoogleIdTokenVerifier().verify(idToken);
    }

}
