package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;

import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class StudentProfileEditSaveAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        if(isUnregistered) {
            // unregistered users cannot view the page
            throw new UnauthorizedAccessException("User is not registered");
        }
        
        account.studentProfile = extractProfileData();
        account.studentProfile.googleId = account.googleId;
        
        try {
            logic.updateStudentProfile(account.studentProfile);
            if (statusToUser.isEmpty()) {
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_EDITED);
            }
            statusToAdmin = "Student Profile for <span class=\"bold\">(" + account.googleId + ")</span> edited.<br>" +
                    account.studentProfile.toString();
        } catch (InvalidParametersException ipe) {
            if (!account.studentProfile.pictureKey.equals("")) {
                deletePicture(new BlobKey(account.studentProfile.pictureKey));
            }
            setStatusForException(ipe);
        } catch (BlobstoreFailureException bfe) {
            // This branch is not tested as recreating such a scenario is difficult in the 
            // dev server for testing purposes.
            
            // delete the newly uploaded picture
            deletePicture(new BlobKey(account.studentProfile.pictureKey));
            statusToAdmin += Const.ACTION_RESULT_FAILURE 
                    + " : Could not delete profile picture of profile for account ("
                    + account.googleId 
                    + ")";
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN);
            isError = true;
        } catch (Exception e) {
            deletePicture(new BlobKey(account.studentProfile.pictureKey));
            throw e;
        }
        
        return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
    }
    
    private void validatePostParameters(StudentProfileAttributes studentProfile) {
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_SHORT_NAME, studentProfile.shortName);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentProfile.email);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_GENDER, studentProfile.gender);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_COUNTRY, studentProfile.country);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, studentProfile.institute);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_PROFILE_MOREINFO, studentProfile.moreInfo);
    }

    private StudentProfileAttributes extractProfileData() {
        StudentProfileAttributes editedProfile = new StudentProfileAttributes();
        
        editedProfile.shortName = getRequestParamValue(Const.ParamsNames.STUDENT_SHORT_NAME);
        editedProfile.email = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_EMAIL);
        editedProfile.institute = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION);
        editedProfile.country = getRequestParamValue(Const.ParamsNames.STUDENT_COUNTRY);
        editedProfile.gender = getRequestParamValue(Const.ParamsNames.STUDENT_GENDER);
        editedProfile.moreInfo = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_MOREINFO);
        extractProfilePictureInfo(editedProfile);
        
        validatePostParameters(editedProfile);
        
        return editedProfile;
    }

    private void extractProfilePictureInfo(
            StudentProfileAttributes editedProfile) {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.STUDENT_PROFILE_PIC);
            
            /*USED FOR TESTING PURPOSES
                Map<String, List<FileInfo>> filesMap = BlobstoreServiceFactory.getBlobstoreService().getFileInfos(request);
                List<FileInfo> files = filesMap.get(Const.ParamsNames.STUDENT_PROFILE_PIC);
                
                log.info(files.get(0).toString());
            */
            if(blobs != null && blobs.size() > 0) {
                BlobInfo profilePic = blobs.get(0);
                validateAndStorePictureKey(editedProfile, profilePic);
            }
        } catch (IllegalStateException e) {
            // this means the student did not give a picture to upload
        }
    }

    private void validateAndStorePictureKey(
            StudentProfileAttributes editedProfile, BlobInfo profilePic) {
        if (profilePic.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deletePicture(profilePic.getBlobKey());
            isError = true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        } else {
            editedProfile.pictureKey = profilePic.getBlobKey().getKeyString();
        }
        
    }

    private void deletePicture(BlobKey blobKey) {
        try {
            logic.deleteProfilePicture(blobKey);
        } catch (BlobstoreFailureException bfe) {
            // This branch is not tested as recreating such a scenario is difficult in the 
            // dev server for testing purposes.
            
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Unable to delete profile picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }

}
