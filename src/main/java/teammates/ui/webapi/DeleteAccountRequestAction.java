package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Deletes an existing account request.
 */
public class DeleteAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest toDelete = sqlLogic.getAccountRequest(id);

        if (toDelete != null && toDelete.getRegisteredAt() != null) {
            // instructor is already registered and cannot be deleted
            throw new InvalidOperationException("Account request of a registered instructor cannot be deleted.");
        }

        sqlLogic.deleteAccountRequest(id);

        return new JsonResult("Account request successfully deleted.");
    }

}
