package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;

/**
 * A result that shows a page in the Browser. These are usually implemented as 
 * JSP pages.
 */
public class ShowPageResult extends ActionResult{
    
    /** The data that will be used to render the page*/
    public PageData data;
    
    public ShowPageResult(
            String destination, 
            AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest,
            List<StatusMessage> status) {
        super(destination, account, parametersFromPreviousRequest, status);
    }
    
    public ShowPageResult(
            String destination, 
            AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest,
            PageData data,
            List<StatusMessage> status) {
        super(destination, account, parametersFromPreviousRequest, status);
        this.data = data;
    }


    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        req.setAttribute("data", data); 
        
        /* These two are required for the 'status message' section of the page
         * Although these two are also sent as parameters in the URL,
         *  they should be set as attributes too, because the status message
         *  section is a {@code jsp:include} and cannot see parameters encoded 
         *  in the URL
         */ 
        req.setAttribute(Const.ParamsNames.ERROR, ""+isError);
        
        addStatusMessageToRequest(req);
        req.getRequestDispatcher(getDestinationWithParams()).forward(req, resp);
    }

    private void addStatusMessageToRequest(HttpServletRequest req) {
        String statusMessageInSession = (String) req.getSession().getAttribute(Const.ParamsNames.STATUS_MESSAGE); 
        String statusMessageColor = (String) req.getSession().getAttribute(Const.ParamsNames.STATUS_MESSAGE_COLOR);
        
        if(statusMessageInSession != null && !statusMessageInSession.isEmpty()){
            //Remove status message in session, thus it becomes an one-time message
            req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGE);            
            req.setAttribute(Const.ParamsNames.STATUS_MESSAGE, statusMessageInSession);
        } else {
            req.setAttribute(Const.ParamsNames.STATUS_MESSAGE, "");
        }
        
        if(statusMessageColor != null && !statusMessageColor.isEmpty()){
            req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGE_COLOR);
            req.setAttribute(Const.ParamsNames.STATUS_MESSAGE_COLOR, statusMessageColor);
        } else {
            req.setAttribute(Const.ParamsNames.STATUS_MESSAGE_COLOR, "info");
        }
    }
}
