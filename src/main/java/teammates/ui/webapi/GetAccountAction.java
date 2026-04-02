package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountIdentity;
import teammates.ui.output.AccountData;

/**
 * Gets account's information.
 */
public class GetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String accountId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        Account account = sqlLogic.getAccountForId(accountId);

        if (account == null) {
            throw new EntityNotFoundException("Account does not exist.");
        }

        AccountIdentity identity = sqlLogic.getFirstIdentityForAccount(accountId);
        String loginIdentifier = identity != null ? identity.getLoginIdentifier() : "";
        String loginProvider = identity != null ? identity.getProviderName() : "";

        AccountData output = new AccountData(account, loginIdentifier, loginProvider);
        return new JsonResult(output);
    }

}
