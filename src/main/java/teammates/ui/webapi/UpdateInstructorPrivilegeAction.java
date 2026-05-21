package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates an instructor's privileges.
 * Can only be accessed by instructors with the modify instructor permission.
 */
public class UpdateInstructorPrivilegeAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = logic.getInstructorByGoogleId(courseId, getCurrentUserGoogleId());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);
        InstructorPrivileges newPrivileges = request.getPrivileges();

        Instructor instructorToUpdate;
        try {
            instructorToUpdate = logic.updateInstructorPrivileges(userId, newPrivileges);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructorToUpdate.getPrivileges());
        return new JsonResult(response);
    }

}
