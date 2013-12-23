package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackResultsPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		if(courseId==null || feedbackSessionName == null) {
			return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
		}
		
		if(notYetJoinedCourse(courseId, account.googleId)){
			return createPleaseJoinCourseResponse(courseId);
		}
		
		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		StudentFeedbackResultsPageData data = new StudentFeedbackResultsPageData(account);
		
		data.student = logic.getStudentForGoogleId(courseId, account.googleId);
		data.bundle = logic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, data.student.email);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		if (data.bundle.feedbackSession.isPublished() == false) {
			throw new UnauthorizedAccessException(
					"This feedback session is not yet visible.");
		}
		statusToAdmin = "Show student feedback result page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
		return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, data);
	}
}
