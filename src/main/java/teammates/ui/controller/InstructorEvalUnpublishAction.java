package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorEvalUnpublishAction extends InstructorEvalPageAction {
	
	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		String evalName = getRequestParam(Constants.PARAM_EVALUATION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		logic.unpublishEvaluation(courseId,evalName);
		
		statusToUser.add(Constants.STATUS_EVALUATION_UNPUBLISHED);
		statusToAdmin = "Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
				"for Course <span class=\"bold\">[" + courseId + "]</span> unpublished.";
		
		return createRedirectResult(Constants.ACTION_INSTRUCTOR_EVALS);
	}
	
}
