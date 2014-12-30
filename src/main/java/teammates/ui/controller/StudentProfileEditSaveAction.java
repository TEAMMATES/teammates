package teammates.ui.controller;

import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * Action: saves the new profile details given by a student. 
 *         A purely Action based URI as it redirects back to
 *         StudentProfilePageAction once completed
 */
public class StudentProfileEditSaveAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        try {
            account.studentProfile = extractProfileData();
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
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_NATIONALITY, studentProfile.nationality);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, studentProfile.institute);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_PROFILE_MOREINFO, studentProfile.moreInfo);
    }

    private StudentProfileAttributes extractProfileData() {
        StudentProfileAttributes editedProfile = new StudentProfileAttributes();
        
        editedProfile.googleId = account.googleId;
        editedProfile.shortName = getRequestParamValue(Const.ParamsNames.STUDENT_SHORT_NAME);
        editedProfile.email = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_EMAIL);
        editedProfile.institute = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION);
        editedProfile.nationality = getRequestParamValue(Const.ParamsNames.STUDENT_NATIONALITY);
        editedProfile.gender = getRequestParamValue(Const.ParamsNames.STUDENT_GENDER);
        editedProfile.moreInfo = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_MOREINFO);
        editedProfile.pictureKey = "";
        
        preprocessParameters(editedProfile);
        validatePostParameters(editedProfile);        

        return editedProfile;
    }

    private void preprocessParameters(StudentProfileAttributes studentProfile) {
        if(studentProfile.shortName != null){
            studentProfile.shortName = studentProfile.shortName.trim();;
        }
       
        if(studentProfile.email != null){
            studentProfile.email = studentProfile.email.trim();
        }
        
        if(studentProfile.gender != null){
            studentProfile.gender = studentProfile.gender.trim();
        }
        
        if(studentProfile.nationality != null){
            studentProfile.nationality = studentProfile.nationality.trim();
        }
        
        if(studentProfile.institute != null){
            studentProfile.institute = studentProfile.institute.trim();
        }
        
        if(studentProfile.moreInfo != null){
            studentProfile.moreInfo = studentProfile.moreInfo.trim();
        }
    }
}
