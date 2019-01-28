package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Adds a new course for instructor.
 */
public class AddInstructorCourseAction extends Action {

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
        String newCourseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String newCourseName = getNonNullRequestParamValue(Const.ParamsNames.COURSE_NAME);
        String newCourseTimeZone = getNonNullRequestParamValue(Const.ParamsNames.COURSE_TIME_ZONE);

        try {
            logic.createCourseAndInstructor(userInfo.id, newCourseId, newCourseName, newCourseTimeZone);
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        String statusMessage = "The course has been added. Click <a href=\"/web/instructor/courses/enroll?courseid="
                + newCourseId + "\">here</a> to add students to the course or click "
                + "<a href=\"/web/instructor/courses/edit?courseid=" + newCourseId + "\">here</a> to add other instructors."
                + "<br>If you don't see the course in the list below, please refresh the page after a few moments.";
        return new JsonResult(statusMessage);
    }
}
