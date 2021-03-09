package teammates.ui.webapi;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;

/**
 * Servlet that handles login.
 */
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (Config.isDevServer()) {
            resp.setStatus(HttpStatus.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", "/devServerLogin");
            return;
        }

        // TODO
    }

}
