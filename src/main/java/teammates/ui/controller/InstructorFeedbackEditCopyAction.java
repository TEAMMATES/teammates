package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditCopyAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {        
        String newFeedbackSessionName = getRequestParamValue(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        String[] coursesIdToCopyTo = getRequestParamValues(Const.ParamsNames.COPIED_COURSES_ID);
        String feedbackSessionNameFrom = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String courseIdFrom = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull("null course id", courseIdFrom);
        Assumption.assertNotNull("null fs name", feedbackSessionNameFrom);
        Assumption.assertNotNull("null copied fs name", newFeedbackSessionName);

        
        if (coursesIdToCopyTo == null || coursesIdToCopyTo.length == 0) {
            return createRedirectToEditPageWithErrorMsg(feedbackSessionNameFrom, courseIdFrom, Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        }
        
        Assumption.assertNotNull(newFeedbackSessionName);
        Assumption.assertNotNull(courseIdFrom);
        Assumption.assertNotNull(feedbackSessionNameFrom);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseIdFrom, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getCourse(courseIdFrom), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        try {
            // Check if there are no conflicting feedback sessions in all the courses 
            List<String> conflictCourse = filterConflictsInCourses(
                    newFeedbackSessionName, coursesIdToCopyTo);
            
            if (!conflictCourse.isEmpty()) {
                String errorToAdmin = "For measuring failure rate: user tried to copy session to multiple courses.";
                errorToAdmin += "Name of Session: " + newFeedbackSessionName + "<br>"; 
                                               
                errorToAdmin += "Copying to course(s) " + conflictCourse.toString() + " failed.";
                log.severe(errorToAdmin);
                                
                String commaSeparatedListOfCourses = StringHelper.toString(conflictCourse, ",");
                String errorToUser = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS, newFeedbackSessionName, commaSeparatedListOfCourses);
                
                return createRedirectToEditPageWithErrorMsg(feedbackSessionNameFrom, courseIdFrom, errorToUser);
            }
            
            // Copy the feedback sessions
            FeedbackSessionAttributes fs = null;
            // TODO: consider doing this as a batch insert
            for (String courseIdToCopyTo : coursesIdToCopyTo) {
                fs = logic.copyFeedbackSession(newFeedbackSessionName, courseIdToCopyTo, feedbackSessionNameFrom, courseIdFrom, instructor.email);
            }
            
            List<String> courses = Arrays.asList(coursesIdToCopyTo);
            String commaSeparatedListOfCourses = StringHelper.toString(courses, ",");
            
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
            statusToAdmin = "Copying to multiple feedback sessions.<br>" +
                            "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Courses: <br>" +
                            commaSeparatedListOfCourses + "<br>" +
                            "<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                            "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                            "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                            "<span class=\"bold\">Instructions:</span> " + fs.instructions + "<br>" +
                            "Copied from <span class=\"bold\">(" + feedbackSessionNameFrom + ")</span> for Course <span class=\"bold\">[" + courseIdFrom + "]</span> created.<br>";

            // Go to sessions page after copying,
            // so that the instructor can see the new feedback sessions
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
            
        } catch (EntityAlreadyExistsException e) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            statusToAdmin = e.getMessage();
            isError = true;
            
            return createRedirectToEditPageWithError(feedbackSessionNameFrom, courseIdFrom);
            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            
            return createRedirectToEditPageWithError(feedbackSessionNameFrom, courseIdFrom);
            
        }
        
    }

    /**
     * Given an array of Course Ids, return only the Ids of Courses which has an existing feedback 
     * session with a name conflicting with feedbackSessionName
     * @param feedbackSessionName
     * @param coursesIdToCopyTo
     */
    private List<String> filterConflictsInCourses(
            String feedbackSessionName, String[] coursesIdToCopyTo) {
        List<String> courses = new ArrayList<String>();
        
        for (String courseIdToCopy: coursesIdToCopyTo) {
            FeedbackSessionAttributes existingFs = logic.getFeedbackSession(feedbackSessionName, courseIdToCopy);
            boolean fsAlreadyExists = (existingFs != null);
            
            if (fsAlreadyExists) {
                courses.add(existingFs.courseId);
            }
        }
        
        return courses;
    }    
    
    private RedirectResult createRedirectToEditPageWithError(String feedbackSessionName,
            String courseId) {
        isError = true;
        
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE);
        redirectResult.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
        redirectResult.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        redirectResult.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
        
        return redirectResult;
    }

    private RedirectResult createRedirectToEditPageWithErrorMsg(String feedbackSessionName,
            String courseId, String errorToUser) {
        statusToUser.add(errorToUser);
        return createRedirectToEditPageWithError(feedbackSessionName, courseId);
    }

}
