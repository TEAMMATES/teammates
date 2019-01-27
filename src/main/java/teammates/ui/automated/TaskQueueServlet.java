package teammates.ui.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Receives POST requests from task queue workers and executes the matching actions.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/standard/java/taskqueue/">https://cloud.google.com/appengine/docs/standard/java/taskqueue/</a>
 */
@SuppressWarnings("serial")
public class TaskQueueServlet extends AutomatedServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        // Task queue workers use POST request
        executeTask(req, resp);
    }

}
