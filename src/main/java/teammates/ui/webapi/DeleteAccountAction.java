package teammates.ui.webapi;

import teammates.common.util.Const;

/**
 * Action: deletes an existing account (either student or instructor).
 */
public class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        // deleteAccountCascade is needed for datastore for dual DB
        // as it deletes the student and instructor entities which are not yet migrated
        logic.deleteAccountCascade(googleId);
        sqlLogic.deleteAccountCascade(googleId);

        return new JsonResult("Account is successfully deleted.");
    }

}
