package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: deleting an instructor for a course by another instructor
 */
public class InstructorCourseInstructorDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));

        int numberOfInstructorsForCourse = logic.getInstructorsForCourse(courseId).size();
        
        /* Process deleting an instructor and setup status to be shown to user and admin */
        if (numberOfInstructorsForCourse != 1) {
            logic.deleteInstructor(courseId, instructorEmail);
            
            statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED);
            statusToAdmin = "Instructor <span class=\"bold\"> " + instructorEmail + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
        } else {
            isError = true;
            statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED);
            statusToAdmin = "Instructor <span class=\"bold\"> " + instructorEmail + "</span>"
                    + " in Course <span class=\"bold\">[" + courseId + "]</span> could not be deleted "
                    + "as there is only one instructor left.<br>";
        }
        
        /* Create redirection. It will redirect back to 'Courses' page if the instructor deletes himself */
        RedirectResult result = null;
        if (logic.isInstructorOfCourse(account.googleId, courseId)) {
            result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        } else {
            result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        }
        
        return result;
    }

}
