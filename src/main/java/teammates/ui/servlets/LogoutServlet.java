package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.Logger;

/**
 * Servlet that handles logout.
 */
public class LogoutServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();

        Cookie cookie = getLoginInvalidationCookie();
        resp.addCookie(cookie);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to home page after logging out");
        resp.sendRedirect(Config.APP_FRONTEND_URL + "/web");
    }

}
