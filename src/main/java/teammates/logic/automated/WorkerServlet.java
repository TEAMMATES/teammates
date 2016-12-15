package teammates.logic.automated;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;

/**
* This class is the parent class for all *WorkerServlets. This class provides logging
* of exceptions that are uncaught by child *WorkerServlet classes. Child classes should
* also perform their own logging of exceptions that could occur during the execution of
* the servlet.
*/
@SuppressWarnings("serial")
public abstract class WorkerServlet extends HttpServlet {
    
    protected static final Logger log = Logger.getLogger();
    
    protected String servletName = "unspecified";
    protected String action = "unspecified";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doGet(req, resp);
        } catch (Exception e) {
            log.severe("Exception occured while performing " + servletName + e.getMessage());
        }
    }
    
    @Override
    public abstract void doGet(HttpServletRequest req, HttpServletResponse resp);
    
    protected void logMessage(HttpServletRequest request, String message) {
        String url = HttpRequestHelper.getRequestedUrl(request);
        ActivityLogEntry activityLogEntry = new ActivityLogEntry(servletName, action, null, message, url);
        log.info(activityLogEntry.generateLogMessage());
    }
}
