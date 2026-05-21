package teammates.ui.servlets;

import java.io.IOException;
import java.io.PrintWriter;

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

        email = getEncodedQueryParam(email);
        nextUrl = getEncodedQueryParam(getSanitizedRedirectUrl(nextUrl));
        String redirectUrl = resp.encodeRedirectURL("/oauth2callback?email=" + email + "&nextUrl=" + nextUrl);
        resp.sendRedirect(redirectUrl);
    }

}
