package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;

/**
 * Action: deletes an existing account (either student or instructor).
 */
class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        AccountAttributes accountInfo = logic.getAccount(googleId);

        if (accountInfo == null || accountInfo.isMigrated()) {
            sqlLogic.deleteAccountCascade(googleId);
        } else {
            logic.deleteAccountCascade(googleId);
        }

        return new JsonResult("Account is successfully deleted.");
    }

}
