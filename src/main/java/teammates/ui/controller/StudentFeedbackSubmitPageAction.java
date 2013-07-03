package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class StudentFeedbackSubmitPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		// Check for empty parameters
		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Constants.PARAM_FEEDBACK_SESSION_NAME);
		
		if(courseId==null || feedbackSessionName == null) {
			return createRedirectResult(Constants.ACTION_STUDENT_HOME);
		}
		
		if(notYetJoinedCourse(courseId, account.googleId)){
			return createPleaseJoinCourseResponse(courseId);
		}
		
		// Verify access level
		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		// Get login details
		StudentFeedbackSubmitPageData data = new StudentFeedbackSubmitPageData(account);
		
		// Set login email
		String email = logic.getStudentForGoogleId(courseId, account.googleId).email;
		
		data.bundle = logic.getFeedbackSessionQuestionsBundle(feedbackSessionName, courseId, email);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		if (data.bundle.feedbackSession.isVisible() == false) {
			throw new UnauthorizedAccessException(
					"This feedback session is not yet visible.");
		}
		
		return createShowPageResult(Constants.VIEW_STUDENT_FEEDBACK_SUBMIT, data);
	}

}
