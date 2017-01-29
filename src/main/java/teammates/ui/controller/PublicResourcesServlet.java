package teammates.ui.controller;

import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.logic.api.GateKeeper;

/** 
 * Serves the public resources stored in google cloud storage using the blobkey.<br>
 * Correct blobkey is required for image serving.
 */
@SuppressWarnings("serial")
public abstract class PublicResourcesServlet extends HttpServlet {
    
    protected static final Logger log = Logger.getLogger();
    
    protected String servletName = "Unspecified";
    protected String action = "unspecified";
    
    /** Parameters received with the request */
    protected Map<String, String[]> requestParameters;

    @Override
    public abstract void doGet(HttpServletRequest req, HttpServletResponse resp);

    @Override
    public final void doPost(HttpServletRequest req, HttpServletResponse resp) {
              
        try {
            doGet(req, resp);
        } catch (Exception e) {
            log.severe("Exception occured while performing " + servletName + e.getMessage());
        }
        
    }

    protected String getBlobKeyFromRequest() {
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY, blobKey);
        return blobKey;
    }
    
    protected String getRequestParamValue(String paramName) {
        return HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
    }
    
    protected void logMessage(HttpServletRequest request, String message) {
        UserType userType = new GateKeeper().getCurrentUser();
        String url = HttpRequestHelper.getRequestedUrl(request);
        ActivityLogEntry activityLogEntry = new ActivityLogEntry(servletName, action, null, message, url, userType);
        log.info(activityLogEntry.generateLogMessage());
    }
}
