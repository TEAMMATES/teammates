package teammates.ui.webapi;

import com.google.firebase.auth.AuthErrorCode;

import teammates.common.exception.AuthException;
import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Action: deletes an existing account (either student or instructor).
 * <p>The associated Firebase user is also deleted.</p>
 */
class DeleteAccountAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws AuthException {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        if (fileStorage.doesFileExist(googleId)) {
            fileStorage.delete(googleId);
        }
        logic.deleteAccountCascade(googleId);

        try {
            authProxy.deleteUser(googleId);
        } catch (AuthException e) {
            if (AuthErrorCode.USER_NOT_FOUND.toString().equals(e.getErrorCode())) {
                // Deleting Firebase user error of type user not found logged as warning and not thrown as exception
                // to reduce unnecessary attention as old TEAMMATES users are not Firebase users
                log.warning("Firebase user not found: " + e.getMessage());
            } else {
                throw e;
            }
        }

        return new JsonResult("Account is successfully deleted.");
    }

}
