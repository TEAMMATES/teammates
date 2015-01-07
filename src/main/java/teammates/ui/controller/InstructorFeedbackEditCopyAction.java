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
        String copiedFeedbackSessionName = getRequestParamValue("newfsname");
        String[] coursesToCopy = getRequestParamValues("coursesToCopyTo");
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        if (coursesToCopy == null || coursesToCopy.length == 0) {
            statusToUser.add("You have not selected any course to copy the feedback session to");
            RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE);
            redirectResult.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
            redirectResult.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
            redirectResult.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
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
            // @TODO
            statusToAdmin = "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                    "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                    "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                    "<span class=\"bold\">Instructions:</span> " + fs.instructions;
            
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
        } catch (EntityAlreadyExistsException e) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            statusToAdmin = e.getMessage();
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
