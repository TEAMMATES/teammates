package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Deletes a student's profile picture and its picture key.
 */
public class DeleteStudentProfilePictureAction extends Action {
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
        logic.deletePicture(new BlobKey(blobKey));
        logic.deletePictureKey(userInfo.id);
        return new JsonResult("Your profile picture has been deleted successfully", HttpStatus.SC_OK);
    }
}
