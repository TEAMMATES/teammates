package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.api.GateKeeper;

/**
 * Action: saves the file information of the profile picture
 *         that was just uploaded.
 */
public class StudentProfilePictureUploadAction extends Action {
    // This class is not tested in ActionTests as it is difficult to 
    // reproduce the upload action done by Google Blobstore API without 
    // the server running. This class is covered in UiTests
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyLoggedInUserPrivileges();
        
        String pictureKey = "";
        BlobKey blobKey = new BlobKey("");
        RedirectResult r = createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
        
        try {
            blobKey = extractProfilePictureKey();
            if (pictureKey != "") {
                pictureKey = renameFileToGoogleId(blobKey);
                logic.updateStudentProfilePicture(account.googleId, pictureKey);
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);
                r.addResponseParam(Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT, "true");
            }
        } catch (BlobstoreFailureException | IOException bfe) {
            deletePicture(blobKey);
            updateStatusesForBlobstoreFailure();
            isError = true;
        } catch (Exception e) {
            // this is for other exceptions like EntityNotFound, IllegalState, etc 
            // that occur rarely and are handled higher up.
            
            deletePicture(new BlobKey(pictureKey));
            statusToUser.clear();
            throw e;
        }
        
        return r;
    }

    private String renameFileToGoogleId(BlobKey blobKey) throws IOException {
        Image oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
        String newKey = GoogleCloudStorageHelper.writeDataToGcs(account.googleId, oldImage.getImageData(), "");
        deletePicture(blobKey);
        blobKey = new BlobKey("");
        return newKey;
    }

    private BlobKey extractProfilePictureKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.STUDENT_PROFILE_PHOTO);
            
            if(blobs != null && blobs.size() > 0) {
                BlobInfo profilePic = blobs.get(0);
                return validateProfilePicture(profilePic);
            } else{
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN);
                isError = true;
                return new BlobKey("");
            }
        } catch (IllegalStateException e) {
            // this means the action was called directly (and not via BlobStore API callback)
            // simply redirect to ProfilePage
            return new BlobKey("");
        }
    }

    private BlobKey validateProfilePicture (BlobInfo profilePic) {
        if (profilePic.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
            return new BlobKey("");
        } else if(!profilePic.getContentType().contains("image/")) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
            return new BlobKey("");
        } else {
            return profilePic.getBlobKey();
        }
        
    }
    
    private void deletePicture(BlobKey blobKey) {
        if (blobKey == new BlobKey("")) return;
        
        try {
            logic.deletePicture(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Unable to delete profile picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }

    private void updateStatusesForBlobstoreFailure() {
        statusToAdmin += Const.ACTION_RESULT_FAILURE 
                + " : Could not delete profile picture for account ("
                + account.googleId 
                + ")" + Const.EOL;
        statusToUser.clear();
        statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN);
    }
}
