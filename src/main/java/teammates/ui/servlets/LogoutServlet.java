package teammates.ui.servlets;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

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

        String frontendUrl = req.getParameter("frontendUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }
        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to home page after logging out");
        resp.sendRedirect(frontendUrl + "/web");
    }

}
