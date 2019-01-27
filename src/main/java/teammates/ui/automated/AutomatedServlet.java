package teammates.ui.automated;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;

/**
 * Receives automated requests from the App Engine server and executes the matching automated action.
 */
@SuppressWarnings("serial")
abstract class AutomatedServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    void executeTask(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Pragma", "no-cache");

        String url = HttpRequestHelper.getRequestedUrl(req);
        // Do not log task queue worker actions to prevent excessive logging
        if (!url.startsWith("/worker/")) {
            log.info("Automated request received: [" + req.getMethod() + "] " + req.getRequestURL().toString()
                    + ", Params: " + HttpRequestHelper.getRequestParametersAsString(req)
                    + ", Headers: " + HttpRequestHelper.getRequestHeadersAsString(req));
        }

        try {
            AutomatedAction action = new AutomatedActionFactory().getAction(req, resp);
            action.execute();
        } catch (ActionMappingException | InvalidHttpParameterException e) {
            log.severe(e.getClass().getSimpleName() + " caught by " + getClass().getSimpleName() + ": "
                    + TeammatesException.toStringWithStackTrace(e));

            // Response status is not set to 4XX to 5XX to prevent GAE retry mechanism because
            // if the cause of the exception is improper request URL, no amount of retry is going to help.
            // The action will be inaccurately marked as "success", but the severe log can be used
            // to trace the origin of the problem.
        } catch (DeadlineExceededException | DatastoreTimeoutException e) {
            log.severe(e.getClass().getSimpleName() + " caught by " + getClass().getSimpleName() + ": "
                    + TeammatesException.toStringWithStackTrace(e));

            // Task will be recognised as failed and GAE retry mechanism can kick in
            resp.setStatus(HttpStatus.SC_GATEWAY_TIMEOUT);
        } catch (Throwable t) {
            log.severe(t.getClass().getSimpleName() + " caught by " + getClass().getSimpleName() + ": "
                       + TeammatesException.toStringWithStackTrace(t));

            // Task will be recognised as failed and GAE retry mechanism can kick in
            resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
