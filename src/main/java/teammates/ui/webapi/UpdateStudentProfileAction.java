package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.request.StudentProfileUpdateRequest;

/**
 * Update a student's profile.
 */
class UpdateStudentProfileAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        if (!studentId.equals(userInfo.id)) {
            throw new UnauthorizedAccessException("You are not authorized to update this student's profile.");
        }
    }

    @Override
    JsonResult execute() {
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        StudentProfileUpdateRequest updateRequest = getAndValidateRequestBody(StudentProfileUpdateRequest.class);

        try {
            StudentProfileAttributes studentProfile = sanitizeProfile(extractProfileData(studentId, updateRequest));
            logic.updateOrCreateStudentProfile(
                    StudentProfileAttributes.updateOptionsBuilder(studentId)
                            .withShortName(studentProfile.shortName)
                            .withEmail(studentProfile.email)
                            .withGender(studentProfile.gender)
                            .withNationality(studentProfile.nationality)
                            .withInstitute(studentProfile.institute)
                            .withMoreInfo(studentProfile.moreInfo)
                            .build());
            return new JsonResult("Your profile has been edited successfully", HttpStatus.SC_ACCEPTED);
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }

    private StudentProfileAttributes extractProfileData(String studentId, StudentProfileUpdateRequest req) {
        StudentProfileAttributes editedProfile =
                StudentProfileAttributes.builder(studentId).build();

        editedProfile.shortName = req.getShortName();
        editedProfile.email = req.getEmail();
        editedProfile.institute = req.getInstitute();
        editedProfile.nationality = req.getNationality();

        if ("".equals(editedProfile.nationality)) {
            editedProfile.nationality = req.getExistingNationality();
        }

        editedProfile.gender = StudentProfileAttributes.Gender.getGenderEnumValue(req.getGender());
        editedProfile.moreInfo = req.getMoreInfo();

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
