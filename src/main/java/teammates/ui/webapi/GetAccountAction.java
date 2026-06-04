package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.AccountData;

/**
 * Gets account's information.
 */
public class GetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID accountId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_ID);

        Account account = logic.getAccount(accountId);

        if (account == null) {
            throw new EntityNotFoundException("Account does not exist.");
        }

        AccountData output = new AccountData(account);
        return new JsonResult(output);
    }

}
