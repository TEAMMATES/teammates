package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		// Check for empty parameters
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		statusToAdmin = "Show student feedback edit result page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
		if(courseId==null || feedbackSessionName == null) {
			return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
		}
		
		if(notYetJoinedCourse(courseId, account.googleId)){
			return createPleaseJoinCourseResponse(courseId);
		}
		
		// Verify access level
		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		// Get login details
		FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account);
		
		// Set login email
		String email = logic.getStudentForGoogleId(courseId, account.googleId).email;
		
		data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(feedbackSessionName, courseId, email);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		
		return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
	}

}
