package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Action: Edits details of a student in a course.
 */
public class UpdateStudentAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        gateKeeper.verifyCanModifyStudent(requestContext, studentId);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        StudentUpdateRequest updateRequest = getAndValidateRequestBody(StudentUpdateRequest.class);

        try {
            if (updateRequest.getIsSessionSummarySendEmail()) {
                logic.updateStudentAndEnqueueSummaryEmail(studentId, updateRequest);
            } else {
                logic.updateStudentEnrollment(studentId, updateRequest);
            }
            return new JsonResult("Student update successfully");
        } catch (EnrollException | EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }

}
