package teammates.ui.servlets;

import static teammates.common.util.UrlHelper.encodeQueryParam;
import static teammates.common.util.UrlHelper.getSafeRedirectUrl;

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
            resp.sendError(HttpStatus.SC_FORBIDDEN);
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
            resp.sendError(HttpStatus.SC_FORBIDDEN);
            return;
        }

        String email = req.getParameter("email");
        if (email == null) {
            return;
        }

        String state = req.getParameter("state");
        if (state == null) {
            return;
        }

        email = encodeQueryParam(email);
        state = encodeQueryParam(state);
        String redirectUrl = resp.encodeRedirectURL("/oauth2callback?email=" + email + "&state=" + state);
        resp.sendRedirect(redirectUrl);
    }

}
