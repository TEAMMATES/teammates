package teammates.ui.webapi;

import teammates.common.exception.AuthException;
import teammates.common.util.Const;

/**
 * Action: deletes an existing account (either student or instructor).
 * <p>The corresponding user in the auth system (if any) is also deleted.</p>
 */
class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws AuthException {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        logic.deleteAccountCascade(googleId);

        authProxy.deleteUser(googleId);

        return new JsonResult("Account is successfully deleted.");
    }

}
