package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;

/**
 * Deletes a student's profile picture.
 */
class DeleteStudentProfilePictureAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to update this resource.");
        }
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        if (!userInfo.id.equals(googleId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this student's profile.");
        }
    }

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        StudentProfileAttributes studentProfileAttributes = logic.getStudentProfile(googleId);
        if (studentProfileAttributes == null) {
            throw new EntityNotFoundException("Invalid student profile");
        }
        if (fileStorage.doesFileExist(studentProfileAttributes.getGoogleId())) {
            fileStorage.delete(studentProfileAttributes.getGoogleId());
        }
        return new JsonResult("Your profile picture has been deleted successfully");
    }
}
