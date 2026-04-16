package teammates.ui.webapi;

import teammates.common.util.Const;

/**
 * Action: deletes an existing account (either student or instructor).
 */
public class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonBlankRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        sqlLogic.deleteAccountCascade(googleId);

        return new JsonResult("Account is successfully deleted.");
    }

}
