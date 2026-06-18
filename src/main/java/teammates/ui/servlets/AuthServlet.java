package teammates.ui.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpResponseHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.UrlHelper;

/**
 * Common servlet class that serves user authentication-related functions.
 */
abstract class AuthServlet extends HttpServlet {

    private static final MemoryDataStoreFactory DATA_STORE_FACTORY = MemoryDataStoreFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList("openid", "email");

    /**
     * Gets the Google authorization code flow to be used across all HTTP servlet requests.
     */
    GoogleAuthorizationCodeFlow getGoogleAuthorizationFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, Config.OIDC_GOOGLE_CLIENT_ID, Config.OIDC_GOOGLE_CLIENT_SECRET, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
    }

    /**
     * Returns the redirect URI for the given HTTP servlet request.
     */
    String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString().replaceFirst("^http://", "https://"));
        url.setRawPath("/oauth2callback");
        url.set("ngsw-bypass", "true");
        return url.build();
    }

    /**
     * Returns the Google ID token verifier to be used across all HTTP servlet requests.
     */
    GoogleIdTokenVerifier getGoogleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
                .setAudience(List.of(Config.OIDC_GOOGLE_CLIENT_ID))
                .build();
    }

    Cookie getLoginInvalidationCookie() {
        Cookie cookie = new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setSecure(!Config.IS_DEV_SERVER);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

    Cookie getLoginCookie(UserInfoCookie uic) {
        Cookie cookie = new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME,
                StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
        cookie.setPath("/");
        cookie.setSecure(!Config.IS_DEV_SERVER);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) Const.COOKIE_VALIDITY_PERIOD.toSeconds()); // one week
        return cookie;
    }

    /**
     * Logs the given error message and prints it in the HTTP response.
     */
    void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        HttpResponseHelper.logAndPrintError(req, resp, status, message);
    }

    /**
     * Encodes the given query parameter value to be safely included in a URL.
     */
    String encodeQueryParam(String param) {
        return UrlHelper.encodeQueryParam(param);
    }

    /**
     * Represents the state object to be persisted during the callback.
     */
    static class AuthState {
        private final String nextUrl;
        private final String sessionId;

        @JsonCreator
        AuthState(String nextUrl, String sessionId) {
            this.nextUrl = nextUrl;
            this.sessionId = sessionId;
        }

        String getNextUrl() {
            return nextUrl;
        }

        public String getSessionId() {
            return sessionId;
        }
    }

}
