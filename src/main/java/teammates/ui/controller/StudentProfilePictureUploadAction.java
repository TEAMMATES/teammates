package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

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
        new GateKeeper().verifyLoggedInUserPrivileges();

        String pictureKey = "";
        BlobKey blobKey = new BlobKey("");
        BlobInfo blobInfo = null;
        RedirectResult r = createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);

        try {
            blobInfo = extractProfilePictureKey();
            if (!isError) {
                blobKey = blobInfo.getBlobKey();
                pictureKey = renameFileToGoogleId(blobInfo);
                logic.updateStudentProfilePicture(account.googleId, pictureKey);
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED, StatusMessageColor.SUCCESS));
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
        InputStream blobStream = new BlobstoreInputStream(blobKey);
        byte[] imageData = new byte[(int) blobInfo.getSize()];
        blobStream.read(imageData);
        blobStream.close();

        String newKey = uploadFileToGcs(imageData);
        deletePicture(blobKey);
        return newKey;
    }

    /**
     * Uploads the given image data to the cloud storage into a file with
     * the user's googleId as the name.
     * Returns a blobKey that can be used to identify the file.
     * 
     * @param fileName
     * @param transformedImage
     * @return BlobKey
     * @throws IOException
     * TODO: use the function 'writeDataToGcs' in GoogleCloudStorageHelper to achieve this 
     */
    private String uploadFileToGcs(byte[] transformedImage) throws IOException {
        GcsFilename fileName = new GcsFilename(Config.GCS_BUCKETNAME, account.googleId);
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, new GcsFileOptions.Builder()
                                                                                                .mimeType("image/png")
                                                                                                .build());

        outputChannel.write(ByteBuffer.wrap(transformedImage));
        outputChannel.close();

        return BlobstoreServiceFactory.getBlobstoreService()
                                      .createGsBlobKey("/gs/" + Config.GCS_BUCKETNAME + "/" + account.googleId)
                                      .getKeyString();
    }

    private BlobInfo extractProfilePictureKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService()
                                                                          .getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.STUDENT_PROFILE_PHOTO);
            if (blobs != null && blobs.size() > 0) {
                BlobInfo profilePic = blobs.get(0);
                return validateProfilePicture(profilePic);
            } else {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN, StatusMessageColor.DANGER));
                isError = true;
                return null;
            }
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
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE, StatusMessageColor.DANGER));
            return null;
        } else if (!profilePic.getContentType().contains("image/")) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE, StatusMessageColor.DANGER));
            return null;
        } else {
            return profilePic;
        }
    }

    private void deletePicture(BlobKey blobKey) {
        if (blobKey == new BlobKey("")) {
            return;
        }
        try {
            logic.deletePicture(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE
                          + " : Unable to delete profile picture (possible unused picture with key: "
                          + blobKey.getKeyString() + " || Error Message: "
                          + bfe.getMessage() + Const.EOL;
        }
    }

    private void updateStatusesForBlobstoreFailure() {
        statusToAdmin += Const.ACTION_RESULT_FAILURE + " : Could not delete profile picture for account ("
                       + account.googleId + ")" + Const.EOL;
        statusToUser.clear();
        statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN, StatusMessageColor.DANGER));
    }

}
