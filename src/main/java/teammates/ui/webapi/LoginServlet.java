package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Servlet that handles login.
 */
@SuppressWarnings("serial")
public class LoginServlet extends AuthServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (Config.isDevServer()) {
            resp.setStatus(HttpStatus.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", "/devServerLogin");
            return;
        }

        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        boolean isLoginNeeded = uic == null || !uic.isValid();
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        if (!isLoginNeeded) {
            resp.sendRedirect(nextUrl);
            return;
        }

        AuthState state = new AuthState(nextUrl, req.getSession().getId());
        AuthorizationCodeRequestUrl authorizationUrl = getAuthorizationFlow().newAuthorizationUrl();
        authorizationUrl.setRedirectUri(getRedirectUri(req));
        authorizationUrl.setState(StringHelper.encrypt(JsonUtils.toCompactJson(state)));
        resp.sendRedirect(authorizationUrl.build());
    }

}
