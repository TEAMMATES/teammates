package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Deletes a student's profile picture and its picture key.
 */
public class PutStudentProfilePictureAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to update this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String blobKey = getNonNullRequestParamValue(Const.ParamsNames.BLOB_KEY);
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        try {
            logic.deletePicture(new BlobKey(blobKey));
            logic.deletePictureKey(googleId);
            return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_PICTURE_DELETED, HttpStatus.SC_OK);
        } catch (BlobstoreFailureException bfe) {
            return new JsonResult(bfe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }
}
