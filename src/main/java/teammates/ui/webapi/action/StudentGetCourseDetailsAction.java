package teammates.ui.webapi.action;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: for student to get his/her own details in the course given.
 */
public class StudentGetCourseDetailsAction extends Action {
    // TODO: Write tests

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("An student account is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!hasStudentJoinedCourse(courseId)) {
            throw new UnauthorizedAccessException("The student is yet to join the course.");
        }

        CourseAttributes course = logic.getCourse(courseId);
        gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.id), course);

    }

    @Override
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
        if (student == null) {
            return new JsonResult(String.format("Student with id (%s) cannot be found.", userInfo.id),
                    HttpStatus.SC_NOT_FOUND);
        }
        student.lastName = null;
        student.comments = null;
        student.key = null;

        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        List<InstructorDetails> instructorDetails = new LinkedList<>();
        instructors.forEach(
                instructor -> {
                    if (instructor.isDisplayedToStudents()) {
                        instructorDetails.add(
                                new InstructorDetails(instructor.getDisplayedName(), instructor.getEmail()));
                    }
                });

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
                    teammateProfile.modifiedDate = null;

                    teammateProfiles.add(teammateProfile);
                }
            });
        }

        StudentGetCourseDetailsResult result = new StudentGetCourseDetailsResult(
                student, logic.getCourse(courseId), instructorDetails, teammateProfiles);

        return new JsonResult(result);
    }

    private boolean hasStudentJoinedCourse(String courseId) {
        if (userInfo != null && userInfo.isStudent) {
            return logic.getStudentForGoogleId(courseId, userInfo.id) != null;
        }
        return false;
    }

    /**
     * A data model to contain details of an instructor.
     */
    public static class InstructorDetails {

        private final String name;
        private final String email;

        public InstructorDetails(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            InstructorDetails that = (InstructorDetails) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(email, that.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, email);
        }
    }

    /**
     * Output format for {@link StudentGetCourseDetailsAction}.
     */
    public static class StudentGetCourseDetailsResult extends ApiOutput {

        private final StudentAttributes student;
        private final CourseAttributes course;
        private final List<InstructorDetails> instructorDetails;
        private final List<StudentProfileAttributes> teammateProfiles;

        public StudentGetCourseDetailsResult(StudentAttributes student, CourseAttributes course,
                                             List<InstructorDetails> instructorDetails,
                                             List<StudentProfileAttributes> teammateProfiles) {
            this.student = student;
            this.course = course;
            this.instructorDetails = instructorDetails;
            this.teammateProfiles = teammateProfiles;
        }

        public StudentAttributes getStudent() {
            return student;
        }

        public CourseAttributes getCourse() {
            return course;
        }

        public List<InstructorDetails> getInstructorDetails() {
            return instructorDetails;
        }

        public List<StudentProfileAttributes> getTeammateProfiles() {
            return teammateProfiles;
        }
    }
}
