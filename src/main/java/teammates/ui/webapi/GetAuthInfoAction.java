package teammates.ui.webapi;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.Cookie;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;
import teammates.ui.output.AuthInfo;

/**
 * Action: gets user authentication information.
 *
 * <p>This does not log in or log out the user.
 */
public class GetAuthInfoAction extends PublicAction {

    @Override
    public JsonResult execute() {
        String frontendUrl = getRequestParamValue("frontendUrl");
        String nextUrl = getRequestParamValue("nextUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }
        if (nextUrl == null) {
            nextUrl = "";
        }

        AuthInfo output = new AuthInfo(createLoginUrl(frontendUrl, nextUrl), userInfo, authType == AuthType.MASQUERADE);
        String existingCsrfToken = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.CSRF_COOKIE_NAME);
        if (existingCsrfToken != null && isMatchingCsrfToken(existingCsrfToken, req.getSession().getId())) {
            return new JsonResult(output);
        }
        String csrfToken = StringHelper.encrypt(req.getSession().getId());
        Cookie csrfTokenCookie = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME, csrfToken);
        csrfTokenCookie.setPath("/");
        List<Cookie> cookieList = Collections.singletonList(csrfTokenCookie);
        return new JsonResult(output, cookieList);
    }

    private static boolean isMatchingCsrfToken(String existingCsrfToken, String sessionId) {
        try {
            return sessionId.equals(StringHelper.decrypt(existingCsrfToken));
        } catch (InvalidParametersException e) {
            return false;
        }
    }

    /**
     * Returns a LoginURL based on the frontendURL and nextURL.
     */
    public static String createLoginUrl(String frontendUrl, String nextUrl) {
        return Const.WebPageURIs.LOGIN + "?nextUrl=" + frontendUrl + nextUrl;
    }

}
