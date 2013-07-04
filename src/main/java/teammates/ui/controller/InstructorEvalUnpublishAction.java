package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorEvalUnpublishAction extends InstructorEvalsPageAction {
	
	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String evalName = getRequestParam(Const.ParamsNames.EVALUATION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		logic.unpublishEvaluation(courseId,evalName);
		
		statusToUser.add(Const.StatusMessages.EVALUATION_UNPUBLISHED);
		statusToAdmin = "Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
				"for Course <span class=\"bold\">[" + courseId + "]</span> unpublished.";
		
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE);
	}
	
}
