package teammates.ui.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseUpdateRequest;

/**
 * Updates a course.
 */
class UpdateCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        CourseAttributes course = logic.getCourse(courseId);
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    JsonResult execute() {
        CourseUpdateRequest courseUpdateRequest = getAndValidateRequestBody(CourseUpdateRequest.class);
        String courseTimeZone = courseUpdateRequest.getTimeZone();

        String timeZoneErrorMessage = FieldValidator.getInvalidityInfoForTimeZone(courseTimeZone);
        if (!timeZoneErrorMessage.isEmpty()) {
            return new JsonResult(timeZoneErrorMessage, HttpStatus.SC_BAD_REQUEST);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String courseName = courseUpdateRequest.getCourseName();
        CourseAttributes updatedCourse;

        try {
            updatedCourse = logic.updateCourseCascade(
                    CourseAttributes.updateOptionsBuilder(courseId)
                            .withName(courseName)
                            .withTimezone(ZoneId.of(courseTimeZone))
                            .build());
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException edee) {
            throw new EntityNotFoundException(edee);
        }

        return new JsonResult(new CourseData(updatedCourse));
    }
}
