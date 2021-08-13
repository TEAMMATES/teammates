package teammates.ui.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;

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
    }

    @Override
    public JsonResult execute() {
        CourseCreateRequest courseCreateRequest = getAndValidateRequestBody(CourseCreateRequest.class);

        String newCourseTimeZone = courseCreateRequest.getTimeZone();

        String timeZoneErrorMessage = FieldValidator.getInvalidityInfoForTimeZone(newCourseTimeZone);
        if (!timeZoneErrorMessage.isEmpty()) {
            return new JsonResult(timeZoneErrorMessage, HttpStatus.SC_BAD_REQUEST);
        }

        String newCourseId = courseCreateRequest.getCourseId();
        String newCourseName = courseCreateRequest.getCourseName();

        String institute = Const.UNKNOWN_INSTITUTION;
        AccountAttributes account = logic.getAccount(userInfo.getId());
        if (account != null && !StringHelper.isEmpty(account.getInstitute())) {
            institute = account.getInstitute();
        }

        CourseAttributes courseAttributes =
                CourseAttributes.builder(newCourseId)
                        .withName(newCourseName)
                        .withTimezone(ZoneId.of(newCourseTimeZone))
                        .withInstitute(institute)
                        .build();

        try {
            logic.createCourseAndInstructor(userInfo.getId(), courseAttributes);
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult("The course ID " + courseAttributes.getId()
                    + " has been used by another course, possibly by some other user."
                    + " Please try again with a different course ID.", HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new CourseData(logic.getCourse(newCourseId)));
    }
}
