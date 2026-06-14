package teammates.ui.webapi;

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
    void checkSpecificAccessControl() throws InvalidHttpRequestBodyException, UnauthorizedAccessException {
        Instructor instructorToEdit = getInstructorToEditFromRequestBody();
        if (instructorToEdit == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        gateKeeper.verifyInstructorHasPrivilege(requestContext, instructorToEdit.getCourseId(),
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        InstructorUpdateRequest instructorRequest = getAndValidateRequestBody(InstructorUpdateRequest.class);

        Instructor updatedInstructor;
        try {
            updatedInstructor = logic.updateInstructorCascade(instructorRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (InstructorUpdateException e) {
            throw new InvalidOperationException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        logic.updateToEnsureValidityOfInstructorsForTheCourse(updatedInstructor);

        InstructorData newInstructorData = new InstructorData(updatedInstructor);

        return new JsonResult(newInstructorData);
    }

    private Instructor getInstructorToEditFromRequestBody() throws InvalidHttpRequestBodyException {
        InstructorUpdateRequest instructorRequest = getAndValidateRequestBody(InstructorUpdateRequest.class);
        return logic.getInstructor(instructorRequest.getId());
    }

}
