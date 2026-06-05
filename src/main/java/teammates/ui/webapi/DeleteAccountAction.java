package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Account;

/**
 * Action: deletes an existing account (either student or instructor).
 */
public class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID accountId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_ID);

        Account account = logic.getAccount(accountId);
        if (account != null) {
            logic.deleteAccountCascade(account.getGoogleId());
        }

        return new JsonResult("Account is successfully deleted.");
    }

}
