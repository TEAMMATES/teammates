package teammates.ui.automated;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * Task queue worker action: sends feedback session reminder email to particular students of a course.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerAction extends AutomatedAction {
    
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
        String feedbackSessionName = getRequestParamValue(ParamsNames.SUBMISSION_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        String courseId = getRequestParamValue(ParamsNames.SUBMISSION_COURSE);
        Assumption.assertNotNull(courseId);
        
        String[] usersToRemind = getRequestParamValues(ParamsNames.SUBMISSION_REMIND_USERLIST);
        
        try {
            FeedbackSessionsLogic.inst()
                    .sendReminderForFeedbackSessionParticularUsers(courseId, feedbackSessionName, usersToRemind);
        } catch (EntityDoesNotExistException e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
