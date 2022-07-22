package teammates.ui.webapi;

import teammates.common.exception.FirebaseException;
import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Action: deletes an existing account (either student or instructor).
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

        if (!googleId.contains("@")) {
            googleId = googleId.concat("@gmail.com");
        }
        try {
            firebaseInstance.deleteUser(googleId);
        } catch (FirebaseException e) {
            // Deleting Firebase user error logged as warning and not throw an exception to ensure backwards compatibility
            log.warning("Cannot delete Firebase user: " + e.getMessage());
        }

        return new JsonResult("Account is successfully deleted.");
    }

}
