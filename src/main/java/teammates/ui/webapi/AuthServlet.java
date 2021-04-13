package teammates.ui.webapi;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

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
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/userinfo.email");

    /**
     * Gets the authorization code flow to be used across all HTTP servlet requests.
     */
    AuthorizationCodeFlow getAuthorizationFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, Config.OAUTH2_CLIENT_ID, Config.OAUTH2_CLIENT_SECRET, SCOPES)
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
        return url.build();
    }

    Cookie getLoginInvalidationCookie() {
        Cookie cookie = new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setSecure(!Config.isDevServer());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

    Cookie getLoginCookie(UserInfoCookie uic) {
        Cookie cookie = new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME,
                StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
        cookie.setPath("/");
        cookie.setSecure(!Config.isDevServer());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // one week
        return cookie;
    }

    /**
     * Represents the state object to be persisted during the callback.
     */
    static class AuthState {
        private final String nextUrl;
        private final String sessionId;

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
