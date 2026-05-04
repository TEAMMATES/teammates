package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.logic.entity.Instructor;
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

        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String emailOfInstructorToUpdate = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        Instructor instructorToUpdate = logic.getInstructorForEmail(courseId, emailOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
        }

        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);
        InstructorPrivileges newPrivileges = request.getPrivileges();
        newPrivileges.validatePrivileges();

        instructorToUpdate.setPrivileges(newPrivileges);
        logic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToUpdate);

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructorToUpdate.getPrivileges());
        return new JsonResult(response);
    }

}
