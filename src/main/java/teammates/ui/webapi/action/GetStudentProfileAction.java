package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.StudentProfileData;

/**
 * Get a student's profile by an instructor, a classmate of the student, or the student itself.
 */
public class GetStudentProfileAction extends Action {

    private static final String MESSAGE_NOT_STUDENT_ACCOUNT = "You did not login as a student,"
            + " so you cannot view your profile";
    private static final String MESSAGE_STUDENT_NOT_FOUND = "The student is not in the course you are given,"
            + " so you cannot access the profile.";
    private static final String MESSAGE_STUDENT_NOT_REGISTERED = "The student has not registered,"
            + " and does not have profile";

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (studentEmail == null || courseId == null) {
            // Student access his own profile
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException(MESSAGE_NOT_STUDENT_ACCOUNT);
            }
        } else {
            // Access someone else's profile
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null) {
                throw new UnauthorizedAccessException(MESSAGE_STUDENT_NOT_FOUND);
            }
            gateKeeper.verifyAccessibleForCurrentUserAsInstructorOrTeamMemberOrAdmin(userInfo.id, courseId,
                    student.section, studentEmail);
        }
    }

    @Override
    public ActionResult execute() {

        String studentId;
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (studentEmail == null || courseId == null) {
            studentId = userInfo.id;
        } else {
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null) {
                return new JsonResult("No student found", HttpStatus.SC_NOT_FOUND);
            }
            studentId = student.getGoogleId();
        }

        if (StringHelper.isEmpty(studentId)) {
            // The student has not registered.
            return new JsonResult(MESSAGE_STUDENT_NOT_REGISTERED, HttpStatus.SC_NOT_FOUND);
        }

        StudentProfileAttributes studentProfile = logic.getStudentProfile(studentId);
        String name = logic.getAccount(studentId).name;

        if (studentProfile == null) {
            // create one on the fly
            studentProfile = StudentProfileAttributes.builder(studentId).build();
        }

        StudentProfileData output = new StudentProfileData(name, studentProfile);
        // If student requesting and is not the student's own profile, hide some fields
        if (userInfo.isStudent && !userInfo.isInstructor && !studentId.equals(userInfo.id)) {
            output.hideInformationWhenViewedByOtherStudent();
        }

        return new JsonResult(output);
    }
}
