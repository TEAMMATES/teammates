package teammates.ui.automated;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.TeammatesException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;

/**
 * Receives automated requests from the App Engine server and executes the matching automated action.
 */
@SuppressWarnings("serial")
public class AutomatedServlet extends HttpServlet {
    
    protected static final Logger log = Utils.getLogger();
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            AutomatedAction action = new AutomatedActionFactory().getAction(req);
            
            String url = HttpRequestHelper.getRequestedUrl(req);
            ActivityLogEntry activityLogEntry = new ActivityLogEntry(
                    url, action.getActionDescription(), null, action.getActionMessage(), url);
            log.info(activityLogEntry.generateLogMessage());
            
            action.execute();
        } catch (Exception e) {
            String requestUrl = req.getRequestURL().toString();
            String requestParams = HttpRequestHelper.printRequestParameters(req);
            log.severe("Exception occured while performing " + requestUrl + "|||"
                       + requestParams + "|||" + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
