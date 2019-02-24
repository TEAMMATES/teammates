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
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        if (!userInfo.id.equals(googleId) && !isMasqueradeMode()) {
            throw new UnauthorizedAccessException("You are not authorized to delete this student's profile.");
        }
    }

    @Override
    public ActionResult execute() {
        logic.deletePicture(new BlobKey(logic.getStudentProfile(userInfo.id).pictureKey));
        logic.deletePictureKey(userInfo.id);
        return new JsonResult("Your profile picture has been deleted successfully", HttpStatus.SC_OK);
    }
}
