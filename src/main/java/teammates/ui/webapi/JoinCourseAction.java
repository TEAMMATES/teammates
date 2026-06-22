package teammates.ui.webapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.RegKeyRequest;

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
        RegKeyRequest requestBody = getAndValidateRequestBody(RegKeyRequest.class);
        return joinCourse(requestBody.getKey());
    }

    private JsonResult joinCourse(String regKey) throws InvalidOperationException {
        try {
            logic.joinCourseAndNotify(regKey, requestContext.getAccount());
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        }

        return new JsonResult("User successfully joined course");
    }
}
