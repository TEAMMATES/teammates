package teammates.ui.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.FileHelper;

/**
 * Servlet that handles dev server login.
 */
public class DevServerLoginServlet extends AuthServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
