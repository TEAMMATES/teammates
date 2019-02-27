package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

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

        if (!studentId.equals(userInfo.id) && !isMasqueradeMode()) {
            return new JsonResult("You are not authorized to view this student's profile.",
                    HttpStatus.SC_FORBIDDEN);
        }

        StudentProfileAttributes studentProfile = logic.getStudentProfile(studentId);
        String name = logic.getAccount(studentId).name;

        if (studentProfile == null) {
            // create one on the fly
            studentProfile = StudentProfileAttributes.builder(studentId).build();
        }

        StudentProfile output = new StudentProfile(name, studentProfile);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetStudentProfileAction}.
     */
    public static class StudentProfile extends ApiOutput {
        private final StudentProfileAttributes studentProfile;
        private String name;

        public StudentProfile(String name, StudentProfileAttributes studentProfile) {
            this.studentProfile = studentProfile;
            this.name = name;
        }

        public StudentProfileAttributes getStudentProfile() {
            return this.studentProfile;
        }

        public String getName() {
            return this.name;
        }
    }
}
