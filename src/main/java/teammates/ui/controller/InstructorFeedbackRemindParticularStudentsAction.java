package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackRemindParticularStudentsAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        String[] usersToRemind = getRequestParamValues("usersToRemind");
        if (usersToRemind == null || usersToRemind.length == 0) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT, StatusMessageColor.DANGER));
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
        }
        
        logic.sendReminderForFeedbackSessionParticularUsers(courseId,
                feedbackSessionName, usersToRemind);
        
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT, StatusMessageColor.SUCCESS));
        statusToAdmin = "Email sent out to the selected user(s): ";
        for(String user : usersToRemind){
            statusToAdmin += "<br>" + user;
        }
        statusToAdmin += "<br>in Feedback Session <span class=\"bold\">(" + feedbackSessionName 
                         + ")</span> " + "of Course <span class=\"bold\">[" + courseId + "]</span>";
        
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
    }
}
