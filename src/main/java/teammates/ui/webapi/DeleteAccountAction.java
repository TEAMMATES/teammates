package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;

/**
 * Action: deletes an existing account (either student or instructor).
 */
public class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID accountId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_ID);

        logic.deleteAccount(accountId);

        return new JsonResult("Account is successfully deleted.");
    }

}
