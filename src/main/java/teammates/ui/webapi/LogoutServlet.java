package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.api.GateKeeper;

/**
 * Servlet that handles logout.
 */
@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String frontendUrl = req.getParameter("frontendUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }
        String logoutUrl = new GateKeeper().getLogoutUrl(frontendUrl + "/web");
        resp.sendRedirect(logoutUrl);
    }

}
