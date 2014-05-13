package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsDownloadAction extends Action {

    @Override
    protected ActionResult execute()  throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(
                instructor,
                session,
                !isCreatorOnly);
        
        String fileContent = logic.getFeedbackSessionResultSummaryAsCsv(courseId, feedbackSessionName, instructor.email);
        String fileName = courseId + "_" + feedbackSessionName;
        
        statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName + " in Course " + courseId + " was downloaded";
        
        return createFileDownloadResult(fileName, fileContent);
    }
}
