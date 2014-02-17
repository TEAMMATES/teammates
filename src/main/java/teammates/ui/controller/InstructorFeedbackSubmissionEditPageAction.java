package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditPageAction extends FeedbackSubmissionEditPageAction {
	@Override
	protected boolean isSpecificUserJoinedCourse() {
		// Instructor is always already joined
		return true;
	}
	
	@Override
	protected void verifyAccesibleForSpecificUser() {
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getFeedbackSession(feedbackSessionName, courseId), false);
	}

	@Override
	protected String getUserEmailForCourse() {
		return logic.getInstructorForGoogleId(courseId, account.googleId).email;
	}

	@Override
	protected FeedbackSessionQuestionsBundle getDataBundle(
			String userEmailForCourse) throws EntityDoesNotExistException {
		return logic.getFeedbackSessionQuestionsBundleForInstructor(
				feedbackSessionName, courseId, userEmailForCourse);
	}

	@Override
	protected void setStatusToAdmin() {
		statusToAdmin = "Show instructor feedback submission edit page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
	}

	@Override
	protected ShowPageResult createSpecificShowPageResult() {
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
	}

}
