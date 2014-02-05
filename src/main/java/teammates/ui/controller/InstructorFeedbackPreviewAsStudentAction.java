package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackPreviewAsStudentAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		String studentEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PREVIEWAS);
		statusToAdmin = "Preview feedback session as student (" + studentEmail + ")<br>" +
				"Session Name: " + feedbackSessionName + "<br>" +
				"Course ID: " + courseId;

		if (courseId == null || feedbackSessionName == null	|| studentEmail == null) {
			Assumption.fail();
		}

		new GateKeeper().verifyAccessible(
			logic.getInstructorForGoogleId(courseId, account.googleId),
			logic.getFeedbackSession(feedbackSessionName, courseId),
			true);

		// Get student login detail
		StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
		FeedbackSubmissionEditPageData data;
		if (!student.googleId.isEmpty()) {
			AccountAttributes account = logic.getAccount(student.googleId);
			data = new FeedbackSubmissionEditPageData(account);
		} else {
			data = new FeedbackSubmissionEditPageData(student.name, studentEmail);
		}
		
		data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(
				feedbackSessionName, courseId, studentEmail);

		if (data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "
					+ feedbackSessionName + " does not exist in " + courseId
					+ ".");
		}

		data.isPreview = true;
		data.bundle.resetAllResponses();

		return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
	}

}
