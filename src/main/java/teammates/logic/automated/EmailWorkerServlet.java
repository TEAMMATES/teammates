package teammates.logic.automated;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seleniumhq.jetty7.server.Response;

import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.Emails;

@SuppressWarnings("serial")
public class EmailWorkerServlet extends WorkerServlet {
    
    private static Logger log = Utils.getLogger();
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        Emails.EmailType typeOfMail = Emails.EmailType.valueOf(HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_TYPE));
        Assumption.assertNotNull(typeOfMail);
        
        EmailAction emailObj = null;
        int responseCode = Response.SC_OK;
        
        log.info("Email worker activated for :"+ HttpRequestHelper.printRequestParameters(req));
        
        switch(typeOfMail) {
            case FEEDBACK_CLOSING:
                emailObj = new FeedbackSessionClosingMailAction(req);
                break;
            case FEEDBACK_OPENING:
                emailObj = new FeedbackSessionOpeningMailAction(req);
                break;
            case FEEDBACK_PUBLISHED:
                emailObj = new FeedbackSessionPublishedMailAction(req);
                break;
            case PENDING_COMMENT_CLEARED:
                emailObj = new PendingCommentClearedMailAction(req);
                break;
            default:
                log.severe("Type of email is null");
                responseCode = Response.SC_INTERNAL_SERVER_ERROR;
        }
        
        if(emailObj != null) {
            emailObj.sendEmails();
        } else {
            log.severe("Email object is null");
            responseCode = Response.SC_INTERNAL_SERVER_ERROR;
        }
        
        resp.setStatus(responseCode);
    }
}    
