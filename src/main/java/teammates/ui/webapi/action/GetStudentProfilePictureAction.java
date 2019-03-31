package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: serves a profile picture that is stored in Google Cloud Storage.
 */
public class GetStudentProfilePictureAction extends Action {

    /** String indicating ACCESS is not given. */
    public static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    /** String indicating student not found. */
    public static final String STUDENT_NOT_FOUND = "No student found for given parameters";

    /** String indicating profile picture not found. */
    public static final String PROFILE_PIC_NOT_FOUND = "Student has no profile picture";

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (studentEmail == null || courseId == null) {
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }
        } else {
            //viewing someone else's photo
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);

            if (student == null) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            gateKeeper.verifyAccessibleForCurrentUserAsInstructorOrTeamMemberOrAdmin(userInfo.id,
                    courseId, student.getSection(), student.getEmail());
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentProfileAttributes studentProfile = null;

        if (studentEmail == null || courseId == null) {
            studentProfile = logic.getStudentProfile(userInfo.id);
        } else {
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);

            if (!student.googleId.isEmpty()) {
                studentProfile = logic.getStudentProfile(student.googleId);
            }
        }

        if (studentProfile == null || studentProfile.pictureKey.equals("")) {
            return new JsonResult(PROFILE_PIC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
        }

        return new ImageResult(studentProfile.pictureKey);
    }
}
