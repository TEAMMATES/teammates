package teammates.ui.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Common servlet class that serves user authentication-related functions.
 */
abstract class AuthServlet extends HttpServlet {

    private static final MemoryDataStoreFactory DATA_STORE_FACTORY = MemoryDataStoreFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> GOOGLE_SCOPES = Arrays.asList("https://www.googleapis.com/auth/userinfo.email");

    private static final String MICROSOFT_AUTHORITY_BASE = "https://login.microsoftonline.com/";
    protected static final Set<String> MICROSOFT_SCOPES = Set.of("openid", "email");

    /**
     * Gets the authorization code flow to be used for Google OAuth2 authentication.
     */
    AuthorizationCodeFlow getGoogleAuthorizationFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, Config.OAUTH2_GOOGLE_CLIENT_ID, Config.OAUTH2_GOOGLE_CLIENT_SECRET, GOOGLE_SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
    }

    /**
     * Creates a Microsoft Entra ID confidential client application (MSAL).
     * Uses a client secret as authentication credential.
     * The returned instance performs full JWT signature verification against Microsoft's JWKS.
     */
    ConfidentialClientApplication getMicrosoftClient() throws MalformedURLException {
        return ConfidentialClientApplication
                .builder(Config.OAUTH2_MS_ENTRA_CLIENT_ID,
                        ClientCredentialFactory.createFromSecret(Config.OAUTH2_MS_ENTRA_CLIENT_SECRET))
                .authority(MICROSOFT_AUTHORITY_BASE + "common") // To allow users from any tenant to sign in
                .build();
    }

    /**
     * Returns the redirect URI for the given HTTP servlet request.
     */
    String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(getSecureRequestUrl(req));
        url.setRawPath("/oauth2callback");
        url.set("ngsw-bypass", "true");
        return url.build();
    }

    /**
     * Returns the redirect URI for Microsoft Entra ID for the given HTTP servlet request.
     */
    String getMicrosoftRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(getSecureRequestUrl(req));
        url.setRawPath("/oauth2callback");
        return url.build();
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

    String getSecureRequestUrl(HttpServletRequest req) {
        return req.getRequestURL().toString().replaceFirst("^http://", "https://");
    }

    /**
     * Represents the state object to be persisted during the callback.
     */
    static class AuthState {
        private final String nextUrl;
        private final String sessionId;
        private final String provider;

        AuthState(String nextUrl, String sessionId) {
            this(nextUrl, sessionId, null);
        }

        AuthState(String nextUrl, String sessionId, String provider) {
            this.nextUrl = nextUrl;
            this.sessionId = sessionId;
            this.provider = provider;
        }

        String getNextUrl() {
            return nextUrl;
        }

        public String getSessionId() {
            return sessionId;
        }

        String getProvider() {
            return provider;
        }
    }

}
