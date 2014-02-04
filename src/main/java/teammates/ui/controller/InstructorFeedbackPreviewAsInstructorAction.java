package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackPreviewAsInstructorAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		String previewInstructorEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PREVIEWAS);
		statusToAdmin = "Preview feedback session as instructor (" + previewInstructorEmail + ")<br>" +
				"Session Name: " + feedbackSessionName + "<br>" +
				"Course ID: " + courseId;
		
		if (courseId == null || feedbackSessionName == null	|| previewInstructorEmail == null) {
			Assumption.fail();
		}

		// Verify access level
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);

		// Get login details
		InstructorAttributes previewInstructor = logic.getInstructorForEmail(courseId, previewInstructorEmail);
		FeedbackSubmissionEditPageData data;
		if (!previewInstructor.googleId.isEmpty()) {
			AccountAttributes account = logic.getAccount(previewInstructor.googleId);
			data = new FeedbackSubmissionEditPageData(account);
		} else {
			data = new FeedbackSubmissionEditPageData(previewInstructor.name, previewInstructor.email);
		}

		data.bundle = logic.getFeedbackSessionQuestionsBundleForInstructor(
				feedbackSessionName, courseId, previewInstructor.email);

		if (data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "
					+ feedbackSessionName + " does not exist in " + courseId
					+ ".");
		}
		
		data.isPreview = true;
		data.bundle.resetAllResponses();
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
	}

}
