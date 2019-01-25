package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.util.Assumption;
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
        // Anyone logged in can upload a student's profile picture
    }

    @Override
    public ActionResult execute() {
        String pictureKey;
        BlobKey blobKey = new BlobKey("");

        try {
            BlobInfo blobInfo = extractProfilePictureKey();
            blobKey = blobInfo.getBlobKey();
            pictureKey = renameFileToGoogleId(blobInfo);
            logic.updateStudentProfilePicture(userInfo.id, pictureKey);
            return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED,
                    HttpStatus.SC_OK);
        } catch (IllegalArgumentException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (BlobstoreFailureException | IOException bfe) {
            deletePicture(blobKey);
            return new JsonResult(bfe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }

    private String renameFileToGoogleId(BlobInfo blobInfo) throws IOException {
        Assumption.assertNotNull(blobInfo);

        BlobKey blobKey = blobInfo.getBlobKey();
        byte[] imageData = new byte[(int) blobInfo.getSize()];
        try (InputStream blobStream = new BlobstoreInputStream(blobKey)) {
            blobStream.read(imageData);
        }

        deletePicture(blobKey);
        return GoogleCloudStorageHelper.writeImageDataToGcs(userInfo.id, imageData);
    }

    private BlobInfo extractProfilePictureKey() throws IllegalStateException {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService()
                    .getBlobInfos(req);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.STUDENT_PROFILE_PHOTO);
            if (blobs == null || blobs.isEmpty()) {
                throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN);
            }
            BlobInfo profilePic = blobs.get(0);
            return validateProfilePicture(profilePic);
        } catch (IllegalStateException e) {
            /*
             * This means the action was called directly (and not via BlobStore API callback).
             * Simply redirect to ProfilePage.
             */
            return null;
        }
    }

    private BlobInfo validateProfilePicture(BlobInfo profilePic) throws IllegalArgumentException {
        if (profilePic.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deletePicture(profilePic.getBlobKey());
            throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        } else if (!profilePic.getContentType().contains("image/")) {
            deletePicture(profilePic.getBlobKey());
            throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
        }

        return profilePic;
    }

    private void deletePicture(BlobKey blobKey) {
        if (blobKey.equals(new BlobKey(""))) {
            return;
        }
        logic.deletePicture(blobKey);
    }
}
