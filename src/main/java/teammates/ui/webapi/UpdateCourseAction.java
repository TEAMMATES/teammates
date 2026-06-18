package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseUpdateRequest;

/**
 * Updates a course.
 */
public class UpdateCourseAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyCanModifyCourse(requestContext, courseId);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseUpdateRequest courseUpdateRequest = getAndValidateRequestBody(CourseUpdateRequest.class);
        String courseTimeZone = courseUpdateRequest.getTimeZone();
        String courseName = courseUpdateRequest.getCourseName();

        try {
            Course updatedCourse = logic.updateCourse(courseId, courseName, courseTimeZone);
            return new JsonResult(new CourseData(updatedCourse));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException edee) {
            throw new EntityNotFoundException(edee);
        }
    }
}
