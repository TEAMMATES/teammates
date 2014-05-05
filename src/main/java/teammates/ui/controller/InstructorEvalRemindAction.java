package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalRemindAction extends InstructorEvalsPageAction {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getEvaluation(courseId, evalName));
        
        logic.sendReminderForEvaluation(courseId,evalName);
        
        statusToUser.add(Const.StatusMessages.EVALUATION_REMINDERSSENT);
        statusToAdmin = "Email sent out to all students who have not completed " +
                "Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
                "of Course <span class=\"bold\">[" + courseId + "]</span>";
        
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE);
    }
    
}
