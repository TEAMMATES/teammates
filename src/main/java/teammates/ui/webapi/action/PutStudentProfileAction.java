package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Action: Update a student's profile.
 */
public class PutStudentProfileAction extends Action {

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
    public ActionResult execute() {
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);

        if (!studentId.equals(userInfo.id) && !isMasqueradeMode()) {
            return new JsonResult("You are not authorized to update this student's profile.",
                    HttpStatus.SC_FORBIDDEN);
        }

        try {
            StudentProfileAttributes studentProfile = sanitizeProfile(extractProfileData(studentId));
            logic.updateOrCreateStudentProfile(
                    StudentProfileAttributes.updateOptionsBuilder(studentId)
                            .withShortName(studentProfile.shortName)
                            .withEmail(studentProfile.email)
                            .withGender(studentProfile.gender)
                            .withNationality(studentProfile.nationality)
                            .withInstitute(studentProfile.institute)
                            .withMoreInfo(studentProfile.moreInfo)
                            .build());
            return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_EDITED, HttpStatus.SC_ACCEPTED);
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }

    private StudentProfileAttributes extractProfileData(String studentId) {
        StudentProfileAttributes editedProfile =
                StudentProfileAttributes.builder(studentId).build();

        editedProfile.shortName = getRequestParamValue(Const.ParamsNames.STUDENT_SHORT_NAME);
        editedProfile.email = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_EMAIL);
        editedProfile.institute = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_INSTITUTION);
        editedProfile.nationality = getRequestParamValue(Const.ParamsNames.STUDENT_NATIONALITY);
        if ("".equals(editedProfile.nationality)) {
            editedProfile.nationality = getRequestParamValue("existingNationality");
        }
        editedProfile.gender = StudentProfileAttributes.Gender.getGenderEnumValue(
                                                                getRequestParamValue(Const.ParamsNames.STUDENT_GENDER));
        editedProfile.moreInfo = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_MOREINFO);
        editedProfile.pictureKey = "";

        sanitizeProfile(editedProfile);

        return editedProfile;
    }

    private StudentProfileAttributes sanitizeProfile(StudentProfileAttributes studentProfile) {
        studentProfile.shortName = StringHelper.trimIfNotNull(studentProfile.shortName);
        studentProfile.email = StringHelper.trimIfNotNull(studentProfile.email);
        studentProfile.nationality = StringHelper.trimIfNotNull(studentProfile.nationality);
        studentProfile.institute = StringHelper.trimIfNotNull(studentProfile.institute);
        studentProfile.moreInfo = StringHelper.trimIfNotNull(studentProfile.moreInfo);

        return studentProfile;
    }
}
