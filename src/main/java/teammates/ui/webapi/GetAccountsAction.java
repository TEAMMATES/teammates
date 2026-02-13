package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountData;
import teammates.ui.output.AccountsData;

/**
 * Gets all accounts with the given email.
 */
public class GetAccountsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        email = SanitizationHelper.sanitizeEmail(email);

        List<AccountAttributes> premigratedAccounts = logic.getAccountsForEmail(email);
        List<Account> migratedAccounts = sqlLogic.getAccountsForEmail(email);
        List<AccountData> accounts = new ArrayList<>();

        for (AccountAttributes accountAttribute : premigratedAccounts) {
            accounts.add(new AccountData(accountAttribute));
        }

        for (Account account : migratedAccounts) {
            accounts.add(new AccountData(account));
        }

        return new JsonResult(new AccountsData(accounts));
    }

}
