package teammates.logic.automated;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;

@SuppressWarnings("serial")
public abstract class AutomatedRemindersServlet extends HttpServlet{
    
    protected static Logger log = Utils.getLogger();
    
    protected String servletName = "unspecified";
    protected String action = "unspecified";
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doGet(req, resp);
        } catch(Exception e) {
            log.severe("Exception occured while performing " + servletName + e.getMessage());
        }
    }
    
    public abstract void doGet(HttpServletRequest req, HttpServletResponse resp);
    
    protected void logMessage(HttpServletRequest request, String message) {
        String url = HttpRequestHelper.getRequestedURL(request);
        ActivityLogEntry activityLogEntry = new ActivityLogEntry(servletName, action, null, message, url);
        log.log(Level.INFO, activityLogEntry.generateLogMessage());
    }
}
