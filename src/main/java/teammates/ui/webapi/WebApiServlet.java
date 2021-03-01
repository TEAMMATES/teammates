package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.TeammatesException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;

/**
 * Servlet that handles all requests from the web application.
 */
@SuppressWarnings("serial")
@MultipartConfig
public class WebApiServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void init() {
        TimeHelper.registerResourceZoneRules();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    private void invokeServlet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Pragma", "no-cache");

        String requestParametersAsString;
        try {
            // Make sure that all parameters are valid UTF-8
            requestParametersAsString = HttpRequestHelper.getRequestParametersAsString(req);
        } catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("BadMessageException")) {
                throwErrorBasedOnRequester(req, resp, e, HttpStatus.SC_BAD_REQUEST);
                return;
            }
            throw e;
        }

        log.info("Request received: [" + req.getMethod() + "] " + req.getRequestURL().toString()
                + ", Params: " + requestParametersAsString
                + ", Headers: " + HttpRequestHelper.getRequestHeadersAsString(req)
                + ", Request ID: " + Config.getRequestId());

        if (Config.MAINTENANCE) {
            throwError(resp, HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "The server is currently undergoing some maintenance.");
            return;
        }

        try {
            Action action = new ActionFactory().getAction(req, req.getMethod());
            action.checkAccessControl();

            ActionResult result = action.execute();
            result.send(resp);
        } catch (ActionMappingException e) {
            throwErrorBasedOnRequester(req, resp, e, e.getStatusCode());
        } catch (InvalidHttpRequestBodyException | InvalidHttpParameterException e) {
            throwErrorBasedOnRequester(req, resp, e, HttpStatus.SC_BAD_REQUEST);
        } catch (UnauthorizedAccessException uae) {
            log.warning(uae.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(uae));
            throwError(resp, HttpStatus.SC_FORBIDDEN,
                    uae.isShowErrorMessage() ? uae.getMessage() : "You are not authorized to access this resource.");
        } catch (EntityNotFoundException enfe) {
            log.warning(enfe.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(enfe));
            throwError(resp, HttpStatus.SC_NOT_FOUND, enfe.getMessage());
        } catch (DeadlineExceededException | DatastoreTimeoutException e) {

            // This exception may not be caught because GAE kills the request soon after throwing it
            // In that case, the error message in the log will be emailed to the admin by a separate cron job

            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(resp, HttpStatus.SC_GATEWAY_TIMEOUT,
                    "The request exceeded the server timeout limit. Please try again later.");

        } catch (Throwable t) {
            log.severe(t.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(t));
            throwError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "The server encountered an error when processing your request.");
        }
    }

    private void throwErrorBasedOnRequester(HttpServletRequest req, HttpServletResponse resp, Exception e, int statusCode)
            throws IOException {
        // The header X-AppEngine-QueueName cannot be spoofed as GAE will strip any user-sent X-AppEngine-QueueName headers.
        // Reference: https://cloud.google.com/appengine/docs/standard/java/taskqueue/push/creating-handlers#reading_request_headers
        boolean isRequestFromAppEngineQueue = req.getHeader("X-AppEngine-QueueName") != null;

        if (isRequestFromAppEngineQueue) {
            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));

            // Response status is not set to 4XX to 5XX to prevent GAE retry mechanism because
            // if the cause of the exception is improper request URL, no amount of retry is going to help.
            // The action will be inaccurately marked as "success", but the severe log can be used
            // to trace the origin of the problem.
            throwError(resp, HttpStatus.SC_ACCEPTED, e.getMessage());
        } else {
            log.warning(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(resp, statusCode, e.getMessage());
        }
    }

    private void throwError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);
    }

}
