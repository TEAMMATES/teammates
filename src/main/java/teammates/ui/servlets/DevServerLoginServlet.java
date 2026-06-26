package teammates.ui.servlets;

import static teammates.common.util.UrlHelper.encodeQueryParam;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.Const;
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

        String email = req.getParameter(Const.ParamsNames.EMAIL);
        String state = req.getParameter(Const.ParamsNames.AUTH_STATE);
        if (email == null || state == null) {
            resp.sendError(HttpStatus.SC_BAD_REQUEST);
            return;
        }

        email = encodeQueryParam(email);
        state = encodeQueryParam(state);
        String redirectUrl = resp.encodeRedirectURL("/oauth2callback?"
                + Const.ParamsNames.EMAIL + "=" + email + "&"
                + Const.ParamsNames.AUTH_STATE + "=" + state);
        resp.sendRedirect(redirectUrl);
    }

}
