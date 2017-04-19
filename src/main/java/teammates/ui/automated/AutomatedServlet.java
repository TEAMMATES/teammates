package teammates.ui.automated;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.exception.TeammatesException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.logic.api.GateKeeper;

/**
 * Receives automated requests from the App Engine server and executes the matching automated action.
 */
@SuppressWarnings("serial")
public class AutomatedServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            AutomatedAction action = new AutomatedActionFactory().getAction(req, resp);
            UserType userType = new GateKeeper().getCurrentUser();

            String url = HttpRequestHelper.getRequestedUrl(req);
            // Do not log task queue worker actions to prevent excessive logging
            if (!url.startsWith("/worker/")) {
                ActivityLogEntry activityLogEntry = new ActivityLogEntry(
                        url, action.getActionDescription(), null, action.getActionMessage(), url, userType);
                log.info(activityLogEntry.generateLogMessage());
            }

            action.execute();
        } catch (Exception e) {
            String requestUrl = req.getRequestURL().toString();
            String requestParams = HttpRequestHelper.printRequestParameters(req);
            log.severe("Exception occured while performing " + requestUrl + "|||"
                       + requestParams + "|||" + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
