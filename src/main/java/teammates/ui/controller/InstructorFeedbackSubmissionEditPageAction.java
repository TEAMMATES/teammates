package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		FeedbackSessionAttributes session =
				logic.getFeedbackSession(feedbackSessionName, courseId);		
		InstructorAttributes instructor =
				logic.getInstructorForGoogleId(courseId, account.googleId);
		
		// Verify access level
		new GateKeeper().verifyAccessible(instructor, session, false);
		
		// Get login details
		InstructorFeedbackSubmissionEditPageData data = new InstructorFeedbackSubmissionEditPageData(account);
				
		data.bundle = logic.getFeedbackSessionQuestionsBundleForInstructor(feedbackSessionName, courseId, instructor.email);
				
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		statusToAdmin = "Show instructor feedback submission edit page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
	}

}
