package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountIdentity;
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

        List<Account> accounts = sqlLogic.getAccountsForEmail(email);
        List<AccountData> accountDataList = accounts.stream()
                .map(account -> {
                    AccountIdentity identity = sqlLogic.getFirstIdentityForAccount(account.getAccountId());
                    String loginIdentifier = identity != null ? identity.getLoginIdentifier() : "";
                    String loginProvider = identity != null ? identity.getProviderName() : "";
                    return new AccountData(account, loginIdentifier, loginProvider);
                })
                .toList();

        return new JsonResult(new AccountsData(accountDataList));
    }

}
