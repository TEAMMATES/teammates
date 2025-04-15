package teammates.ui.webapi;

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

    @Override
    public boolean isTransactionNeeded() {
        return false;
    }

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

        boolean canCreateCourse = sqlLogic.canInstructorCreateCourseWithTransaction(userInfo.getId(), institute);

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
            Account instructorAccount = sqlLogic.getAccountForGoogleIdWithTransaction(userInfo.getId());
            course = sqlLogic.createCourseAndInstructorWithTransaction(instructorAccount, course);

            Instructor instructorCreatedForCourse = sqlLogic.getInstructorByGoogleIdWithTransaction(newCourseId, userInfo.getId());
            taskQueuer.scheduleInstructorForSearchIndexing(instructorCreatedForCourse.getCourseId(),
                    instructorCreatedForCourse.getEmail());
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("The course ID " + course.getId()
                    + " has been used by another course, possibly by some other user."
                    + " Please try again with a different course ID.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        CourseData courseData = new CourseData(course);
        return new JsonResult(courseData);
    }
}
