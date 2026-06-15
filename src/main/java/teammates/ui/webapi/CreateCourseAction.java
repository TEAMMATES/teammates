package teammates.ui.webapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;

/**
 * Create a new course for an instructor.
 */
public class CreateCourseAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException, InvalidHttpRequestBodyException {
        CourseCreateRequest courseCreateRequest = getAndValidateRequestBody(CourseCreateRequest.class);
        gateKeeper.verifyAccountVerifiedForInstitute(requestContext, courseCreateRequest.getInstituteId());
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        CourseCreateRequest courseCreateRequest = getAndValidateRequestBody(CourseCreateRequest.class);

        try {
            Course createdCourse = logic.createCourseAndInstructor(
                    getCurrentAccount(), courseCreateRequest);
            HibernateUtil.flushSession();
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
