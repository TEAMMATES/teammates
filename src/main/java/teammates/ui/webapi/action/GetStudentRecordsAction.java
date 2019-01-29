package teammates.ui.webapi.action;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets records of a student in a course.
 */
public class GetStudentRecordsAction extends Action {

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
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));
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

        List<String> sessionNames = logic.getFeedbackSessionsListForInstructor(userInfo.id, false).stream()
                .filter(tempFs -> tempFs.getCourseId().equals(courseId) && instructor.isAllowedForPrivilege(
                        student.section, tempFs.getFeedbackSessionName(),
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS))
                .sorted(FeedbackSessionAttributes.DESCENDING_ORDER)
                .map(session -> session.getFeedbackSessionName())
                .collect(Collectors.toList());

        boolean isInstructorAllowedToViewStudent = instructor.isAllowedForPrivilege(student.section,
                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        StudentProfileAttributes studentProfile = !isInstructorAllowedToViewStudent || student.googleId.isEmpty()
                ? null : logic.getStudentProfile(student.googleId);
        if (studentProfile != null) {
            studentProfile.googleId = null;
            studentProfile.modifiedDate = null;
        }

        StudentRecords output = new StudentRecords(courseId, student.name, student.email, studentProfile, sessionNames);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetStudentRecordsAction}.
     */
    public static class StudentRecords extends ApiOutput {
        private final String courseId;
        private final String studentName;
        private final String studentEmail;
        private final StudentProfileAttributes studentProfile;
        private final List<String> sessionNames;

        StudentRecords(String courseId, String studentName, String studentEmail, StudentProfileAttributes studentProfile,
                       List<String> sessionNames) {
            this.courseId = courseId;
            this.studentName = studentName;
            this.studentEmail = studentEmail;
            this.studentProfile = studentProfile;
            this.sessionNames = sessionNames;
        }

        public List<String> getSessionNames() {
            return sessionNames;
        }

        public StudentProfileAttributes getStudentProfile() {
            return studentProfile;
        }

        public String getStudentEmail() {
            return studentEmail;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getCourseId() {
            return courseId;
        }

    }

}
