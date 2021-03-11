package teammates.ui.webapi;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Common servlet class that serves user authentication-related functions.
 */
abstract class AuthServlet extends HttpServlet {

    Cookie getLoginInvalidationCookie() {
        Cookie cookie = new Cookie(Const.AuthConfig.AUTH_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setSecure(!Config.isDevServer());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

    Cookie getLoginCookie(UserInfoCookie uic) {
        Cookie cookie = new Cookie(Const.AuthConfig.AUTH_COOKIE_NAME, StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
        cookie.setPath("/");
        cookie.setSecure(!Config.isDevServer());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // one week
        return cookie;
    }

}
