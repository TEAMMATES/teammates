package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;

/**
 * Action: downgrades an instructor account to student account.
 */
class DowngradeAccountAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String instructorId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        try {
            logic.downgradeInstructorToStudentCascade(instructorId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult("Instructor account is successfully downgraded to student.", HttpStatus.SC_OK);
    }

}
