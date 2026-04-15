package teammates.ui.webapi;

import teammates.common.util.Const;

import java.util.UUID;

/**
 * Action: deletes an existing account (either student or instructor).
 */
public class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID accountId = getUuidRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        sqlLogic.deleteAccountCascade(accountId);

        return new JsonResult("Account is successfully deleted.");
    }

}
