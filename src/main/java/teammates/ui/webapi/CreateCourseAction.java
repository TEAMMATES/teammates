package teammates.ui.webapi;

import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
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
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        List<InstructorAttributes> existingInstructors = logic.getInstructorsForGoogleId(userInfo.getId());
        boolean canCreateCourse = existingInstructors
                .stream()
                .filter(InstructorAttributes::hasCoownerPrivileges)
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
        courseCreateRequest.setCourseId(courseCreateRequest.getCourseId().trim());

        String newCourseTimeZone = courseCreateRequest.getTimeZone();

        String timeZoneErrorMessage = FieldValidator.getInvalidityInfoForTimeZone(newCourseTimeZone);
        if (!timeZoneErrorMessage.isEmpty()) {
            throw new InvalidHttpRequestBodyException(timeZoneErrorMessage);
        }

        String newCourseId = courseCreateRequest.getCourseId();
        String newCourseName = courseCreateRequest.getCourseName();
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        CourseAttributes courseAttributes =
                CourseAttributes.builder(newCourseId)
                        .withName(newCourseName)
                        .withTimezone(newCourseTimeZone)
                        .withInstitute(institute)
                        .build();

        try {
            logic.createCourseAndInstructor(userInfo.getId(), courseAttributes);

            InstructorAttributes instructorCreatedForCourse = logic.getInstructorForGoogleId(newCourseId, userInfo.getId());
            taskQueuer.scheduleInstructorForSearchIndexing(instructorCreatedForCourse.getCourseId(),
                    instructorCreatedForCourse.getEmail());
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("The course ID " + courseAttributes.getId()
                    + " has been used by another course, possibly by some other user."
                    + " Please try again with a different course ID.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new CourseData(logic.getCourse(newCourseId)));
    }
}
