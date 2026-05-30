package teammates.ui.webapi;

import java.util.List;
import java.util.Objects;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Create a new course for an instructor.
 */
public class CreateCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
        List<Instructor> existingInstructors = logic.getInstructorsForGoogleId(getCurrentUserGoogleId());
        boolean canCreateCourse = existingInstructors.stream()
                .filter(Instructor::hasCoownerPrivileges)
                .map(instructor -> logic.getCourse(instructor.getCourseId()))
                .filter(Objects::nonNull)
                .anyMatch(course -> institute.equals(course.getInstitute()));

        if (!canCreateCourse) {
            throw new UnauthorizedAccessException("You are not allowed to create a course under this institute. "
                    + "If you wish to do so, please request for an account under the institute.", true);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        CourseCreateRequest courseCreateRequest = getAndValidateRequestBody(CourseCreateRequest.class);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        try {
            Course createdCourse = logic.createCourseAndInstructor(
                    getCurrentAccount(), courseCreateRequest, institute);
            return new JsonResult(new CourseData(createdCourse));
        } catch (EntityAlreadyExistsException e) {
            String newCourseId = courseCreateRequest.getCourseId().trim();
            throw new InvalidOperationException("The course ID " + newCourseId
                    + " has been used by another course, possibly by some other user."
                    + " Please try again with a different course ID.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }
}
