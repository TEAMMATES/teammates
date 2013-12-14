package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsDownloadAction extends Action {

	@Override
	protected ActionResult execute()  throws EntityDoesNotExistException {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
		
		new GateKeeper().verifyAccessible(
				instructor,
				logic.getFeedbackSession(feedbackSessionName, courseId),
				false);
		
		String fileContent = logic.getFeedbackSessionResultSummaryAsCsv(courseId, feedbackSessionName, instructor.email);
		String fileName = courseId + "_" + feedbackSessionName;
		
		statusToAdmin = "Summary data for Feedback Session "+ feedbackSessionName + " in Course "+courseId + " was downloaded";
		
		return createFileDownloadResult(fileName, fileContent);
	}

}
