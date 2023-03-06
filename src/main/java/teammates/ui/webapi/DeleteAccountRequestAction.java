package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Deletes an existing account request.
 */
class DeleteAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequest toDelete = sqlLogic.getAccountRequest(email, institute);

        if (toDelete != null && toDelete.getRegisteredAt() != null) {
            // instructor is already registered and cannot be deleted
            throw new InvalidOperationException("Account request of a registered instructor cannot be deleted.");
        }

        sqlLogic.deleteAccountRequest(email, institute);

        return new JsonResult("Account request successfully deleted.");
    }

}
