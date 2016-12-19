package teammates.ui.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;

/**
 * An automated "action" to be performed by the system, triggered by cron jobs or task queues.
 * Non-administrators are barred from performing this class of action.
 */
public abstract class AutomatedAction {
    
    protected static final Logger log = Logger.getLogger();
    
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    
    protected void initialiseAttributes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    
    protected String getRequestParamValue(String paramName) {
        return HttpRequestHelper.getValueFromRequestParameterMap(request, paramName);
    }
    
    protected String[] getRequestParamValues(String paramName) {
        return HttpRequestHelper.getValuesFromRequestParameterMap(request, paramName);
    }
    
    protected void setForRetry() {
        // Sets an arbitrary retry code outside of the range 200-299 so GAE will automatically retry upon failure
        response.setStatus(100);
    }
    
    protected void setErrorResponse() {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    protected abstract String getActionDescription();
    
    protected abstract String getActionMessage();
    
    public abstract void execute();
    
}
