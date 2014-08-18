package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;

public class AjaxResult extends ActionResult {

    public PageData data;
    
    public AjaxResult(String destination, AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest,
            List<String> status) {
        super(destination, account, parametersFromPreviousRequest, status);
    }

    //TODO: remove destination parameter and use empty string instead? It is not used for ajax results.
    public AjaxResult(String destination, AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest,
            List<String> status, PageData data) {
        super(destination, account, parametersFromPreviousRequest, status);
        this.data = data;
    }
    
    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        req.setAttribute(Const.ParamsNames.ERROR, ""+isError);        
        clearStatusMessageForRequest(req);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String jsonData = (new Gson()).toJson(data);
        
        resp.getWriter().write(jsonData);    
    } 
    
    private void clearStatusMessageForRequest(HttpServletRequest req) {
        String statusMessageInSession = (String) req.getSession().getAttribute(Const.ParamsNames.STATUS_MESSAGE); 
        if(statusMessageInSession != null){
            //Remove status message in session, thus it becomes an one-time message
            req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGE);
        }
    }
}
