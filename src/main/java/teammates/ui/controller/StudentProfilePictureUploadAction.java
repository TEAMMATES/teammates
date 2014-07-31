package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: saves the file information of the profile picture
 *         that was just uploaded.
 */
public class StudentProfilePictureUploadAction extends Action {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyLoggedInUserPrivileges();
        
        String pictureKey = "";
        RedirectResult r = createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
        try {
            pictureKey = extractProfilePictureKey();
            if (pictureKey != "") {
                logic.updateStudentProfilePicture(account.googleId, pictureKey);
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);
                r.addResponseParam(Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT, "true");
            }
        } catch (BlobstoreFailureException bfe) {
            // This branch is not tested as recreating such a scenario is difficult in the 
            // dev server for testing purposes.
            // TODO: find a way to cover this branch
            // delete the newly uploaded picture
            deletePicture(new BlobKey(pictureKey));
            statusToAdmin += Const.ACTION_RESULT_FAILURE 
                    + " : Could not delete profile picture for account ("
                    + account.googleId 
                    + ")";
            statusToUser.clear();
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN);
            isError = true;
        } catch (Exception e) {
            // this is for other exceptions like EntityNotFound, IllegalState, etc that might occur rarely
            // and are handled higher up.
            deletePicture(new BlobKey(pictureKey));
            statusToUser.clear();
            throw e;
        }
        
        return r;
                
    }

    private String extractProfilePictureKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.STUDENT_PROFILE_PHOTO);
            
            /*USED FOR TESTING PURPOSES
                Map<String, List<FileInfo>> filesMap = BlobstoreServiceFactory.getBlobstoreService().getFileInfos(request);
                List<FileInfo> files = filesMap.get(Const.ParamsNames.STUDENT_PROFILE_PIC);
                
                log.info(files.get(0).toString());
            */
            if(blobs != null && blobs.size() > 0) {
                BlobInfo profilePic = blobs.get(0);
                return validateProfilePicture(profilePic);
            } else{
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN);
                isError = true;
                return "";
            }
        } catch (IllegalStateException e) {
            // this means the student did not give a picture to upload
            return "";
        }
    }

    private String validateProfilePicture (BlobInfo profilePic) {
        if (profilePic.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
            return "";
        } else if(!profilePic.getContentType().contains("image/")) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
            return "";
        } else {
            return profilePic.getBlobKey().getKeyString();
        }
        
    }
    
    private void deletePicture(BlobKey blobKey) {
        if (blobKey == new BlobKey("")) return;
        
        try {
            logic.deletePicture(blobKey);
        } catch (BlobstoreFailureException bfe) {
            // This branch is not tested as recreating such a scenario is difficult in the 
            // dev server for testing purposes.
            // TODO: find a way to cover this branch
            
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Unable to delete profile picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }
}
