package teammates.ui.newcontroller;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: for student to get his/her own details in the course given.
 */
public class StudentGetCourseDetailsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("An student account is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!hasStudentJoinedCourse(courseId)) {
            throw new UnauthorizedAccessException("The student is yet to join the course.");
        }

        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            return new JsonResult(String.format("Course with the given course id (%s) cannot be found.",
                    courseId), HttpStatus.SC_NOT_FOUND);
        }
        gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.id), course);

        StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
        if (student == null) {
            return new JsonResult(String.format("Student with id (%s) cannot be found.", userInfo.id),
                    HttpStatus.SC_NOT_FOUND);
        }
        student.lastName = null;
        student.comments = null;
        student.key = null;

        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        List<String> instructorNames = new LinkedList<>();
        if (instructors != null) {
            instructors.forEach(instructor -> instructorNames.add(instructor.getName()));
        }

        TeamDetailsBundle teamDetails = logic.getTeamDetailsForStudent(student);
        if (teamDetails == null) {
            return new JsonResult(String.format("Team for the student with id (%s) cannot be found.", userInfo.id),
                    HttpStatus.SC_NOT_FOUND);
        }

        List<StudentProfileAttributes> teammateProfiles = new LinkedList<>();
        if (teamDetails.students != null) {
            teamDetails.students.removeIf(st -> st.googleId.equals(userInfo.id));

            teamDetails.students.forEach(teammate -> {
                StudentProfileAttributes teammateProfile = logic.getStudentProfile(teammate.googleId);
                if (teammateProfile != null) {
                    teammateProfile.googleId = null;
                    teammateProfile.email = null;
                    teammateProfile.modifiedDate = null;

                    teammateProfiles.add(teammateProfile);
                }
            });
        }

        StudentGetCourseDetailsResult result = new StudentGetCourseDetailsResult(
                student, course, instructorNames, teammateProfiles);

        return new JsonResult(result);
    }

    private boolean hasStudentJoinedCourse(String courseId) {
        if (userInfo != null && userInfo.isStudent) {
            return logic.getStudentForGoogleId(courseId, userInfo.id) != null;
        }
        return false;
    }

    /**
     * Output format for {@link StudentGetCourseDetailsAction}.
     */
    public static class StudentGetCourseDetailsResult extends ActionResult.ActionOutput {

        private final StudentAttributes student;
        private final CourseAttributes course;
        private final List<String> instructorNames;
        private final List<StudentProfileAttributes> teammateProfiles;

        public StudentGetCourseDetailsResult(StudentAttributes student, CourseAttributes course,
                                             List<String> instructorNames,
                                             List<StudentProfileAttributes> teammateProfiles) {
            this.student = student;
            this.course = course;
            this.instructorNames = instructorNames;
            this.teammateProfiles = teammateProfiles;
        }

        public StudentAttributes getStudent() {
            return student;
        }

        public CourseAttributes getCourse() {
            return course;
        }

        public List<String> getInstructorNames() {
            return instructorNames;
        }

        public List<StudentProfileAttributes> getTeammateProfiles() {
            return teammateProfiles;
        }
    }
}
