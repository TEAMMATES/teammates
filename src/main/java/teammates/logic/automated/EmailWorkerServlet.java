package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.Emails;

@SuppressWarnings("serial")
public class EmailWorkerServlet extends WorkerServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        Emails.EmailType typeOfMail = Emails.EmailType.valueOf(HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_TYPE));
        Assumption.assertNotNull(typeOfMail);
        
        EmailAction emailObj = null;
        int responseCode = HttpServletResponse.SC_OK;
        
        log.info("Email worker activated for :" + HttpRequestHelper.printRequestParameters(req));
        
        switch (typeOfMail) {
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
            responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            break;
        }
        
        if (emailObj == null) {
            log.severe("Email object is null");
            responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else {
            emailObj.sendEmails();
        }
        
        resp.setStatus(responseCode);
    }
}
