package teammates.ui.webapi;

import teammates.common.exception.FirebaseException;
import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Action: deletes an existing account (either student or instructor).
 * <p>The associated Firebase user is also deleted.</p>
 */
class DeleteAccountAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        if (fileStorage.doesFileExist(googleId)) {
            fileStorage.delete(googleId);
        }
        logic.deleteAccountCascade(googleId);

        String email = googleId.contains("@") ? googleId : googleId.concat("@gmail.com");
        try {
            firebaseInstance.deleteUser(email);
        } catch (FirebaseException e) {
            // Deleting Firebase user error logged as warning and not thrown as exception to ensure backwards
            // compatibility as old TEAMMATES users are not Firebase users
            log.warning("Cannot delete Firebase user: " + e.getMessage());
        }

        return new JsonResult("Account is successfully deleted.");
    }

}
