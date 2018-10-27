package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.ui.pagedata.InstructorFeedbackEditCopyData;

public class InstructorFeedbackEditCopyAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String newFeedbackSessionName = getRequestParamValue(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        String[] coursesIdToCopyTo = getRequestParamValues(Const.ParamsNames.COPIED_COURSES_ID);
        String originalFeedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String originalCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, originalCourseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, originalFeedbackSessionName);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, newFeedbackSessionName);

        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE;
        }

        if (coursesIdToCopyTo == null || coursesIdToCopyTo.length == 0) {
            return createAjaxResultWithErrorMessage(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        }

        InstructorAttributes instructor = logic.getInstructorForGoogleId(originalCourseId, account.googleId);
        FeedbackSessionAttributes fsa = logic.getFeedbackSession(originalFeedbackSessionName, originalCourseId);

        gateKeeper.verifyAccessible(instructor, logic.getCourse(originalCourseId),
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, fsa, false);

        try {
            // Check if there are no conflicting feedback sessions in all the courses
            List<String> conflictCourses =
                    filterConflictsInCourses(newFeedbackSessionName, coursesIdToCopyTo);

            if (!conflictCourses.isEmpty()) {
                String commaSeparatedListOfCourses = StringHelper.toString(conflictCourses, ",");
                String errorToUser = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                                                   newFeedbackSessionName,
                                                   commaSeparatedListOfCourses);

                return createAjaxResultWithErrorMessage(errorToUser);
            }

            FeedbackSessionAttributes fs = null;
            // Copy the feedback sessions
            // TODO: consider doing this as a batch insert
            for (String courseIdToCopyTo : coursesIdToCopyTo) {
                InstructorAttributes instructorForCourse =
                        logic.getInstructorForGoogleId(courseIdToCopyTo, account.googleId);
                CourseAttributes courseToCopyTo = logic.getCourse(courseIdToCopyTo);
                gateKeeper.verifyAccessible(instructorForCourse, courseToCopyTo,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

                fs = logic.copyFeedbackSession(newFeedbackSessionName, courseIdToCopyTo, courseToCopyTo.getTimeZone(),
                        originalFeedbackSessionName, originalCourseId, instructor.email);
            }

            List<String> courses = Arrays.asList(coursesIdToCopyTo);
            String commaSeparatedListOfCourses = StringHelper.toString(courses, ",");

            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPIED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "Copying to multiple feedback sessions.<br>"
                    + "New Feedback Session <span class=\"bold\">(" + fs.getFeedbackSessionName() + ")</span> "
                    + "for Courses: <br>" + commaSeparatedListOfCourses + "<br>"
                    + "<span class=\"bold\">From:</span> " + fs.getStartTime()
                    + "<span class=\"bold\"> to</span> " + fs.getEndTime() + "<br>"
                    + "<span class=\"bold\">Session visible from:</span> " + fs.getSessionVisibleFromTime() + "<br>"
                    + "<span class=\"bold\">Results visible from:</span> " + fs.getResultsVisibleFromTime() + "<br><br>"
                    + "<span class=\"bold\">Instructions:</span> " + fs.getInstructions() + "<br>"
                    + "Copied from <span class=\"bold\">(" + originalFeedbackSessionName + ")</span> for Course "
                    + "<span class=\"bold\">[" + originalCourseId + "]</span> created.<br>";

            // Return with redirection url (handled in javascript) to the sessions page after copying,
            // so that the instructor can see the new feedback sessions
            return createAjaxResultWithoutClearingStatusMessage(
                       new InstructorFeedbackEditCopyData(account, sessionToken,
                                                          Config.getAppUrl(nextUrl)
                                                                .withParam(Const.ParamsNames.ERROR,
                                                                           Boolean.FALSE.toString())
                                                                .withParam(Const.ParamsNames.USER_ID,
                                                                           account.googleId)
                                                          ));

        } catch (EntityAlreadyExistsException e) {
            // If conflicts are checked above, this will only occur via race condition
            setStatusForException(e, Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            return createAjaxResultWithErrorMessage(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            return createAjaxResultWithErrorMessage(e.getMessage());
        }

    }

    /**
     * Given an array of Course Ids, return only the Ids of Courses which has
     * an existing feedback session with a name conflicting with feedbackSessionName.
     */
    private List<String> filterConflictsInCourses(String feedbackSessionName, String[] coursesIdToCopyTo) {
        List<String> courses = new ArrayList<>();

        for (String courseIdToCopy : coursesIdToCopyTo) {
            FeedbackSessionAttributes existingFs =
                    logic.getFeedbackSession(feedbackSessionName, courseIdToCopy);
            boolean hasExistingFs = existingFs != null;

            if (hasExistingFs) {
                courses.add(existingFs.getCourseId());
            }
        }

        return courses;
    }

    private AjaxResult createAjaxResultWithErrorMessage(String errorToUser) {
        isError = true;
        return createAjaxResult(new InstructorFeedbackEditCopyData(account, sessionToken, errorToUser));
    }
}
