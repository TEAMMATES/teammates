package teammates.ui.webapi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.HttpRequestHelper;

/**
 * Servlet that handles dev server login.
 */
@SuppressWarnings("serial")
public class DevServerLoginServlet extends AuthServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServer()) {
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", Const.WebPageURIs.LOGIN);
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

        String html = FileHelper.readResourceFile("devServerLoginPage.html");
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.print(html);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServer()) {
            resp.setStatus(HttpStatus.SC_FORBIDDEN);
            return;
        }

        String email = req.getParameter("email");
        if (email == null) {
            return;
        }
        boolean isAdmin = "on".equalsIgnoreCase(req.getParameter("isAdmin"));

        UserInfoCookie uic = new UserInfoCookie(email.replaceFirst("@gmail\\.com$", ""), isAdmin);
        Cookie cookie = getLoginCookie(uic);
        resp.addCookie(cookie);

        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        resp.sendRedirect(nextUrl);
    }

}
