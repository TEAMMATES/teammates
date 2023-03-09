package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.User;

/**
 * Action: deletes an existing account (either student or instructor).
 */
class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        AccountAttributes accountInfo = logic.getAccount(googleId);

        if (accountInfo == null || accountInfo.isMigrated()) {
            List<User> usersToDelete = sqlLogic.getAllUsersByGoogleId(googleId);

            for (User user : usersToDelete) {
                sqlLogic.deleteUser(user);
            }

            sqlLogic.deleteAccount(googleId);
        } else {
            logic.deleteAccountCascade(googleId);
        }

        return new JsonResult("Account is successfully deleted.");
    }

}
