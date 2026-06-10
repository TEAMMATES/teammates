package teammates.ui.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.FileHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.ui.loginmethodhandlers.AuthState;
import teammates.ui.output.LoginMethod;

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

        AuthState state = JsonUtils.fromJson(req.getParameter("state"), AuthState.class);

        String email = req.getParameter("email");
        if (email == null) {
            return;
        }

        String nextUrl = state.getNextUrl();
        if (nextUrl == null) {
            nextUrl = "/";
        }

        LoginMethod method = state.getMethod();
        if (method == null) {
            return;
        }

        email = getEncodedQueryParam(email);
        state = new AuthState(nextUrl, state.getSessionId(), method);
        String redirectUrl = resp.encodeRedirectURL(
                "/oauth2callback?email=" + email
                + "&state=" + getEncodedQueryParam(StringHelper.encrypt(JsonUtils.toJson(state))));
        resp.sendRedirect(redirectUrl);
    }

}
