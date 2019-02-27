package teammates.ui.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Receives GET requests from cron job scheduler and executes the matching actions.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/standard/java/config/cron">https://cloud.google.com/appengine/docs/standard/java/config/cron</a>
 */
@SuppressWarnings("serial")
public class CronJobServlet extends AutomatedServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        // Cron job schedulers use GET request
        executeTask(req, resp);
    }

}
