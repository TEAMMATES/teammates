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
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditCopyAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {        
        String newFeedbackSessionName = getRequestParamValue(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        String[] coursesIdToCopyTo = getRequestParamValues(Const.ParamsNames.COPIED_COURSES_ID);
        String originalFeedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String originalCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull("null course id", originalCourseId);
        Assumption.assertNotNull("null fs name", originalFeedbackSessionName);
        Assumption.assertNotNull("null copied fs name", newFeedbackSessionName);
        
        String currentPage = getRequestParamValue(Const.ParamsNames.CURRENT_PAGE);
        
        if (coursesIdToCopyTo == null || coursesIdToCopyTo.length == 0) {
            return createRedirectWithErrorMsg(
                    originalFeedbackSessionName,
                    originalCourseId,
                    Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED,
                    currentPage);
        }
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(originalCourseId, account.googleId); 
        FeedbackSessionAttributes fsa = logic.getFeedbackSession(originalFeedbackSessionName, originalCourseId);
        
        GateKeeper gk = new GateKeeper();
        gk.verifyAccessible(
                instructor,
                logic.getCourse(originalCourseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        gk.verifyAccessible(instructor, fsa, false);
        
        try {
            // Check if there are no conflicting feedback sessions in all the courses 
            List<String> conflictCourses =
                    filterConflictsInCourses(newFeedbackSessionName, coursesIdToCopyTo);
            
            if (!conflictCourses.isEmpty()) {
                String errorToAdmin = "For measuring failure rate: user tried to copy session to multiple courses."
                                      + "Name of Session: " + newFeedbackSessionName + "<br>"
                                      + "Copying to course(s) " + conflictCourses.toString() + " failed.";
                log.severe(errorToAdmin);
                
                String commaSeparatedListOfCourses = StringHelper.toString(conflictCourses, ",");
                String errorToUser = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                                                   newFeedbackSessionName,
                                                   commaSeparatedListOfCourses);
                
                return createRedirectWithErrorMsg(originalFeedbackSessionName,
                                                            originalCourseId,
                                                            errorToUser, currentPage);
            }
            
            FeedbackSessionAttributes fs = null;
            // Copy the feedback sessions
            // TODO: consider doing this as a batch insert
            for (String courseIdToCopyTo : coursesIdToCopyTo) {
                InstructorAttributes instructorForCourse =
                        logic.getInstructorForGoogleId(courseIdToCopyTo, account.googleId);
                gk.verifyAccessible(
                        instructorForCourse,
                        logic.getCourse(courseIdToCopyTo),
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
                
                fs = logic.copyFeedbackSession(newFeedbackSessionName, courseIdToCopyTo,
                        originalFeedbackSessionName, originalCourseId, instructor.email);
            }
            
            List<String> courses = Arrays.asList(coursesIdToCopyTo);
            String commaSeparatedListOfCourses = StringHelper.toString(courses, ",");
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPIED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "Copying to multiple feedback sessions.<br>"
                    + "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> "
                    + "for Courses: <br>" + commaSeparatedListOfCourses + "<br>"
                    + "<span class=\"bold\">From:</span> " + fs.startTime
                    + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>"
                    + "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>"
                    + "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>"
                    + "<span class=\"bold\">Instructions:</span> " + fs.instructions + "<br>"
                    + "Copied from <span class=\"bold\">(" + originalFeedbackSessionName + ")</span> for Course "
                    + "<span class=\"bold\">[" + originalCourseId + "]</span> created.<br>";

            // Go to sessions page after copying,
            // so that the instructor can see the new feedback sessions
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
            
        } catch (EntityAlreadyExistsException e) {
            // If conflicts are checked above, this will only occur via race condition
            setStatusForException(e, Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            return createRedirectWithError(originalFeedbackSessionName, originalCourseId, currentPage);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            return createRedirectWithError(originalFeedbackSessionName, originalCourseId, currentPage);
        }
        
    }

    /**
     * Given an array of Course Ids, return only the Ids of Courses which has 
     * an existing feedback session with a name conflicting with feedbackSessionName
     * @param feedbackSessionName
     * @param coursesIdToCopyTo
     */
    private List<String> filterConflictsInCourses(String feedbackSessionName, String[] coursesIdToCopyTo) {
        List<String> courses = new ArrayList<String>();
        
        for (String courseIdToCopy: coursesIdToCopyTo) {
            FeedbackSessionAttributes existingFs =
                    logic.getFeedbackSession(feedbackSessionName, courseIdToCopy);
            boolean fsAlreadyExists = existingFs != null;
            
            if (fsAlreadyExists) {
                courses.add(existingFs.courseId);
            }
        }
        
        return courses;
    }    
    
    private RedirectResult createRedirectWithError(String feedbackSessionName, String courseId, String currentPage) {
        isError = true;      
        String redirectUrl = getRedirectUrl(currentPage);

        RedirectResult redirectResult = createRedirectResult(redirectUrl);
        redirectResult.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
        redirectResult.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        redirectResult.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
        
        return redirectResult;
    }
    
    private RedirectResult createRedirectWithErrorMsg(
            String feedbackSessionName, String courseId, String errorToUser, String currentPage) {
        statusToUser.add(new StatusMessage(errorToUser, StatusMessageColor.DANGER));
        return createRedirectWithError(feedbackSessionName, courseId, currentPage);
    }
    
    private String getRedirectUrl(String currentPage) {
        if (currentPage.contains(Const.PageNames.INSTRUCTOR_HOME_PAGE)) {
            return Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        } else if (currentPage.contains(Const.PageNames.INSTRUCTOR_FEEDBACKS_PAGE)
                      || currentPage.contains(Const.PageNames.INSTRUCTOR_FEEDBACK_COPY)) {
            return Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        } else {
            return Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
        }
    }
}
