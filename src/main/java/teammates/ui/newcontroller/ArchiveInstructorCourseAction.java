package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Archives an active course for instructor.
 */
public class ArchiveInstructorCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String archiveStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ARCHIVE_STATUS);
        boolean isArchive = Boolean.parseBoolean(archiveStatus);

        try {
            logic.setArchiveStatusOfInstructor(userInfo.id, idOfCourseToArchive, isArchive);
        } catch (EntityDoesNotExistException | InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        String statusMessage;
        if (isArchive) {
            if (isRedirectedToHomePage()) {
                statusMessage = "The course " + idOfCourseToArchive + " has been archived. "
                        + "It will not appear in the home page any more. You can access archived courses from the 'Courses' tab."
                        + "<br>Go there to undo the archiving and bring the course back to the home page.";
            } else {
                statusMessage = "The course " + idOfCourseToArchive + " has been archived. "
                        + "It will not appear in the home page any more.";
            }
        } else {
            statusMessage = "The course " + idOfCourseToArchive + " has been unarchived.";
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
