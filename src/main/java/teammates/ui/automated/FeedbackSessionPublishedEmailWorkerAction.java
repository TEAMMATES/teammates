package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * Task queue worker action: prepares session published reminder for a particular session to be sent.
 */
public class FeedbackSessionPublishedEmailWorkerAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return null;
    }
    
    @Override
    protected String getActionMessage() {
        return null;
    }
    
    @Override
    public void execute() {
        String feedbackSessionName = getRequestParamValue(ParamsNames.EMAIL_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        String courseId = getRequestParamValue(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
        
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (session == null) {
            log.severe("Feedback session object for feedback session name: " + feedbackSessionName
                       + " for course: " + courseId + " could not be fetched.");
            return;
        }
        List<EmailWrapper> emailsToBeSent =
                new EmailGenerator().generateFeedbackSessionPublishedEmails(session);
        try {
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
            session.setSentPublishedEmail(true);
            fsLogic.updateFeedbackSession(session);
        } catch (Exception e) {
            log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
