package teammates.ui.controller;

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
		String paramStudentEmail = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
		if (courseId == null || feedbackSessionName == null	|| paramStudentEmail == null) {
			Assumption.fail();
		}

		new GateKeeper().verifyAccessible(
			logic.getInstructorForGoogleId(courseId, account.googleId),
			logic.getFeedbackSession(feedbackSessionName, courseId),
			true);
		
		StudentAttributes previewStudent = logic.getStudentForEmail(courseId, paramStudentEmail);
		
		FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account);
		
		data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(
				feedbackSessionName, courseId, previewStudent.email);
		if (data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "
					+ feedbackSessionName + " does not exist in " + courseId
					+ ".");
		}
		
		data.isPreview = true;
		data.previewStudent = previewStudent;
		data.bundle.resetAllResponses();

		statusToAdmin = "Preview feedback session as student (" + previewStudent.email + ")<br>" +
				"Session Name: " + feedbackSessionName + "<br>" +
				"Course ID: " + courseId;
		
		return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
	}
}
