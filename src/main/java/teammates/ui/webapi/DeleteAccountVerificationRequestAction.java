package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.InvalidOperationException;

/**
 * Deletes an existing account request.
 */
public class DeleteAccountVerificationRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        AccountVerificationRequest toDelete = logic.getAccountVerificationRequest(id);

        if (toDelete != null && toDelete.getCreatedDemoCourseAt() != null) {
            // TODO: remove this check
            // instructor is already registered and cannot be deleted
            throw new InvalidOperationException("Account request of a registered instructor cannot be deleted.");
        }

        logic.deleteAccountVerificationRequest(id);

        return new JsonResult("Account request successfully deleted.");
    }

}
