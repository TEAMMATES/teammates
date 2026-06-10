package teammates.ui.servlets;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpResponseHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.ui.loginmethodhandlers.DevServerLoginHandler;
import teammates.ui.loginmethodhandlers.GoogleLoginHandler;
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * Common servlet class that serves user authentication-related functions.
 */
abstract class AuthServlet extends HttpServlet {

    private static final Map<LoginMethod, LoginMethodHandler> LOGIN_HANDLERS = Map.of(
            LoginMethod.DEV_SERVER, new DevServerLoginHandler(),
            LoginMethod.GOOGLE, new GoogleLoginHandler());

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
     * Gets the login method from the login request and returns the corresponding handler.
     */
    LoginMethodHandler getLoginMethodHandler(LoginMethod loginMethod) {
        return LOGIN_HANDLERS.get(loginMethod);
    }

    /**
     * Logs the given error message and prints it in the HTTP response.
     */
    void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        HttpResponseHelper.logAndPrintError(req, resp, status, message);
    }

}
