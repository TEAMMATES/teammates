package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.AccountRequestData;

/**
 * Gets account request information.
 */
public class GetAccountRequestAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);
        gateKeeper.verifyCanViewAccountRequest(requestContext, id);
    }

    @Override
    public JsonResult execute() {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest = logic.getAccountRequest(id);

        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request with id: " + id.toString() + " does not exist.");
        }

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
