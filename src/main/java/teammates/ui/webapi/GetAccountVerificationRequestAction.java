package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.AccountVerificationRequestData;

/**
 * Gets account request information.
 */
public class GetAccountVerificationRequestAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);
        gateKeeper.verifyCanViewAccountVerificationRequest(requestContext, id);
    }

    @Override
    public JsonResult execute() {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        AccountVerificationRequest accountVerificationRequest = logic.getAccountVerificationRequest(id);

        if (accountVerificationRequest == null) {
            throw new EntityNotFoundException("Account request with id: " + id.toString() + " does not exist.");
        }

        AccountVerificationRequestData output = new AccountVerificationRequestData(accountVerificationRequest);
        return new JsonResult(output);
    }

}
