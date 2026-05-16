package teammates.ui.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.HttpRequestHelper;

/**
 * Servlet that handles dev server login.
 */
public class DevServerLoginServlet extends AuthServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));
        if (!Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", Const.WebPageURIs.LOGIN + "?nextUrl=" + nextUrl.replace("&", "%26"));
            return;
        }

        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        boolean isLoginNeeded = uic == null || !uic.isValid();
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
        if (!Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpStatus.SC_FORBIDDEN);
            return;
        }

        String email = req.getParameter("email");
        if (email == null) {
            return;
        }

        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }

        // Prevent HTTP response splitting
        nextUrl = nextUrl.replace("\r\n", "");
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String encodedNextUrl = URLEncoder.encode(nextUrl, StandardCharsets.UTF_8);
        String redirectUrl = resp.encodeRedirectURL("/oauth2callback?email=" + encodedEmail + "&nextUrl=" + encodedNextUrl);
        resp.sendRedirect(redirectUrl);
    }

}
