package teammates.ui.webapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.CourseJoinKeyRequest;

/**
 * Action: joins a course for a student/instructor.
 */
public class JoinCourseAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        CourseJoinKeyRequest requestBody = getAndValidateRequestBody(CourseJoinKeyRequest.class);
        try {
            logic.joinCourseAndNotify(requestBody.getKey(), requestContext.getAccount());
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        }

        return new JsonResult("User successfully joined course");
    }
}
