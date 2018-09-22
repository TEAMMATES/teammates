package teammates.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.FeedbackSessionNotVisibleException;
import teammates.common.exception.InvalidOriginException;
import teammates.common.exception.InvalidPostParametersException;
import teammates.common.exception.PageNotFoundException;
import teammates.common.exception.TeammatesException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogMessageGenerator;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

/**
 * Receives requests from the Browser, executes the matching action and sends
 * the result back to the Browser. The result can be a page to view or instructions
 * for the Browser to send another request for a different follow up Action.
 */
@SuppressWarnings("serial")
public class ControllerServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void init() {
        TimeHelper.registerResourceZoneRules();
    }

    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    public final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        UserType userType = new GateKeeper().getCurrentUser();
        String url = HttpRequestHelper.getRequestedUrl(req);
        Map<String, String[]> params = HttpRequestHelper.getParameterMap(req);

        try {
            /* We are using the Template Method Design Pattern here.
             * This method contains the high level logic of the request processing.
             * Concrete details of the processing steps are to be implemented by child
             * classes, based on request-specific needs.
             */
            long startTime = System.currentTimeMillis();

            log.info("Request received : [" + req.getMethod() + "] " + req.getRequestURL().toString()
                    + ":" + HttpRequestHelper.printRequestParameters(req));
            log.info("User agent : " + req.getHeader("User-Agent"));

            Action c = new ActionFactory().getAction(req);
            if (c.isValidUser()) {
                ActionResult actionResult = c.executeAndPostProcess();
                actionResult.writeSessionTokenToCookieIfRequired(req, resp);
                actionResult.send(req, resp);
            } else {
                resp.sendRedirect(c.getAuthenticationRedirectUrl());
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            // This is the log message that is used to generate the 'activity log' for the admin.

            log.info(c.getLogMessage() + "|||" + timeTaken);

        } catch (PageNotFoundException e) {
            log.warning(new LogMessageGenerator()
                                .generateActionFailureLogMessage(url, params, e, userType));
            cleanUpStatusMessageInSession(req);
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.ACTION_NOT_FOUND_PAGE, params, url));
        } catch (EntityNotFoundException e) {
            log.warning(new LogMessageGenerator()
                                .generateActionFailureLogMessage(url, params, e, userType));
            cleanUpStatusMessageInSession(req);
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.ENTITY_NOT_FOUND_PAGE, params, url));

        } catch (FeedbackSessionNotVisibleException e) {
            log.warning(new LogMessageGenerator()
                                .generateActionFailureLogMessage(url, params, e, userType));
            cleanUpStatusMessageInSession(req);
            req.getSession().setAttribute(Const.ParamsNames.FEEDBACK_SESSION_NOT_VISIBLE, e.getStartTimeString());
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.FEEDBACK_SESSION_NOT_VISIBLE, params, url));

        } catch (InvalidOriginException e) {
            log.warning(new LogMessageGenerator()
                                .generateActionFailureLogMessage(url, params, e, userType));
            cleanUpStatusMessageInSession(req);
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.INVALID_ORIGIN, params, url));

        } catch (UnauthorizedAccessException e) {
            log.warning(new LogMessageGenerator()
                                .generateActionFailureLogMessage(url, params, e, userType));
            cleanUpStatusMessageInSession(req);
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.UNAUTHORIZED, params, url));

        } catch (DeadlineExceededException | DatastoreTimeoutException e) {
            /*This exception may not be caught because GAE kills
              the request soon after throwing it. In that case, the error
              message in the log will be emailed to the admin by a separate
              cron job.*/
            cleanUpStatusMessageInSession(req);
            log.severe("Deadline exceeded exception caught by ControllerServlet : "
                    + TeammatesException.toStringWithStackTrace(e));
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.DEADLINE_EXCEEDED_ERROR_PAGE, params, url));

        } catch (InvalidPostParametersException e) {
            String requestUrl = req.getRequestURL().toString();
            log.info(e.getMessage());
            cleanUpStatusMessageInSession(req);

            List<StatusMessage> statusMessagesToUser = new ArrayList<>();
            statusMessagesToUser.add(new StatusMessage(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE,
                                                       StatusMessageColor.WARNING));
            req.getSession().setAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST, statusMessagesToUser);

            if (requestUrl.contains("/instructor")) {
                resp.sendRedirect(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
            } else if (requestUrl.contains("/student")) {
                resp.sendRedirect(Const.ActionURIs.STUDENT_HOME_PAGE);
            } else if (requestUrl.contains("/admin")) {
                resp.sendRedirect(Const.ActionURIs.ADMIN_HOME_PAGE);
            } else {
                cleanUpStatusMessageInSession(req);
                resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.ERROR_PAGE, params, url));
            }
        } catch (Throwable t) {
            /* Log only stack trace to prevent delay in termination of request
             * which can result in GAE shutting down the instance.
             * Note that severe logs are sent by email automatically in the cron job auto/compileLogs.
             */
            log.severe("Unexpected exception caught by ControllerServlet : "
                        + TeammatesException.toStringWithStackTrace(t));
            cleanUpStatusMessageInSession(req);
            resp.sendRedirect(appendParamsToErrorPageUrl(Const.ViewURIs.ERROR_PAGE, params, url));
        }

    }

    private void cleanUpStatusMessageInSession(HttpServletRequest req) {
        req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);
    }

    private String appendParamsToErrorPageUrl(String baseUrl, Map<String, String[]> params, String requestUrl) {
        String redirectUrl = baseUrl;
        redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.ERROR_FEEDBACK_URL_REQUESTED, requestUrl);
        redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.REGKEY,
                HttpRequestHelper.getValueFromParamMap(params, Const.ParamsNames.REGKEY));
        redirectUrl = Url.addParamToUrl(redirectUrl,
                Const.ParamsNames.COURSE_ID, HttpRequestHelper.getValueFromParamMap(params, Const.ParamsNames.COURSE_ID));
        redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.STUDENT_EMAIL,
                HttpRequestHelper.getValueFromParamMap(params, Const.ParamsNames.STUDENT_EMAIL));
        return redirectUrl;
    }
}
