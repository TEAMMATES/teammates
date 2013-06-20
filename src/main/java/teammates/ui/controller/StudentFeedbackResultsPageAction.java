package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class StudentFeedbackResultsPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		if(courseId==null || feedbackSessionName == null) {
			return createRedirectResult(Common.PAGE_STUDENT_HOME);
		}
		
		new GateKeeper().verifyCourseOwnerOrStudentInCourse(courseId);

		StudentFeedbackResultsPageData data = new StudentFeedbackResultsPageData(account);
		data.student = logic.getStudentForGoogleId(courseId, account.googleId);
		
		String email;		
		if(data.student == null) {
			// Currently allowing the course owner to access student feedback results page.
			email = account.email;
		} else {
			// Get student email instead of account email which may be different.
			email = data.student.email;
		}
		data.bundle = logic.getFeedbackSessionResultsForUser(feedbackSessionName, courseId, email);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		
		return createShowPageResult(Common.JSP_STUDENT_FEEDBACK_RESULTS, data);
	}
}
