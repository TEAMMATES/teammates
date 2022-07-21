package teammates.ui.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;

/**
 * Servlet that handles login.
 */
public class LoginServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));
        if (Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpStatus.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", "/devServerLogin?nextUrl=" + nextUrl.replace("&", "%26"));
            log.request(req, HttpStatus.SC_MOVED_PERMANENTLY, "Redirect to dev server login page");
            return;
        }

        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        boolean isLoginNeeded = uic == null || !uic.isValid();
        if (!isLoginNeeded) {
            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to next URL");
            resp.sendRedirect(nextUrl);
            return;
        }

        log.request(req, HttpStatus.SC_MOVED_PERMANENTLY, "Redirect to web login page");
        resp.sendRedirect(Config.APP_FRONTEND_URL + "/web/login?nextUrl="
                + nextUrl.replace("?", "%3f").replace("&", "%26"));

    }

}
