package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.ui.exception.InvalidOperationException;

/**
 * Deletes an existing account verification request.
 */
public class DeleteAccountVerificationRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        logic.deleteAccountVerificationRequest(id);

        return new JsonResult("Account verification request successfully deleted.");
    }

}
