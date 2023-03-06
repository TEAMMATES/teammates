package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountsData;

/**
 * Gets all accounts with the given email.
 */
class GetAccountsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        email = SanitizationHelper.sanitizeEmail(email);

        List<Account> accounts = sqlLogic.getAccountsForEmail(email);

        return new JsonResult(new AccountsData(accounts));
    }

}
