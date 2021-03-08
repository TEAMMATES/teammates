package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles logout.
 */
@SuppressWarnings("serial")
public class LogoutServlet extends AuthServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();

        Cookie cookie = getLoginInvalidationCookie();
        resp.addCookie(cookie);

        String frontendUrl = req.getParameter("frontendUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }
        resp.sendRedirect(frontendUrl + "/web");
    }

}
