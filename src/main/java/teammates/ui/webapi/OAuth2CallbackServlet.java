package teammates.ui.webapi;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Config;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (Config.isDevServer()) {
            return;
        }

        // TODO
    }

}
