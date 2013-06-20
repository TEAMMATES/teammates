package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.GateKeeper;

public class InstructorFeedbackEditPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		new GateKeeper().verifyInstructorUsingOwnIdOrAbove(account.googleId);

		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		if (courseId==null || feedbackSessionName==null) {
			statusToAdmin = "instructorFeedbackEdit Page Redirect<br>"
					+ "Tried to edit feedback session with null parameters";
			return createRedirectResult(Common.PAGE_INSTRUCTOR_FEEDBACK);
		}
		
		InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
		
		data.session = logic.getFeedbackSession(feedbackSessionName, courseId);
		
		if (data.session == null) {
			throw new EntityDoesNotExistException("Feedback session: " +
					feedbackSessionName + "does not exist in course: "
					+ courseId + ".");
		}
		if (data.session.creatorEmail.equals(
				logic.getInstructorForGoogleId(courseId, data.account.googleId).email) == false) {
			throw new UnauthorizedAccessException(
					"Only the creator of the feedback session is " +
							"allowed to edit it");
		}
		
		data.questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		
		statusToAdmin = "instructorFeedbackEdit Page Load<br>"
				+ "Editing information for Feedback Session <span class=\"bold\">["
				+ feedbackSessionName + "]</span>" + "in Course: <span class=\"bold\">" + courseId + "]</span>";
		
		return createShowPageResult(Common.JSP_INSTRUCTOR_FEEDBACK_EDIT, data);
	}

}
