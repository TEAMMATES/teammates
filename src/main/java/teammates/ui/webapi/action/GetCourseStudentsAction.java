package teammates.ui.webapi.action;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: for an instructor to get his/her courses and relevant sections, teams and students given the course ids.
 */
public class GetCourseStudentsAction extends Action {

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

        if (!hasInstructorJoinedCourse(courseId)) {
            throw new UnauthorizedAccessException(
                    String.format("The instructor is yet to join the course of id %s", courseId));
        }

        InstructorAttributes self = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(self, logic.getCourse(courseId));
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseAttributes course = logic.getCourse(courseId);
        if (course != null) {
            List<SectionDetails> sections = getSectionsForCourse(courseId);
            CourseDetails courseDetails = new CourseDetails(course.getId(), course.getName(), course.createdAt, sections);
            return new JsonResult(new GetCourseStudentsActionResult(courseDetails));
        }

        return new JsonResult(String.format("Course with id (%s) cannot be found.", courseId),
                HttpStatus.SC_NOT_FOUND);
    }

    private List<SectionDetails> getSectionsForCourse(String courseId) {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

        try {
            List<SectionDetails> sections = new ArrayList<>();

            List<SectionDetailsBundle> courseSectionDetails = logic.getSectionsForCourse(courseId);
            courseSectionDetails.forEach(section -> {
                List<TeamDetails> teams = getTeamsForSection(courseId, section);
                boolean isAllowedToViewStudents = instructor.isAllowedForPrivilege(
                        section.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
                boolean isAllowedToEditStudents = instructor.isAllowedForPrivilege(
                        section.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
                sections.add(new SectionDetails(
                        section.name, teams, isAllowedToViewStudents, isAllowedToEditStudents, courseId));
            });

            return sections;
        } catch (EntityDoesNotExistException e) {
            return new ArrayList<>();
        }
    }

    private List<TeamDetails> getTeamsForSection(String courseId, SectionDetailsBundle section) {
        List<TeamDetailsBundle> teamDetails = section.teams;
        List<TeamDetails> teams = new ArrayList<>();

        teamDetails.forEach(teamDetail -> {
            List<StudentDetails> students = getStudentsForTeam(courseId, section.name, teamDetail);
            teams.add(new TeamDetails(teamDetail.name, students, section.name, courseId));
        });

        return teams;
    }

    private List<StudentDetails> getStudentsForTeam(String courseId, String sectionName, TeamDetailsBundle team) {
        List<StudentDetails> students = new ArrayList<>();

        team.students.forEach(student -> {
            StudentDetails studentDetails = new StudentDetails(
                    student.name, student.email, student.getStudentStatus(), team.name, sectionName, courseId);
            students.add(studentDetails);
        });

        return students;
    }

    private boolean hasInstructorJoinedCourse(String courseId) {
        if (userInfo != null && userInfo.isInstructor) {
            return logic.getInstructorForGoogleId(courseId, userInfo.id) != null;
        }
        return false;
    }

    /**
     * ActionResult: a data model for the response of this action.
     */
    public static class GetCourseStudentsActionResult extends ApiOutput {
        private final CourseDetails course;

        public GetCourseStudentsActionResult(CourseDetails course) {
            this.course = course;
        }

        public CourseDetails getCourse() {
            return course;
        }
    }

    /**
     * A data model for a course containing information about its sections, teams and students.
     */
    public static class CourseDetails {
        private final String id;
        private final String name;
        private final Instant createdAt;
        private final List<SectionDetails> sections;

        public CourseDetails(String id, String name, Instant createdAt, List<SectionDetails> sections) {
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
            this.sections = sections;

            this.sections.sort(Comparator.comparing(SectionDetails::getName));
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public List<SectionDetails> getSections() {
            return sections;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CourseDetails that = (CourseDetails) o;
            return Objects.equals(id, that.id)
                    && Objects.equals(name, that.name)
                    && Objects.equals(createdAt, that.createdAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, createdAt, sections);
        }
    }

    /**
     * A data model for a section containing information about its teams and students.
     */
    public static class SectionDetails {
        private final String name;
        private final List<TeamDetails> teams;
        private final boolean isAllowedToViewStudents;
        private final boolean isAllowedToEditStudents;
        private final String courseId;

        public SectionDetails(String name, List<TeamDetails> teams, boolean isAllowedToViewStudents,
                              boolean isAllowedToEditStudents, String courseId) {
            this.name = name;
            this.teams = teams;
            this.isAllowedToViewStudents = isAllowedToViewStudents;
            this.isAllowedToEditStudents = isAllowedToEditStudents;
            this.courseId = courseId;

            this.teams.sort(Comparator.comparing(TeamDetails::getName));
        }

        public String getName() {
            return name;
        }

        public List<TeamDetails> getTeams() {
            return teams;
        }

        public boolean isAllowedToViewStudents() {
            return isAllowedToViewStudents;
        }

        public boolean isAllowedToEditStudents() {
            return isAllowedToEditStudents;
        }

        public String getCourseId() {
            return courseId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SectionDetails that = (SectionDetails) o;
            return isAllowedToViewStudents == that.isAllowedToViewStudents
                    && isAllowedToEditStudents == that.isAllowedToEditStudents
                    && Objects.equals(name, that.name)
                    && Objects.equals(courseId, that.courseId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name, isAllowedToViewStudents, isAllowedToEditStudents, courseId);
        }
    }

    /**
     * A data model for a team containing information about its students.
     */
    public static class TeamDetails {
        private final String name;
        private final List<StudentDetails> students;
        private final String section;
        private final String courseId;

        public TeamDetails(String name, List<StudentDetails> students, String section, String courseId) {
            this.name = name;
            this.students = students;
            this.section = section;
            this.courseId = courseId;

            this.students.sort(Comparator.comparing(StudentDetails::getName));
        }

        public String getName() {
            return name;
        }

        public List<StudentDetails> getStudents() {
            return students;
        }

        public String getSection() {
            return section;
        }

        public String getCourseId() {
            return courseId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TeamDetails that = (TeamDetails) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(section, that.section)
                    && Objects.equals(courseId, that.courseId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name, section, courseId);
        }
    }

    /**
     * A data model for a student in a specific course.
     */
    public static class StudentDetails {
        private final String name;
        private final String email;
        private final String status;
        private final String team;
        private final String section;
        private final String courseId;

        public StudentDetails(String name, String email, String status, String team, String section, String courseId) {
            this.name = name;
            this.email = email;
            this.status = status;
            this.team = team;
            this.section = section;
            this.courseId = courseId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getStatus() {
            return status;
        }

        public String getTeam() {
            return team;
        }

        public String getSection() {
            return section;
        }

        public String getCourseId() {
            return courseId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StudentDetails that = (StudentDetails) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(email, that.email)
                    && Objects.equals(status, that.status)
                    && Objects.equals(team, that.team)
                    && Objects.equals(section, that.section)
                    && Objects.equals(courseId, that.courseId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name, email, status, team, section, courseId);
        }
    }
}
