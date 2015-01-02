package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackRemindParticularStudentsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(feedbackSessionName);
        
        FeedbackSessionAttributes fsa = logic.getFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);        
        new GateKeeper().verifyAccessible(
                instructor, 
                fsa,
                false);
        
        InstructorFeedbackRemindParticularStudentsPageData data = new InstructorFeedbackRemindParticularStudentsPageData(account);
        data.responseStatus = logic.getFeedbackSessionResponseStatus(feedbackSessionName, courseId);
        
        return createAjaxResult("", data);
    }

}
