package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action: deletes a student from a course.
 */
public class DeleteStudentAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        gateKeeper.verifyCanModifyStudent(requestContext, userId);
    }

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        logic.deleteStudentCascade(userId);

        return new JsonResult("Student is successfully deleted.");
    }

}
