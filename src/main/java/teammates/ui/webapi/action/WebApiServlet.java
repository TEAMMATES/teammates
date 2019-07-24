package teammates.ui.webapi.action;

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

        log.info("Request received: [" + req.getMethod() + "] " + req.getRequestURL().toString()
                + ", Params: " + HttpRequestHelper.getRequestParametersAsString(req)
                + ", Headers: " + HttpRequestHelper.getRequestHeadersAsString(req)
                + ", Request ID: " + Config.getRequestId());

        try {
            Action action = new ActionFactory().getAction(req, req.getMethod());
            action.checkAccessControl();

            ActionResult result = action.execute();
            result.send(resp);
        } catch (ActionMappingException e) {
            throwError(resp, e.getStatusCode(), e.getMessage());
        } catch (InvalidHttpRequestBodyException e) {
            throwError(resp, HttpStatus.SC_BAD_REQUEST, e.getMessage());
        } catch (InvalidHttpParameterException e) {
            log.warning(e.getClass().getSimpleName() + " caught by WebApiServlet: " + e.getMessage());
            throwError(resp, HttpStatus.SC_BAD_REQUEST, "The request is not valid.");
        } catch (UnauthorizedAccessException uae) {
            log.warning(uae.getClass().getSimpleName() + " caught by WebApiServlet: " + uae.getMessage());
            throwError(resp, HttpStatus.SC_FORBIDDEN, "You are not authorized to access this resource.");
        } catch (EntityNotFoundException enfe) {
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

    private void throwError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);
    }

}
