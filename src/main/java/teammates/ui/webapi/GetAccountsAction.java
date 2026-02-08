package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

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

        List<Account> accounts = sqlLogic.getAccountsForEmail(email);
        List<AccountData> accountDataList = accounts.stream()
                .map(AccountData::new).collect(Collectors.toList());

        return new JsonResult(new AccountsData(accountDataList));
    }

}
