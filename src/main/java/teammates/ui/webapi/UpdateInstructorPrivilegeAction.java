package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates an instructor's privileges.
 * Can only be accessed by instructors with the modify instructor permission.
 */
public class UpdateInstructorPrivilegeAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (isCourseMigrated(courseId)) {
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(
                    instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        } else {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String emailOfInstructorToUpdate = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (!isCourseMigrated(courseId)) {
            return executeWithDatastore(courseId, emailOfInstructorToUpdate);
        }

        Instructor instructorToUpdate = sqlLogic.getInstructorForEmail(courseId, emailOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
        }

        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);
        InstructorPrivileges newPrivileges = request.getPrivileges();
        newPrivileges.validatePrivileges();

        instructorToUpdate.setPrivileges(newPrivileges);
        sqlLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToUpdate);

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructorToUpdate.getPrivileges());
        return new JsonResult(response);
    }

    private JsonResult executeWithDatastore(String courseId, String emailOfInstructorToUpdate)
            throws InvalidHttpRequestBodyException {
        InstructorAttributes instructorToUpdate = logic.getInstructorForEmail(courseId, emailOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
        }

        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);
        InstructorPrivileges newPrivileges = request.getPrivileges();
        newPrivileges.validatePrivileges();

        instructorToUpdate.setPrivileges(newPrivileges);
        logic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToUpdate);

        try {
            instructorToUpdate = logic.updateInstructor(
                    InstructorAttributes
                            .updateOptionsWithEmailBuilder(instructorToUpdate.getCourseId(), instructorToUpdate.getEmail())
                            .withPrivileges(instructorToUpdate.getPrivileges())
                            .build());
        } catch (InstructorUpdateException | InvalidParametersException e) {
            // Should not happen as only privilege is updated
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructorToUpdate.getPrivileges());
        return new JsonResult(response);
    }

}
