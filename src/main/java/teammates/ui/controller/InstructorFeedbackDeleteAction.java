package teammates.ui.controller;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorFeedbackDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
				
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);

		new GateKeeper().verifyCourseInstructorOrAbove(courseId);

		logic.deleteFeedbackSession(feedbackSessionName, courseId);
		statusToUser.add(Common.MESSAGE_FEEDBACK_SESSION_DELETED);
		statusToAdmin = "Feedback Session <span class=\"bold\">[" + feedbackSessionName + "]</span>" +
				" from Course: <span class=\"bold\">[" + courseId + " deleted.";
		
		return createRedirectResult(Common.JSP_INSTRUCTOR_FEEDBACK);
	}

}
