package teammates.ui.webapi;

import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Create a new course for an instructor.
 */
class CreateCourseAction extends Action {

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

        // SQL Logic
        List<Instructor> existingInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.getId());
        boolean canCreateCourse = existingInstructors
                .stream()
                .filter(Instructor::hasCoownerPrivileges)
                .map(instructor -> instructor.getCourse())
                .filter(Objects::nonNull)
                .anyMatch(course -> institute.equals(course.getInstitute()));

        // Datastore Logic
        List<InstructorAttributes> existingInstructorsAttributes = logic.getInstructorsForGoogleId(userInfo.getId());
        canCreateCourse = canCreateCourse || existingInstructorsAttributes
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

        Course course = new Course(newCourseId, newCourseName, newCourseTimeZone, institute);

        try {
            sqlLogic.createCourse(course);

            Instructor instructorCreatedForCourse = sqlLogic.getInstructorByGoogleId(newCourseId, userInfo.getId());
            taskQueuer.scheduleInstructorForSearchIndexing(instructorCreatedForCourse.getCourseId(),
                    instructorCreatedForCourse.getEmail());
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("The course ID " + course.getId()
                    + " has been used by another course, possibly by some other user."
                    + " Please try again with a different course ID.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        HibernateUtil.flushSession();
        CourseData courseData = new CourseData(course);
        return new JsonResult(courseData);
    }
}
