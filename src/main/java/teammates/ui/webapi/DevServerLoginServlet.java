package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Servlet that handles dev server login.
 */
@SuppressWarnings("serial")
public class DevServerLoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServer()) {
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", Const.WebPageURIs.LOGIN);
            return;
        }

        // TODO
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServer()) {
            resp.setStatus(HttpStatus.SC_FORBIDDEN);
            return;
        }

        // TODO
    }

}
