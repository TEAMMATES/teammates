package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsDownloadAction extends Action {

    @Override
    protected ActionResult execute()  throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(
                instructor,
                session,
                !isCreatorOnly);
        
        String fileContent = "";
        String fileName = "";
        try {
            if(section == null || section.equals("All")){
                fileContent = logic.getFeedbackSessionResultSummaryAsCsv(courseId, feedbackSessionName, instructor.email);
                fileName = courseId + "_" + feedbackSessionName;
                statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName + " in Course " + courseId + " was downloaded";
            } else {
                fileContent = logic.getFeedbackSessionResultSummaryInSectionAsCsv(courseId, feedbackSessionName, instructor.email, section);
                fileName = courseId + "_" + feedbackSessionName + "_" + section;
                statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName + " in Course " + courseId + " within " + section + " was downloaded";
            } 
        } catch (ExceedingRangeException e){
            statusToUser.add("There are too many responses. Please download the feedback results by section");
            isError = true;
            RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
            result.addResponseParam(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
            return result;
        }
        
        return createFileDownloadResult(fileName, fileContent);
    }
}
