package teammates.ui.controller;

import java.util.HashMap;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditCopyAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {        
        String copiedFeedbackSessionName = getRequestParamValue(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        String[] coursesToCopy = getRequestParamValues(Const.ParamsNames.COPIED_COURSES_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull("null course id", courseId);
        Assumption.assertNotNull("null fs name", feedbackSessionName);
        Assumption.assertNotNull("null copied fs name", copiedFeedbackSessionName);

        
        if (coursesToCopy == null || coursesToCopy.length == 0) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
            
            RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE);
            redirectResult.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
            redirectResult.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
            redirectResult.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
            
            isError = true;
            return redirectResult;
        }
        Assumption.assertNotNull(copiedFeedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
                
        
        try {
            FeedbackSessionAttributes fs = logic.copyMultipleFeedbackSession(copiedFeedbackSessionName, coursesToCopy, feedbackSessionName, courseId, instructor.email);
            
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
            statusToAdmin = "Copying to multiple feedback sessions.<br>" +
                    "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                    "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                    "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                    "<span class=\"bold\">Instructions:</span> " + fs.instructions + "<br>" +
                    "Copied from <span class=\"bold\">(" + feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";

            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
        } catch (EntityAlreadyExistsException e) {
            FeedbackSessionAttributes fs = (FeedbackSessionAttributes)e.getOffendingEntity();
            
            statusToUser.add(String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS, 
                                           fs.feedbackSessionName, fs.courseId));
            statusToAdmin = e.getMessage();
            log.severe("Instructor failed to copy " + feedbackSessionName + " from " + courseId + " to " + copiedFeedbackSessionName + " in " + fs.courseId);
            
            isError = true;            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
    
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE);
        redirectResult.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
        redirectResult.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        redirectResult.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
        
        return redirectResult;
    }

}
