package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorFeedbackDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
				
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);

		FeedbackSessionAttributes sessionToDelete =
				logic.getFeedbackSession(feedbackSessionName, courseId);
		InstructorAttributes instructorDoingDelete = 
				logic.getInstructorForGoogleId(courseId, account.googleId);
		
		new GateKeeper().verifyAccessible(
				instructorDoingDelete, sessionToDelete);
		
		if (sessionToDelete.creatorEmail.equals(instructorDoingDelete.email) == false) {
			statusToUser.add("Only the creator of this feedback session is" +
					" allowed to delete it.");
			return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS);
		}
		
		logic.deleteFeedbackSession(feedbackSessionName, courseId);
		statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_DELETED);
		statusToAdmin = "Feedback Session <span class=\"bold\">[" + feedbackSessionName + "]</span>" +
				" from Course: <span class=\"bold\">[" + courseId + " deleted.";
		
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS);
	}

}
