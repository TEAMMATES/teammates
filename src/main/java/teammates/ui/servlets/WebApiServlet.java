package teammates.ui.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.cloud.datastore.DatastoreException;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.ActionMappingException;
import teammates.common.exception.DeadlineExceededException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.TeammatesException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.LogEvent;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.common.util.TimeHelper;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;
import teammates.ui.webapi.ActionResult;
import teammates.ui.webapi.JsonResult;

/**
 * Servlet that handles all requests from the web application.
 */
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
        int statusCode = 0;
        Action action = null;
        try {
            action = new ActionFactory().getAction(req, req.getMethod());
            action.init(req);
            action.checkAccessControl();

            ActionResult result = action.execute();
            statusCode = result.getStatusCode();
            result.send(resp);
        } catch (ActionMappingException e) {
            statusCode = e.getStatusCode();
            throwErrorBasedOnRequester(req, resp, e, statusCode);
        } catch (InvalidHttpRequestBodyException | InvalidHttpParameterException e) {
            statusCode = HttpStatus.SC_BAD_REQUEST;
            throwErrorBasedOnRequester(req, resp, e, statusCode);
        } catch (UnauthorizedAccessException uae) {
            statusCode = HttpStatus.SC_FORBIDDEN;
            log.warning(uae.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(uae));
            throwError(resp, statusCode,
                    uae.isShowErrorMessage() ? uae.getMessage() : "You are not authorized to access this resource.");
        } catch (EntityNotFoundException enfe) {
            statusCode = HttpStatus.SC_NOT_FOUND;
            log.warning(enfe.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(enfe));
            throwError(resp, statusCode, enfe.getMessage());
        } catch (DeadlineExceededException dee) {
            statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
            log.severe("TimeoutException caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(dee));
            throwError(resp, statusCode, "The request exceeded the server timeout limit. Please try again later.");
        } catch (DatastoreException e) {
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(resp, statusCode, e.getMessage());
        } catch (Throwable t) {
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            log.severe(t.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(t));
            throwError(resp, statusCode,
                    "The server encountered an error when processing your request.");
        } finally {
            long timeElapsed = RequestTracer.getTimeElapsedMillis();
            Map<String, Object> responseDetails = new HashMap<>();
            responseDetails.put("responseStatus", statusCode);
            responseDetails.put("responseTime", timeElapsed);
            putUserInfo(responseDetails, action);

            String logMessage = "%s " + RequestTracer.getTraceId() + " %s with %s in " + timeElapsed + "ms";
            if (action == null) {
                logMessage = String.format(logMessage, "Response", "dispatched", statusCode);
            } else {
                responseDetails.put("actionClass", action.getClass().getSimpleName());
                logMessage = String.format(logMessage, action.getClass().getSimpleName(), "finished", statusCode);
            }

            log.event(LogEvent.RESPONSE_DISPATCHED, logMessage, responseDetails);
        }
    }

    private void throwErrorBasedOnRequester(HttpServletRequest req, HttpServletResponse resp, Exception e, int statusCode)
            throws IOException {
        // The header X-AppEngine-QueueName cannot be spoofed as GAE will strip any user-sent X-AppEngine-QueueName headers.
        // Reference: https://cloud.google.com/tasks/docs/creating-appengine-handlers#reading_app_engine_task_request_headers
        boolean isRequestFromAppEngineQueue = req.getHeader("X-AppEngine-QueueName") != null;

        if (isRequestFromAppEngineQueue) {
            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));

            // Response status is not set to 4XX to 5XX to prevent Cloud Tasks retry mechanism because
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

    private void putUserInfo(Map<String, Object> responseDetails, Action action) {
        if (action == null) {
            return;
        }

        String googleId = action.userInfo == null ? null : action.userInfo.getId();
        String regkey = null;
        String studentEmail = null;
        Optional<StudentAttributes> student = action.getUnregisteredStudent();
        if (student.isPresent()) {
            regkey = student.get().getKey();
            studentEmail = student.get().getEmail();
        }

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("googleId", googleId);
        userInfo.put("regkey", regkey);
        userInfo.put("email", studentEmail);

        responseDetails.put("userInfo", userInfo);
    }

}
