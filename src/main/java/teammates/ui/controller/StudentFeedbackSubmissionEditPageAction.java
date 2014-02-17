package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditPageAction extends FeedbackSubmissionEditPageAction {
	@Override
	protected boolean isSpecificUserJoinedCourse() {
		return !notYetJoinedCourse(courseId, account.googleId);
	}
	
	@Override
	protected void verifyAccesibleForSpecificUser() {
		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
	}

	@Override
	protected String getUserEmailForCourse() {
		return logic.getStudentForGoogleId(courseId, account.googleId).email;
	}

	@Override
	protected FeedbackSessionQuestionsBundle getDataBundle(
			String userEmailForCourse) throws EntityDoesNotExistException {
		return logic.getFeedbackSessionQuestionsBundleForStudent(
				feedbackSessionName, courseId, userEmailForCourse);
	}

	@Override
	protected void setStatusToAdmin() {
		statusToAdmin = "Show student feedback submission edit page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
	}

	@Override
	protected ShowPageResult createSpecificShowPageResult() {
		return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
	}
}
