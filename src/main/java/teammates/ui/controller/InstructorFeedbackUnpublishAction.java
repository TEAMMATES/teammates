package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackUnpublishAction extends InstructorFeedbacksPageAction {
	
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);
		
		try {
			logic.unpublishFeedbackSession(feedbackSessionName, courseId);
		} catch (InvalidParametersException e) {
			Assumption.fail("InvalidParametersException not expected at this point");
		}
		
		statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
		statusToAdmin = "Evaluation <span class=\"bold\">(" + feedbackSessionName + ")</span> " +
				"for Course <span class=\"bold\">[" + courseId + "]</span> unpublished.";
		
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
	}
	
}
