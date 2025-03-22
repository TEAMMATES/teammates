package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;

/**
 * Gets account request information.
 */
public class GetAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest = sqlLogic.getAccountRequest(id);

        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request with id: " + id.toString() + " does not exist.");
        }

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
