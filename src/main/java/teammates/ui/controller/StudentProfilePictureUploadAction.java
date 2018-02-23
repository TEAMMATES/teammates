package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: saves the file information of the profile picture
 *         that was just uploaded.
 */
public class StudentProfilePictureUploadAction extends Action {
    /*
     * This class is not tested in ActionTests as it is difficult to
     * reproduce the upload action done by Google Blobstore API
     * without the server running.
     * This class is covered in UiTests.
     */
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        gateKeeper.verifyLoggedInUserPrivileges();

        String pictureKey = "";
        BlobKey blobKey = new BlobKey("");
        RedirectResult r = createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);

        try {
            BlobInfo blobInfo = extractProfilePictureKey();
            if (!isError) {
                blobKey = blobInfo.getBlobKey();
                pictureKey = renameFileToGoogleId(blobInfo);
                logic.updateStudentProfilePicture(account.googleId, pictureKey);
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED,
                                                   StatusMessageColor.SUCCESS));
                r.addResponseParam(Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT, "true");
            }
        } catch (BlobstoreFailureException | IOException bfe) {
            deletePicture(blobKey);
            updateStatusesForBlobstoreFailure();
            isError = true;
        } catch (Exception e) {
            /*
             * This is for other exceptions like EntityNotFound, IllegalState, etc
             * that occur rarely and are handled higher up.
             */
            deletePicture(new BlobKey(pictureKey));
            statusToUser.clear();
            throw e;
        }

        return r;
    }

    private String renameFileToGoogleId(BlobInfo blobInfo) throws IOException {
        Assumption.assertNotNull(blobInfo);

        BlobKey blobKey = blobInfo.getBlobKey();
        byte[] imageData = new byte[(int) blobInfo.getSize()];
        try (InputStream blobStream = new BlobstoreInputStream(blobKey)) {
            blobStream.read(imageData);
        }

        deletePicture(blobKey);
        return GoogleCloudStorageHelper.writeImageDataToGcs(account.googleId, imageData);
    }

    private BlobInfo extractProfilePictureKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService()
                                                                          .getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.STUDENT_PROFILE_PHOTO);
            if (blobs == null || blobs.isEmpty()) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN,
                                                   StatusMessageColor.DANGER));
                isError = true;
                return null;
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

    private BlobInfo validateProfilePicture(BlobInfo profilePic) {
        if (profilePic.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE,
                                               StatusMessageColor.DANGER));
            return null;
        } else if (!profilePic.getContentType().contains("image/")) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE,
                                               StatusMessageColor.DANGER));
            return null;
        }

        return profilePic;
    }

    private void deletePicture(BlobKey blobKey) {
        if (blobKey.equals(new BlobKey(""))) {
            return;
        }
        try {
            logic.deletePicture(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE
                          + " : Unable to delete profile picture (possible unused picture with key: "
                          + blobKey.getKeyString() + " || Error Message: "
                          + bfe.getMessage() + System.lineSeparator();
        }
    }

    private void updateStatusesForBlobstoreFailure() {
        statusToAdmin += Const.ACTION_RESULT_FAILURE + " : Could not delete profile picture for account ("
                       + account.googleId + ")" + System.lineSeparator();
        statusToUser.clear();
        statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN,
                                           StatusMessageColor.DANGER));
    }

}
