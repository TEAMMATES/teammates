package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;

/**
 * Get the information of an instructor by user ID.
 */
public class GetInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        gateKeeper.verifyInstructorInSameCourseAsInstructor(requestContext, userId);
    }

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Instructor instructor = logic.getInstructor(userId);
        if (instructor == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
        }

        return new JsonResult(new InstructorData(instructor));
    }

}
