package teammates.ui.newcontroller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.InvalidPostParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;

/**
 * Servlet that handles all requests from the web application.
 */
@SuppressWarnings("serial")
public class WebApiServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void init() {
        TimeHelper.registerResourceZoneRules();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    private void invokeServlet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");

        @SuppressWarnings("PMD.PrematureDeclaration") // used to measure response time
        long startTime = System.currentTimeMillis();

        log.info("Request received: [" + req.getMethod() + "] " + req.getRequestURL().toString()
                + ", Params: " + HttpRequestHelper.getRequestParametersAsString(req)
                + ", Headers: " + HttpRequestHelper.getRequestHeadersAsString(req));

        Action action;
        try {
            action = new ActionFactory().getAction(req);
        } catch (ActionMappingException e) {
            throwError(resp, e.getStatusCode(), e.getMessage());
            return;
        }

        boolean passAccessControlCheck = action.checkAccessControl();
        if (!passAccessControlCheck) {
            throwError(resp, 403, "Not authorized to access this resource.");
            return;
        }

        try {
            ActionResult result = action.execute(resp);
            result.send(resp);
            // TODO handle all sorts of Exceptions

            long timeTaken = System.currentTimeMillis() - startTime;

            log.info(action.getLogMessage() + "|||" + timeTaken);
        } catch (InvalidPostParametersException ippe) {
            throwError(resp, 400, ippe.getMessage());
        } catch (DeadlineExceededException | DatastoreTimeoutException e) {

            // This exception may not be caught because GAE kills the request soon after throwing it
            // In that case, the error message in the log will be emailed to the admin by a separate cron job

            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(resp, 408, e.getMessage());

        } catch (Throwable t) {
            log.severe(t.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(t));
            throwError(resp, 500, t.getMessage());
        }
    }

    private void throwError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);
    }

}
