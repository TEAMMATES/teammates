package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorUpdateRequest;

/**
 * Edits an instructor in a course.
 */
public class UpdateInstructorAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID instructorId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        gateKeeper.verifyCanModifyInstructor(requestContext, instructorId);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID instructorId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        InstructorUpdateRequest instructorRequest = getAndValidateRequestBody(InstructorUpdateRequest.class);

        Instructor updatedInstructor;
        try {
            updatedInstructor = logic.updateInstructorCascade(instructorId, instructorRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (InstructorUpdateException e) {
            throw new InvalidOperationException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        InstructorData newInstructorData = new InstructorData(updatedInstructor);

        return new JsonResult(newInstructorData);
    }

}
