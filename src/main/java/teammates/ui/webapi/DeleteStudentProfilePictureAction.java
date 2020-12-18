package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Deletes a student's profile picture and its picture key.
 */
class DeleteStudentProfilePictureAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to update this resource.");
        }
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        if (!userInfo.id.equals(googleId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this student's profile.");
        }
    }

    @Override
    JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        StudentProfileAttributes studentProfileAttributes = logic.getStudentProfile(googleId);
        if (studentProfileAttributes == null) {
            return new JsonResult("Invalid student profile", HttpStatus.SC_NOT_FOUND);
        }
        logic.deletePicture(studentProfileAttributes.pictureKey);
        logic.deletePictureKey(userInfo.id);
        return new JsonResult("Your profile picture has been deleted successfully", HttpStatus.SC_OK);
    }
}
