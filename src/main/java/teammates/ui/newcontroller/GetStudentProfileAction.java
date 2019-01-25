package teammates.ui.newcontroller;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Get a student's profile.
 */
public class GetStudentProfileAction extends Action {
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
        StudentProfileAttributes studentProfile = logic.getStudentProfile(studentId);

        if (studentProfile == null) {
            // create one on the fly
            studentProfile = StudentProfileAttributes.builder(studentId).build();
        }

        StudentProfile output = new StudentProfile(studentProfile);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetStudentProfileAction}.
     */
    public static class StudentProfile extends ActionResult.ActionOutput {
        private final StudentProfileAttributes studentProfile;

        public StudentProfile(StudentProfileAttributes studentProfile) {
            this.studentProfile = studentProfile;
        }

        public StudentProfileAttributes getStudentProfile() {
            return this.studentProfile;
        }
    }
}
