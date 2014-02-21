package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {
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
	protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
			throws EntityDoesNotExistException {
		return logic.getFeedbackSessionQuestionsBundleForStudent(
				feedbackSessionName, courseId, userEmailForCourse);
	}

	@Override
	protected void setStatusToAdmin() {
		statusToAdmin = "Show student feedback edit result page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" +
				"Course ID: " + courseId;
	}

	@Override
	protected boolean isSessionOpenForSpecificUser() {
		return data.bundle.feedbackSession.isOpened()
				|| data.bundle.feedbackSession.isInGracePeriod();
	}

	@Override
	protected RedirectResult createSpecificRedirectResult() {
		return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
	}
}