package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * Contains a list of students and instructors in a course. Useful for caching
 * a copy of student and instructor details of a course instead of reading
 * them from the database multiple times.
 */
public class CourseRoster {

    private final Map<String, StudentAttributes> studentListByEmail = new HashMap<>();
    private final Map<String, InstructorAttributes> instructorListByEmail = new HashMap<>();
    private final Map<String, List<StudentAttributes>> teamToMembersTable;

    public CourseRoster(List<StudentAttributes> students, List<InstructorAttributes> instructors) {
        populateStudentListByEmail(students);
        populateInstructorListByEmail(instructors);
        teamToMembersTable = buildTeamToMembersTable(getStudents());
    }

    public List<StudentAttributes> getStudents() {
        return new ArrayList<>(studentListByEmail.values());
    }

    public List<InstructorAttributes> getInstructors() {
        return new ArrayList<>(instructorListByEmail.values());
    }

    public Map<String, List<StudentAttributes>> getTeamToMembersTable() {
        return teamToMembersTable;
    }

    /**
     * Checks whether a student is in course.
     */
    public boolean isStudentInCourse(String studentEmail) {
        return studentListByEmail.containsKey(studentEmail);
    }

    /**
     * Checks whether a team is in course.
     */
    public boolean isTeamInCourse(String teamName) {
        return teamToMembersTable.containsKey(teamName);
    }

    /**
     * Checks whether a student is in team.
     */
    public boolean isStudentInTeam(String studentEmail, String targetTeamName) {
        StudentAttributes student = studentListByEmail.get(studentEmail);
        return student != null && student.getTeam().equals(targetTeamName);
    }

    /**
     * Checks whether two students are in the same team.
     */
    public boolean isStudentsInSameTeam(String studentEmail1, String studentEmail2) {
        StudentAttributes student1 = studentListByEmail.get(studentEmail1);
        StudentAttributes student2 = studentListByEmail.get(studentEmail2);
        return student1 != null && student2 != null
                && student1.getTeam() != null && student1.getTeam().equals(student2.getTeam());
    }

    /**
     * Returns the student object for the given email.
     */
    public StudentAttributes getStudentForEmail(String email) {
        return studentListByEmail.get(email);
    }

    /**
     * Returns the instructor object for the given email.
     */
    public InstructorAttributes getInstructorForEmail(String email) {
        return instructorListByEmail.get(email);
    }

    private void populateStudentListByEmail(List<StudentAttributes> students) {

        if (students == null) {
            return;
        }

        for (StudentAttributes s : students) {
            studentListByEmail.put(s.getEmail(), s);
        }
    }

    private void populateInstructorListByEmail(List<InstructorAttributes> instructors) {

        if (instructors == null) {
            return;
        }

        for (InstructorAttributes i : instructors) {
            instructorListByEmail.put(i.getEmail(), i);
        }
    }

    /**
     * Builds a Map from team name to team members.
     */
    public static Map<String, List<StudentAttributes>> buildTeamToMembersTable(List<StudentAttributes> students) {
        Map<String, List<StudentAttributes>> teamToMembersTable = new HashMap<>();
        // group students by team
        for (StudentAttributes studentAttributes : students) {
            teamToMembersTable.computeIfAbsent(studentAttributes.getTeam(), key -> new ArrayList<>())
                    .add(studentAttributes);
        }
        return teamToMembersTable;
    }

    /**
     * Gets info of a participant associated with an identifier in the course.
     *
     * @return an object {@link ParticipantInfo} containing the name, teamName and the sectionName.
     */
    public ParticipantInfo getInfoForIdentifier(String identifier) {
        String name = Const.USER_NOBODY_TEXT;
        String teamName = Const.USER_NOBODY_TEXT;
        String sectionName = Const.DEFAULT_SECTION;

        boolean isStudent = getStudentForEmail(identifier) != null;
        boolean isInstructor = getInstructorForEmail(identifier) != null;
        boolean isTeam = getTeamToMembersTable().containsKey(identifier);
        if (isStudent) {
            StudentAttributes student = getStudentForEmail(identifier);

            name = student.getName();
            teamName = student.getTeam();
            sectionName = student.getSection();
        } else if (isInstructor) {
            InstructorAttributes instructor = getInstructorForEmail(identifier);

            name = instructor.getName();
            teamName = Const.USER_TEAM_FOR_INSTRUCTOR;
            sectionName = Const.DEFAULT_SECTION;
        } else if (isTeam) {
            StudentAttributes teamMember = getTeamToMembersTable().get(identifier).iterator().next();

            name = identifier;
            teamName = identifier;
            sectionName = teamMember.getSection();
        }

        return new ParticipantInfo(name, teamName, sectionName);
    }

    /**
     * Simple data transfer object containing the information of a participant.
     */
    public static final class ParticipantInfo {

        private final String name;
        private final String teamName;
        private final String sectionName;

        private ParticipantInfo(String name, String teamName, String sectionName) {
            this.name = name;
            this.teamName = teamName;
            this.sectionName = sectionName;
        }

        public String getName() {
            return name;
        }

        public String getTeamName() {
            return teamName;
        }

        public String getSectionName() {
            return sectionName;
        }
    }
}
