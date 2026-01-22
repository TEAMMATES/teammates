package teammates.ui.webapi;

import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Create a new course for an instructor.
 */
public class CreateCourseAction extends Action {
    boolean useDatastore; // TODO: Remove once migration is complete

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

        useDatastore = checkCreateCoursePermissionDatastore(institute);
        boolean canCreateCourse = checkCreateCoursePermissionSql(institute)
                || checkCreateCoursePermissionDatastore(institute);

        if (!canCreateCourse) {
            throw new UnauthorizedAccessException("You are not allowed to create a course under this institute. "
                    + "If you wish to do so, please request for an account under the institute.", true);
        }
    }

    private boolean checkCreateCoursePermissionSql(String institute) {
        List<Instructor> existingInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.getId());
        return existingInstructors
                .stream()
                .filter(Instructor::hasCoownerPrivileges)
                .map(instructor -> sqlLogic.getCourse(instructor.getCourseId()))
                .filter(Objects::nonNull)
                .anyMatch(course -> institute.equals(course.getInstitute()));
    }

    private boolean checkCreateCoursePermissionDatastore(String institute) {
        List<InstructorAttributes> existingInstructors = logic.getInstructorsForGoogleId(userInfo.getId());
        return existingInstructors
                .stream()
                .filter(InstructorAttributes::hasCoownerPrivileges)
                .map(instructor -> logic.getCourse(instructor.getCourseId()))
                .filter(Objects::nonNull)
                .anyMatch(course -> institute.equals(course.getInstitute()));
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

        // TODO: Remove datastore course creation logic once migration is complete. This is to satisfy datastore E2E tests.
        if (useDatastore) {
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

        try {
            Course createdCourse = sqlLogic.createCourse(course);
            Account account = sqlLogic.getAccountForGoogleId(userInfo.getId());
            Instructor instructor = getInstructor(account, createdCourse);

            Instructor createdInstructor = sqlLogic.createInstructor(instructor);
            taskQueuer.scheduleInstructorForSearchIndexing(
                    createdInstructor.getCourseId(),
                    createdInstructor.getEmail());

            return new JsonResult(new CourseData(createdCourse));

        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("The course ID " + newCourseId
                    + " has been used by another course, possibly by some other user."
                    + " Please try again with a different course ID.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }

    private static Instructor getInstructor(Account account, Course createdCourse) {
        String instructorName = account.getName();
        String instructorEmail = account.getEmail();
        InstructorPrivileges privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor instructor = new Instructor(
                createdCourse,
                instructorName,
                instructorEmail,
                true, // isDisplayedToStudents
                instructorName, // displayName
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                privileges
        );
        instructor.setAccount(account);
        return instructor;
    }
}
