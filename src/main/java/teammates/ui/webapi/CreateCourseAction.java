package teammates.ui.webapi;

import java.util.List;
import java.util.Objects;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.output.CourseViewData;
import teammates.ui.output.InstructorCoursePermissionsData;
import teammates.ui.request.CourseCreateRequest;

/**
 * Create a new course for an instructor.
 */
public class CreateCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException, InvalidHttpRequestBodyException {
        CourseCreateRequest courseCreateRequest = getAndValidateRequestBody(CourseCreateRequest.class);

        String institute = courseCreateRequest.getInstitute().trim();
        List<Instructor> existingInstructors = logic.getInstructorsByAccountId(requestContext.getAccount().getId());
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

        try {
            Course createdCourse = logic.createCourseAndInstructor(
                    getCurrentAccount(), courseCreateRequest);
            HibernateUtil.flushSession();
            CourseViewData output = new CourseViewData(new CourseData(createdCourse));
            Instructor instructor = getInstructorFromRequest(createdCourse.getId());
            if (instructor != null) {
                output.setInstructorPermissions(new InstructorCoursePermissionsData(
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_COURSE),
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT),
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)));
            }
            return new JsonResult(output);
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
