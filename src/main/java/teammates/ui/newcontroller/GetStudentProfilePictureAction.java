package teammates.ui.newcontroller;

import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Action: serves a profile picture that is stored in Google Cloud Storage.
 */
public class GetStudentProfilePictureAction extends Action {

    private static final Logger log = Logger.getLogger();

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
        log.info("blob-key given: " + blobKey);
        return new ImageResult(blobKey);
    }
}
