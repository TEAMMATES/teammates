package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

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
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_EDITED);
            statusToAdmin = "Student Profile for <span class=\"bold\">(" + account.googleId + ")</span> edited.<br>" +
                    account.studentProfile.toString();
        } catch (InvalidParametersException ipe) {
            setStatusForException(ipe);
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
        
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> keys = blobs.get(Const.ParamsNames.STUDENT_PROFILE_PIC);
        
        if(keys != null && keys.size() > 0) {
            editedProfile.pictureKey = keys.get(0).getKeyString();
        }
        
        validatePostParameters(editedProfile);
        
        return editedProfile;
    }

}
