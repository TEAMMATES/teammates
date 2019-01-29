package teammates.ui.webapi.action;

import teammates.common.util.Const;

/**
 * Action: serves a profile picture that is stored in Google Cloud Storage.
 */
public class GetStudentProfilePictureAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Profile picture is available to everyone with the blob key
    }

    @Override
    public ActionResult execute() {
        String blobKey = getNonNullRequestParamValue(Const.ParamsNames.BLOB_KEY);
        return new ImageResult(blobKey);
    }
}
