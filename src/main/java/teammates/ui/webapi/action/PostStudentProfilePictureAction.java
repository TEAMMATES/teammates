package teammates.ui.webapi.action;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;

/**
 * Action: saves the file information of the profile picture
 *         that was just uploaded.
 */
public class PostStudentProfilePictureAction extends Action {
    /*
     * This class is not tested in ActionTests as it is difficult to
     * reproduce the upload action done by Google Blobstore API
     * without the server running.
     * TODO: To cover it in UiTests.
     */

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() throws EntityNotFoundException {
        try {
            Part image = extractProfilePicture();
            byte[] imageData = new byte[(int) image.getSize()];
            try (InputStream is = image.getInputStream()) {
                is.read(imageData);
            }
            String pictureKey = GoogleCloudStorageHelper.writeImageDataToGcs(userInfo.id, imageData);
            logic.updateStudentProfilePicture(userInfo.id, pictureKey);
            return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED,
                    HttpStatus.SC_OK);
        } catch (IllegalArgumentException | IOException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }

    private Part extractProfilePicture() throws IllegalStateException {
        try {
            if (req.getParts() == null || req.getParts().isEmpty()) {
                throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN);
            }
            Part image = req.getParts().iterator().next();
            return validateProfilePicture(image);
        } catch (Exception e) {
            /*
             * This means the action was called directly (and not via BlobStore API callback).
             * Simply redirect to ProfilePage.
             */
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            return null;
        }
    }

    private Part validateProfilePicture(Part image) throws IllegalArgumentException {
        if (image.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        } else if (!image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
        }

        return image;
    }

}
