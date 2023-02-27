package teammates.ui.webapi;

import teammates.common.exception.InvalidOperationException;
import teammates.common.util.Const;

/**
 * Deletes an existing account request.
 */
class DeleteAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws teammates.ui.webapi.InvalidOperationException {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        try {
            sqlLogic.deleteAccountRequest(email, institute);
        } catch (InvalidOperationException invalidOperationException) {
            throw new teammates.ui.webapi.InvalidOperationException(invalidOperationException.getMessage());
        }
        return new JsonResult("Account request successfully deleted.");
    }

}
