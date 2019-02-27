package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets details of a student in a course.
 */
public class GetCourseStudentDetailsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException(
                new EntityDoesNotExistException("No student with given email in given course."));
        }
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
    }

    @Override
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return new JsonResult("No student with given email in given course.", HttpStatus.SC_NOT_FOUND);
        }
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);

        boolean hasSection = false;
        try {
            hasSection = logic.hasIndicatedSections(courseId);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No course with given id.", HttpStatus.SC_NOT_FOUND);
        }

        StudentProfileAttributes studentProfile = null;
        if (student.isRegistered()) {
            studentProfile = logic.getStudentProfile(student.googleId);
        }

        student.googleId = null;
        student.key = null;
        if (studentProfile != null) {
            studentProfile.googleId = null;
            studentProfile.modifiedDate = null;
        }
        StudentInfo output = new StudentInfo(student, studentProfile, hasSection);
        return new JsonResult(output);

    }

    /**
     * Output format for {@link GetCourseStudentDetailsAction}.
     */
    public static class StudentInfo extends ApiOutput {

        private final StudentAttributes student;
        private final StudentProfileAttributes studentProfile;
        private final boolean hasSection;

        public StudentInfo(StudentAttributes student,
                           StudentProfileAttributes studentProfile, boolean hasSection) {
            this.student = student;
            this.studentProfile = studentProfile;
            this.hasSection = hasSection;
        }

        public boolean isHasSection() {
            return hasSection;
        }

        public StudentProfileAttributes getStudentProfile() {
            return studentProfile;
        }

        public StudentAttributes getStudent() {
            return student;
        }
    }
}
