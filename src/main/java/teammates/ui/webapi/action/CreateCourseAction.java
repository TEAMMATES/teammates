package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.ui.webapi.output.CourseData;
import teammates.ui.webapi.request.CourseCreateRequest;

/**
 * Create a new course for an instructor.
 */
public class CreateCourseAction extends Action {

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
        CourseCreateRequest courseCreateRequest = getAndValidateRequestBody(CourseCreateRequest.class);

        String newCourseId = courseCreateRequest.getCourseId();
        String newCourseName = courseCreateRequest.getCourseName();
        String newCourseTimeZone = courseCreateRequest.getTimeZone();

        try {
            logic.createCourseAndInstructor(userInfo.id, newCourseId, newCourseName, newCourseTimeZone);
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new CourseData(logic.getCourse(newCourseId)));
    }
}
