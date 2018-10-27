package teammates.ui.automated;

import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.exception.TeammatesException;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogMessageGenerator;
import teammates.common.util.Logger;

/**
 * Receives automated requests from the App Engine server and executes the matching automated action.
 */
@SuppressWarnings("serial")
abstract class AutomatedServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    void executeTask(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (req.getParameterNames().hasMoreElements()) {
                log.info(HttpRequestHelper.getRequestParametersAsString(req));
            }

            AutomatedAction action = new AutomatedActionFactory().getAction(req, resp);

            String url = HttpRequestHelper.getRequestedUrl(req);
            // Do not log task queue worker actions to prevent excessive logging
            if (!url.startsWith("/worker/")) {
                @SuppressWarnings("unchecked")
                Map<String, String[]> params = req.getParameterMap();
                // no logged-in user for automated servlet
                LogMessageGenerator logGenerator = new LogMessageGenerator();
                log.info(logGenerator.generateBasicActivityLogMessage(url, params, action.getActionMessage(), null));
            }

            action.execute();
        } catch (Throwable t) {
            String requestUrl = req.getRequestURL().toString();
            log.severe("Exception occured while performing " + requestUrl + "|||"
                       + TeammatesException.toStringWithStackTrace(t));
            // So task will be recognised as failed and GAE retry mechanism can kick in
            resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
