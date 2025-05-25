package teammates.ui.webapi;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.Cookie;

import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;
import teammates.ui.output.AuthInfo;

/**
 * Action: gets user authentication information.
 *
 * <p>This does not log in or log out the user.
 */
public class GetAuthInfoAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // Login information is available to everyone
    }

    @Override
    public JsonResult execute() {
        String frontendUrl = getRequestParamValue("frontendUrl");
        String nextUrl = getRequestParamValue("nextUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }

        AuthInfo output;
        if (userInfo == null) {
            if (nextUrl == null) {
                output = new AuthInfo(
                        createLoginUrl(frontendUrl, Const.WebPageURIs.STUDENT_HOME_PAGE),
                        createLoginUrl(frontendUrl, Const.WebPageURIs.INSTRUCTOR_HOME_PAGE),
                        createLoginUrl(frontendUrl, Const.WebPageURIs.ADMIN_HOME_PAGE),
                        createLoginUrl(frontendUrl, Const.WebPageURIs.MAINTAINER_HOME_PAGE)
                );
            } else {
                output = new AuthInfo(
                        createLoginUrl(frontendUrl, nextUrl),
                        createLoginUrl(frontendUrl, nextUrl),
                        createLoginUrl(frontendUrl, nextUrl),
                        createLoginUrl(frontendUrl, nextUrl)
                );
            }
        } else {
            output = new AuthInfo(userInfo, authType == AuthType.MASQUERADE);
        }

        String csrfToken = StringHelper.encrypt(req.getSession().getId());
        String existingCsrfToken = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.CSRF_COOKIE_NAME);
        if (csrfToken.equals(existingCsrfToken)) {
            return new JsonResult(output);
        }
        Cookie csrfTokenCookie = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME, csrfToken);
        csrfTokenCookie.setPath("/");
        List<Cookie> cookieList = Collections.singletonList(csrfTokenCookie);
        return new JsonResult(output, cookieList);
    }

    /**
     * Returns a LoginURL based on the frontendURL and nextURL.
     */
    public static String createLoginUrl(String frontendUrl, String nextUrl) {
        return Const.WebPageURIs.LOGIN + "?nextUrl=" + frontendUrl + nextUrl;
    }

}
