package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackPublishAction extends InstructorFeedbacksPageAction {
	
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);
		
		try {
			logic.publishFeedbackSession(feedbackSessionName, courseId);
			
			statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
			statusToAdmin = "Feedback Session <span class=\"bold\">(" + feedbackSessionName + ")</span> " +
					"for Course <span class=\"bold\">[" + courseId + "]</span> published.";
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
	}
	
}
