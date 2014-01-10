package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {
	@Override
	protected void verifyAccesibleForSpecificUser() {
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getFeedbackSession(feedbackSessionName, courseId),
				false);
	}

	@Override
	protected String getUserEmailForCourse() {
		return logic.getInstructorForGoogleId(courseId, account.googleId).email;
	}

	@Override
	protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
			throws EntityDoesNotExistException {
		return logic.getFeedbackSessionQuestionsBundleForInstructor(
				feedbackSessionName, courseId, userEmailForCourse);
	}

	@Override
	protected void setStatusToAdmin() {
		statusToAdmin = "Show instructor feedback submission edit&save page<br>"
				+
				"Session Name: " + feedbackSessionName + "<br>" +
				"Course ID: " + courseId;
	}

	@Override
	protected boolean isSessionOpenForSpecificUser() {
		if (data.bundle.feedbackSession.isOpened() == false &&
				data.bundle.feedbackSession.isPrivateSession() == false &&
				data.bundle.feedbackSession.isInGracePeriod() == false) {
			return false;
		}
		return true;
	}

	@Override
	protected RedirectResult createSpecificRedirectResult() {
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
	}
}