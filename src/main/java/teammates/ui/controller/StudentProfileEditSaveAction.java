package teammates.ui.controller;

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
        
        try {
            account.studentProfile = extractProfileData();
            account.studentProfile.googleId = account.googleId;
            logic.updateStudentProfile(account.studentProfile);
            if (statusToUser.isEmpty()) {
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_EDITED);
            }
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
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_NATIONALITY, studentProfile.nationality);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, studentProfile.institute);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_PROFILE_MOREINFO, studentProfile.moreInfo);
    }

    private StudentProfileAttributes extractProfileData() {
        StudentProfileAttributes editedProfile = new StudentProfileAttributes();
        
        editedProfile.shortName = getRequestParamValue(Const.ParamsNames.STUDENT_SHORT_NAME);
        editedProfile.email = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_EMAIL);
        editedProfile.institute = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION);
        editedProfile.nationality = getRequestParamValue(Const.ParamsNames.STUDENT_NATIONALITY);
        editedProfile.gender = getRequestParamValue(Const.ParamsNames.STUDENT_GENDER);
        editedProfile.moreInfo = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_MOREINFO);
        editedProfile.pictureKey = "";
        
        validatePostParameters(editedProfile);
        
        return editedProfile;
    }
}
