package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.request.InvalidHttpRequestBodyException;
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
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        if (!studentId.equals(userInfo.id)) {
            throw new UnauthorizedAccessException("You are not authorized to update this student's profile.");
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        StudentProfileUpdateRequest updateRequest = getAndValidateRequestBody(StudentProfileUpdateRequest.class);

        try {
            StudentProfileAttributes studentProfile = sanitizeProfile(extractProfileData(studentId, updateRequest));
            logic.updateOrCreateStudentProfile(
                    StudentProfileAttributes.updateOptionsBuilder(studentId)
                            .withShortName(studentProfile.getShortName())
                            .withEmail(studentProfile.getEmail())
                            .withGender(studentProfile.getGender())
                            .withNationality(studentProfile.getNationality())
                            .withInstitute(studentProfile.getInstitute())
                            .withMoreInfo(studentProfile.getMoreInfo())
                            .build());
            return new JsonResult("Your profile has been edited successfully");
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }
    }

    private StudentProfileAttributes extractProfileData(String studentId, StudentProfileUpdateRequest req) {
        StudentProfileAttributes editedProfile =
                StudentProfileAttributes.builder(studentId).build();

        editedProfile.setShortName(req.getShortName());
        editedProfile.setEmail(req.getEmail());
        editedProfile.setInstitute(req.getInstitute());
        editedProfile.setNationality(req.getNationality());

        if ("".equals(editedProfile.getNationality())) {
            editedProfile.setNationality(req.getExistingNationality());
        }

        editedProfile.setGender(StudentProfileAttributes.Gender.getGenderEnumValue(req.getGender()));
        editedProfile.setMoreInfo(req.getMoreInfo());

        sanitizeProfile(editedProfile);
        return editedProfile;
    }

    private StudentProfileAttributes sanitizeProfile(StudentProfileAttributes studentProfile) {
        studentProfile.setShortName(StringHelper.trimIfNotNull(studentProfile.getShortName()));
        studentProfile.setEmail(StringHelper.trimIfNotNull(studentProfile.getEmail()));
        studentProfile.setNationality(StringHelper.trimIfNotNull(studentProfile.getNationality()));
        studentProfile.setInstitute(StringHelper.trimIfNotNull(studentProfile.getInstitute()));
        studentProfile.setMoreInfo(StringHelper.trimIfNotNull(studentProfile.getMoreInfo()));

        return studentProfile;
    }
}
