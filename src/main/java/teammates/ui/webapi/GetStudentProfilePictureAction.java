package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Action: serves a profile picture that is stored in Google Cloud Storage.
 */
class GetStudentProfilePictureAction extends Action {

    /** Indicates ACCESS is not given. */
    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
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

            gateKeeper.verifyAccessibleForCurrentUserAsInstructorOrTeamMember(userInfo.id,
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
            if (student == null) {
                throw new EntityNotFoundException("No student found");
            }

            if (!StringHelper.isEmpty(student.getGoogleId())) {
                studentProfile = logic.getStudentProfile(student.getGoogleId());
            }
        }

        if (studentProfile == null || !fileStorage.doesFileExist(studentProfile.getGoogleId())) {
            return new ImageResult();
        }

        byte[] bytes = fileStorage.getContent(studentProfile.getGoogleId());
        return new ImageResult(bytes);
    }
}
