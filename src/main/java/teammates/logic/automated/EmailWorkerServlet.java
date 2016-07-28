package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.HttpRequestHelper;

@SuppressWarnings("serial")
public class EmailWorkerServlet extends WorkerServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        String emailTypeParam = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_TYPE);
        EmailType emailType = EmailType.valueOf(emailTypeParam);
        Assumption.assertNotNull(emailType);
        
        EmailAction emailObj = null;
        int responseCode = HttpServletResponse.SC_OK;
        
        log.info("Email worker activated for :" + HttpRequestHelper.printRequestParameters(req));
        
        switch (emailType) {
        case FEEDBACK_CLOSING:
            emailObj = new FeedbackSessionClosingMailAction(req);
            break;
        case FEEDBACK_OPENING:
            emailObj = new FeedbackSessionOpeningMailAction(req);
            break;
        case FEEDBACK_PUBLISHED:
            emailObj = new FeedbackSessionPublishedMailAction(req);
            break;
        case FEEDBACK_UNPUBLISHED:
            emailObj = new FeedbackSessionUnpublishedMailAction(req);
            break;
        case PENDING_COMMENT_CLEARED:
            emailObj = new PendingCommentClearedMailAction(req);
            break;
        case FEEDBACK_SESSION_REMINDER:
        case NEW_INSTRUCTOR_ACCOUNT:
        case STUDENT_COURSE_JOIN:
        case STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET:
        case INSTRUCTOR_COURSE_JOIN:
        case ADMIN_SYSTEM_ERROR:
        case SEVERE_LOGS_COMPILATION:
            // not sent via email worker
            break;
        default:
            break;
        }
        
        if (emailObj == null) {
            log.severe("Email object is null for type: " + emailTypeParam);
            responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else {
            emailObj.sendEmails();
        }
        
        resp.setStatus(responseCode);
    }
}
