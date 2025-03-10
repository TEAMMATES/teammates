package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountData;

/**
 * Gets account's information.
 */
public class GetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        Account account = sqlLogic.getAccountForGoogleId(googleId);

        if (account == null) {
            throw new EntityNotFoundException("Account does not exist.");
        }

        AccountData output = new AccountData(account);
        return new JsonResult(output);
    }

}
