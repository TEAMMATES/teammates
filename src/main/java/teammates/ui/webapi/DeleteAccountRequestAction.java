package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.InvalidOperationException;

/**
 * Deletes an existing account request.
 */
public class DeleteAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest toDelete = logic.getAccountRequest(id);

        if (toDelete != null && toDelete.getRegisteredAt() != null) {
            // instructor is already registered and cannot be deleted
            throw new InvalidOperationException("Account request of a registered instructor cannot be deleted.");
        }

        logic.deleteAccountRequest(id);

        return new JsonResult("Account request successfully deleted.");
    }

}
