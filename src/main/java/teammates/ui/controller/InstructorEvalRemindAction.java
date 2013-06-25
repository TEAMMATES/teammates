package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorEvalRemindAction extends InstructorEvalPageAction {
	
	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String evalName = getRequestParam(Common.PARAM_EVALUATION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		logic.sendReminderForEvaluation(courseId,evalName);
		
		statusToUser.add(Common.MESSAGE_EVALUATION_REMINDERSSENT);
		statusToAdmin = "Email sent out to all students who have not completed " +
				"Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
				"of Course <span class=\"bold\">[" + courseId + "]</span>";
		
		return createRedirectResult(Common.PAGE_INSTRUCTOR_EVAL);
	}
	
}
