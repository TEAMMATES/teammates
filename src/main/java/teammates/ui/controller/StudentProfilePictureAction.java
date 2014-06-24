package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class StudentProfilePictureAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        if (isUnregistered) {
            throw new UnauthorizedAccessException("User is not registered");
        }
        
        ActionResult result = null;
        if (getRequestParamValue(Const.ParamsNames.BLOB_KEY) != null) {
            result = handleRequestWithBlobKey();
        } else if (getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL) != null) {
            result = handleRequestWithEmailAndCourse();
        } else {
            Assumption.assertPostParamNotNull("expected blob-key or student email", null);
        }
        
        return result;
    }

    private ActionResult handleRequestWithEmailAndCourse() {
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, email);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        
        String googleId = logic.getStudentForEmail(courseId, email).googleId;
        String blobKey = logic.getStudentProfile(googleId).pictureKey;
        
        return createImageResult(blobKey);
    }

    private ActionResult handleRequestWithBlobKey() {
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY, blobKey);
        return createImageResult(blobKey);
    }
}
 