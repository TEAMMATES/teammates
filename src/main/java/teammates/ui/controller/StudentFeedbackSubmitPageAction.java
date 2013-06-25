package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class StudentFeedbackSubmitPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		// Check for empty parameters
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		if(courseId==null || feedbackSessionName == null) {
			return createRedirectResult(Common.PAGE_STUDENT_HOME);
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
		String email;
		if (account.isInstructor) {
			// Currently allowing the course owner to access student feedback submit page as well.
			email = account.email;
		} else {
			// Get student email instead of account email which may be different.
			email = logic.getStudentForGoogleId(courseId, account.googleId).email;
		}
		
		data.bundle = logic.getFeedbackSessionQuestionsBundle(feedbackSessionName, courseId, email);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		
		return createShowPageResult(Common.JSP_STUDENT_FEEDBACK_SUBMIT, data);
	}

}
