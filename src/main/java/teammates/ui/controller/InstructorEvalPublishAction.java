package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorEvalPublishAction extends InstructorEvalPageAction {
	
	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String evalName = getRequestParam(Const.ParamsNames.EVALUATION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		logic.publishEvaluation(courseId,evalName);
		
		statusToUser.add(Const.StatusMessages.EVALUATION_PUBLISHED);
		statusToAdmin = "Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
				"for Course <span class=\"bold\">[" + courseId + "]</span> published.";
		
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS);
	}
	
}
