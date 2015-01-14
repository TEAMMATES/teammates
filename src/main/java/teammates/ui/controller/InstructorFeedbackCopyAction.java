package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackCopyAction extends InstructorFeedbacksPageAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String copiedFeedbackSessionName = getRequestParamValue(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        String copiedCourseId = getRequestParamValue(Const.ParamsNames.COPIED_COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull(copiedFeedbackSessionName);
        Assumption.assertNotNull(copiedCourseId);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
                
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        
        try {
            FeedbackSessionAttributes fs = logic.copyFeedbackSession(copiedFeedbackSessionName, copiedCourseId, feedbackSessionName, courseId, instructor.email);
            data.newFeedbackSession = fs;
            
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
            statusToAdmin = "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                    "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                    "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                    "<span class=\"bold\">Instructions:</span> " + fs.instructions;
            
            //TODO: add a condition to include the status due to inconsistency problem of database 
            //      (similar to the one below)
            return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(fs.courseId,fs.feedbackSessionName));
            
        } catch (EntityAlreadyExistsException e) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            statusToAdmin = e.getMessage();
            isError = true;
            
        } catch (InvalidParametersException e) {
            // updates isError attribute
            setStatusForException(e);
        } 
        
        // if isError == true, (an exception occurred above)


        boolean omitArchived = true;
        data.instructors = loadCourseInstructorMap(omitArchived);
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>(data.instructors.values());
        data.courses = loadCoursesList(instructorList);
        data.existingEvalSessions = loadEvaluationsList(instructorList);
        data.existingFeedbackSessions = loadFeedbackSessionsList(instructorList);
        
        if (data.existingFeedbackSessions.size() == 0) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY);
        }
    
        EvaluationAttributes.sortEvaluationsByDeadlineDescending(data.existingEvalSessions);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(data.existingFeedbackSessions);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }

}
