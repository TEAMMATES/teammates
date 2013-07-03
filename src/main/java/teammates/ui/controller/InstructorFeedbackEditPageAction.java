package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorFeedbackEditPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		

		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Constants.PARAM_FEEDBACK_SESSION_NAME);
		
		if (courseId==null || feedbackSessionName==null) {
			statusToAdmin = "instructorFeedbackEdit Page Redirect<br>"
					+ "Tried to edit feedback session with null parameters";
			return createRedirectResult(Constants.ACTION_INSTRUCTOR_FEEDBACKS);
		}
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
		
		data.session = logic.getFeedbackSession(feedbackSessionName, courseId);
		
		if (data.session == null) {
			throw new EntityDoesNotExistException("Feedback session: " +
					feedbackSessionName + "does not exist in course: "
					+ courseId + ".");
		}
		if (data.session.creatorEmail.equals(
				logic.getInstructorForGoogleId(courseId, data.account.googleId).email) == false) {
			statusToUser.add("Only the creator of the feedback session is" +
					" allowed to edit it.");
			return createRedirectResult(Constants.ACTION_INSTRUCTOR_FEEDBACKS);
		}
		
		data.questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		
		statusToAdmin = "instructorFeedbackEdit Page Load<br>"
				+ "Editing information for Feedback Session <span class=\"bold\">["
				+ feedbackSessionName + "]</span>" + "in Course: <span class=\"bold\">" + courseId + "]</span>";
		
		return createShowPageResult(Constants.VIEW_INSTRUCTOR_FEEDBACK_EDIT, data);
	}

}
