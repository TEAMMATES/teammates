package teammates.ui.webapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.HttpRequest;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (Config.isDevServer()) {
            return;
        }

        StringBuffer buf = req.getRequestURL();
        if (req.getQueryString() != null) {
            buf.append('?').append(req.getQueryString());
        }
        AuthorizationCodeResponseUrl responseUrl =
                new AuthorizationCodeResponseUrl(buf.toString().replaceFirst("^http://", "https://"));
        if (responseUrl.getError() != null) {
            resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(responseUrl.getError());
            return;
        }
        String code = responseUrl.getCode();
        String state = responseUrl.getState();
        if (code == null || state == null) {
            resp.setStatus(HttpStatus.SC_BAD_REQUEST);
            resp.getWriter().print("Missing authorization code");
            return;
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
                resp.setStatus(HttpStatus.SC_BAD_REQUEST);
                resp.getWriter().print("Invalid authorization code");
                return;
            }
        } catch (JsonParseException | InvalidParametersException e) {
            resp.setStatus(HttpStatus.SC_BAD_REQUEST);
            resp.getWriter().print("Invalid authorization code");
            return;
        }

        String redirectUri = getRedirectUri(req);
        TokenResponse token = getAuthorizationFlow().newTokenRequest(code).setRedirectUri(redirectUri).execute();
        String googleId = null;
        try {
            String userInfoResponse = HttpRequest.executeGetRequest(
                    new URI("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="
                            + token.getAccessToken()));

            Map<String, Object> parsedResponse =
                    JsonUtils.fromJson(userInfoResponse, new TypeToken<Map<String, Object>>(){}.getType());
            String email = String.valueOf(parsedResponse.get("email"));
            if (email != null) {
                googleId = email.replaceFirst("@gmail\\.com$", "");
            }
        } catch (URISyntaxException | IOException | JsonSyntaxException e) {
            // if any of the operation fail, googleId is kept at null
        }

        Cookie cookie;
        if (googleId == null) {
            // invalid google ID
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
        } else {
            boolean isAdmin = Config.APP_ADMINS.contains(googleId);
            UserInfoCookie uic = new UserInfoCookie(googleId, isAdmin);
            cookie = getLoginCookie(uic);
        }

        resp.addCookie(cookie);
        resp.sendRedirect(nextUrl);
    }

}
