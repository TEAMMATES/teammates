package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        // Prevent HTTP response splitting
        frontendUrl = resp.encodeRedirectURL(frontendUrl.replace("\r\n", ""));
        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to home page after logging out");
        resp.sendRedirect(frontendUrl + "/web");
    }

}
