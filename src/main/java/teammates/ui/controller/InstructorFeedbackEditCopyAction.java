package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

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
        String[] coursesIdToCopyTo = getRequestParamValues(Const.ParamsNames.COPIED_COURSES_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull("null course id", courseId);
        Assumption.assertNotNull("null fs name", feedbackSessionName);
        Assumption.assertNotNull("null copied fs name", copiedFeedbackSessionName);

        
        if (coursesIdToCopyTo == null || coursesIdToCopyTo.length == 0) {
            return redirectWithErrorMsg(feedbackSessionName, courseId, Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        }
        
        Assumption.assertNotNull(copiedFeedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
                
        
        try {
            List<String> coursesWhichCopyFails = checkForExistingFeedbackSessions(
                    copiedFeedbackSessionName, coursesIdToCopyTo);
            
            if (!coursesWhichCopyFails.isEmpty()) {
                String error = "For measuring failure rate: user tried to copy session to multiple courses.";
                error += "Name of Session: " + copiedFeedbackSessionName; 
                for (String course : coursesWhichCopyFails) {
                    error += "Copying to " + course + " failed";
                }
                
                log.severe(error);
                
                String coursesWithSameNameFs = "";
                String delim = "";
                for (String courseWhichCopyFails : coursesWhichCopyFails) {
                    coursesWithSameNameFs += delim + courseWhichCopyFails ;
                    delim = ", ";
                }
                
                return redirectWithErrorMsg(feedbackSessionName, courseId, String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS, copiedFeedbackSessionName, coursesWithSameNameFs));
            }
            
            FeedbackSessionAttributes fs = null;
            // TODO: consider doing this as a batch insert
            for (String newCourseId : coursesIdToCopyTo) {
                fs = logic.copyFeedbackSession(copiedFeedbackSessionName, newCourseId, feedbackSessionName, courseId, instructor.email);
            }
            
            
            String adminListOfMsg = "";
            String delim = "";
            for (String newCourseId : coursesIdToCopyTo) {
                adminListOfMsg += delim + newCourseId ;
                delim = ", ";
            }
            
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
            statusToAdmin = "Copying to multiple feedback sessions.<br>" +
                            "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Courses: <br>" +
                            adminListOfMsg + "<br>" +
                            "<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                            "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                            "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                            "<span class=\"bold\">Instructions:</span> " + fs.instructions + "<br>" +
                            "Copied from <span class=\"bold\">(" + feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";

            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
            
        } catch (EntityAlreadyExistsException e) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            statusToAdmin = e.getMessage();
            
            isError = true;
            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        
        return redirectWithError(feedbackSessionName, courseId);
    }


    private List<String> checkForExistingFeedbackSessions(
            String copiedFeedbackSessionName, String[] coursesIdToCopyTo) {
        List<String> coursesWhichCopyFails = new ArrayList<String>();
        
        for (String courseIdToCopy: coursesIdToCopyTo) {
            FeedbackSessionAttributes existingFs = logic.getFeedbackSession(copiedFeedbackSessionName, courseIdToCopy);
            boolean fsAlreadyExists = (existingFs != null);
            
            if (fsAlreadyExists) {
                coursesWhichCopyFails.add(existingFs.courseId);
            }
        }
        return coursesWhichCopyFails;
    }
    
    
    private RedirectResult redirectWithError(String feedbackSessionName,
            String courseId) {
        isError = true;
        
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE);
        redirectResult.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
        redirectResult.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        redirectResult.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
        
        return redirectResult;
    }

    private RedirectResult redirectWithErrorMsg(String feedbackSessionName,
            String courseId, String errorToUser) {
        statusToUser.add(errorToUser);
        return redirectWithError(feedbackSessionName, courseId);
    }

}
