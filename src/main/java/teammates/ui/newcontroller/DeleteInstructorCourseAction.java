package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Moves a course to Recycle Bin (soft-delete).
 */
public class DeleteInstructorCourseAction extends Action {

    private String idOfCourseToDelete;

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, userInfo.id),
                logic.getCourse(idOfCourseToDelete),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    public ActionResult execute() {

        idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String statusMessage;

        try {
            logic.moveCourseToRecycleBin(idOfCourseToDelete);

            if (isRedirectedToHomePage()) {
                statusMessage = "The course " + idOfCourseToDelete + " has been deleted. "
                        + "You can restore it from the 'Courses' tab.";
            } else {
                statusMessage = "The course " + idOfCourseToDelete + " has been deleted. "
                        + "You can restore it from the soft-deleted courses table below.";
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(statusMessage);
    }

    /**
     * Checks if the action is executed in 'Home' page or 'Courses' page based on its redirection.
     */
    private boolean isRedirectedToHomePage() {
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        return nextUrl != null && nextUrl.equals(Const.ResourceURIs.INSTRUCTOR_HOME);
    }
}
