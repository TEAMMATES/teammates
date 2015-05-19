package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * Action: serves a profile picture that is stored in Google Cloud Storage
 */
public class StudentProfilePictureAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        ActionResult result = null;
        if (getRequestParamValue(Const.ParamsNames.BLOB_KEY) != null) {
            result = handleRequestWithBlobKey();
            statusToAdmin = "Requested Profile Picture by student directly";
        } else if (getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL) != null) {
            result = handleRequestWithEmailAndCourse();
            statusToAdmin = "Requested Profile Picture by instructor/other students";
        } else {
            Assumption.fail("expected blob-key, or student email with courseId");
        }
        return result;
    }

    private ActionResult handleRequestWithBlobKey() {
        String blobKey = getBlobKeyFromRequest();
        log.info("blob-key given: " + blobKey);
        return createImageResult(blobKey);
    }

    private ActionResult handleRequestWithEmailAndCourse()
            throws EntityDoesNotExistException {
        String email = getStudentEmailFromRequest();
        String courseId = getCourseIdFromRequest();
        log.info("email: " + email + ", course: " + courseId);

        StudentAttributes student = getStudentForGivenParameters(courseId, email);
        new GateKeeper().verifyAccessibleForCurrentUserAsInstructor(account, courseId, student.section);

        return createImageResult(getPictureKeyForStudent(student));
    }

    private StudentAttributes getStudentForGivenParameters(String courseId, String email)
            throws EntityDoesNotExistException {
        StudentAttributes student = logic.getStudentForEmail(courseId, email);
        if (student == null) {
            throw new EntityDoesNotExistException("student with " + courseId + "/" + email);
        }
        return student;
    }

    private String getBlobKeyFromRequest() {
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY, blobKey);
        return blobKey;
    }

    private String getCourseIdFromRequest() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        courseId = StringHelper.decrypt(courseId);
        return courseId;
    }

    private String getStudentEmailFromRequest() {
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, email);
        email = StringHelper.decrypt(email);
        return email;
    }

    private String getPictureKeyForStudent(StudentAttributes student) {
        String blobKey = "";
        if (student.googleId.isEmpty()) {
            // unregistered student, so ignore the picture request
        } else {
            StudentProfileAttributes profile = logic.getStudentProfile(student.googleId);

            // TODO: remove the null check once all legacy data has been ported
            if (profile != null) {
                blobKey = profile.pictureKey;
            }
        }
        return blobKey;
    }

}
