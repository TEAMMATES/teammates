package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Contains a list of students and instructors in a course. Useful for caching
 * a copy of student and instructor details of a course instead of reading
 * them from the database multiple times.
 */
public class SqlCourseRoster {

    private final Map<String, Student> studentListByEmail = new HashMap<>();
    private final Map<String, Instructor> instructorListByEmail = new HashMap<>();
    private final Map<String, List<Student>> teamToMembersTable;

    public SqlCourseRoster(List<Student> students, List<Instructor> instructors) {
        populateStudentListByEmail(students);
        populateInstructorListByEmail(instructors);
        teamToMembersTable = buildTeamToMembersTable(getStudents());
    }

    public List<Student> getStudents() {
        return new ArrayList<>(studentListByEmail.values());
    }

    public List<Instructor> getInstructors() {
        return new ArrayList<>(instructorListByEmail.values());
    }

    public Map<String, List<Student>> getTeamToMembersTable() {
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
        Student student = studentListByEmail.get(studentEmail);
        return student != null && student.getTeamName().equals(targetTeamName);
    }

    /**
     * Checks whether two students are in the same team.
     */
    public boolean isStudentsInSameTeam(String studentEmail1, String studentEmail2) {
        Student student1 = studentListByEmail.get(studentEmail1);
        Student student2 = studentListByEmail.get(studentEmail2);
        return student1 != null && student2 != null
                && student1.getTeam() != null && student1.getTeam().equals(student2.getTeam());
    }

    /**
     * Returns the student object for the given email.
     */
    public Student getStudentForEmail(String email) {
        return studentListByEmail.get(email);
    }

    /**
     * Returns the instructor object for the given email.
     */
    public Instructor getInstructorForEmail(String email) {
        return instructorListByEmail.get(email);
    }

    private void populateStudentListByEmail(List<Student> students) {

        if (students == null) {
            return;
        }

        for (Student s : students) {
            studentListByEmail.put(s.getEmail(), s);
        }
    }

    private void populateInstructorListByEmail(List<Instructor> instructors) {

        if (instructors == null) {
            return;
        }

        for (Instructor i : instructors) {
            instructorListByEmail.put(i.getEmail(), i);
        }
    }

    /**
     * Builds a Map from team name to team members.
     */
    public static Map<String, List<Student>> buildTeamToMembersTable(List<Student> students) {
        Map<String, List<Student>> teamToMembersTable = new HashMap<>();
        // group students by team
        for (Student student : students) {
            teamToMembersTable.computeIfAbsent(student.getTeamName(), key -> new ArrayList<>())
                    .add(student);
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
            Student student = getStudentForEmail(identifier);

            name = student.getName();
            teamName = student.getTeamName();
            sectionName = student.getSectionName();
        } else if (isInstructor) {
            Instructor instructor = getInstructorForEmail(identifier);

            name = instructor.getName();
            teamName = Const.USER_TEAM_FOR_INSTRUCTOR;
            sectionName = Const.DEFAULT_SECTION;
        } else if (isTeam) {
            Student teamMember = getTeamToMembersTable().get(identifier).iterator().next();

            name = identifier;
            teamName = identifier;
            sectionName = teamMember.getSectionName();
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
