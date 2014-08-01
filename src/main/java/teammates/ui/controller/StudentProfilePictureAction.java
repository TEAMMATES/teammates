package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Action: serves a profile picture that is stored in Google Cloud Storage
 */
public class StudentProfilePictureAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        ActionResult result = null;
        if (getRequestParamValue(Const.ParamsNames.BLOB_KEY) != null) {
            log.info("blob-key given");
            result = handleRequestWithBlobKey();
        } else if (getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL) != null) {
            log.info("email and course given");
            result = handleRequestWithEmailAndCourse();
        } else {
            Assumption.fail("expected blob-key, or student email with courseId");
        }
        
        return result;
    }

    private ActionResult handleRequestWithEmailAndCourse() throws EntityDoesNotExistException {
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, email);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        email = StringHelper.decrypt(email);
        courseId = StringHelper.decrypt(courseId);
        
        log.info("email: " + email + ", course: " + courseId);
        
        if(logic.getInstructorForGoogleId(courseId, account.googleId) == null) {
            throw new UnauthorizedAccessException(
                    "User is not instructor of the course that student belongs to");
        }
        
        StudentAttributes student = logic.getStudentForEmail(courseId, email);
        String blobKey = "";
        if (student == null) {
            throw new EntityDoesNotExistException("student with " +
                    courseId + "/" + email);
        } else if (student.googleId == null 
                || student.googleId.isEmpty()) {
            // unregistered student, so ignore the picture request
        } else {
            StudentProfileAttributes profile = logic.getStudentProfile(student.googleId);
            if (profile != null) {
                blobKey = profile.pictureKey;
            }
        }
        return createImageResult(blobKey);
    }

    private ActionResult handleRequestWithBlobKey() {
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY, blobKey);
        return createImageResult(blobKey);
    }
}
 