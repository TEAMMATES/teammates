package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Contains a list of students and instructors in a course. Useful for caching
 * a copy of student and instructor details of a course instead of reading
 * them from the database multiple times.
 */
public class CourseRoster {

    private final Map<String, Student> emailToStudents = new HashMap<>();
    private final Map<String, Instructor> emailToInstructors = new HashMap<>();
    private final Map<String, List<Student>> teamToMembers;
    private final Map<UUID, Student> idToStudents = new HashMap<>();
    private final Map<UUID, Instructor> idToInstructors = new HashMap<>();
    private final Map<UUID, Team> teamIdToTeam = new HashMap<>();

    public CourseRoster(List<Student> students, List<Instructor> instructors) {
        populateStudentList(students);
        populateInstructorList(instructors);
        teamToMembers = buildTeamToMembersTable(getStudents());
    }

    public List<Student> getStudents() {
        return new ArrayList<>(emailToStudents.values());
    }

    public List<Instructor> getInstructors() {
        return new ArrayList<>(emailToInstructors.values());
    }

    public Map<String, List<Student>> getTeamToMembers() {
        return teamToMembers;
    }

    /**
     * Checks whether a student is in course.
     */
    public boolean isStudentInCourse(String studentEmail) {
        return emailToStudents.containsKey(normalizeEmail(studentEmail));
    }

    /**
     * Checks whether a team is in course.
     */
    public boolean isTeamInCourse(String teamName) {
        return teamToMembers.containsKey(teamName);
    }

    /**
     * Checks whether a student is in team.
     */
    public boolean isStudentInTeam(String studentEmail, String targetTeamName) {
        Student student = emailToStudents.get(normalizeEmail(studentEmail));
        return student != null && student.getTeamName().equals(targetTeamName);
    }

    /**
     * Checks whether two students are in the same team.
     */
    public boolean isStudentsInSameTeam(String studentEmail1, String studentEmail2) {
        Student student1 = emailToStudents.get(normalizeEmail(studentEmail1));
        Student student2 = emailToStudents.get(normalizeEmail(studentEmail2));
        return student1 != null && student2 != null
                && student1.getTeam() != null && student1.getTeam().equals(student2.getTeam());
    }

    /**
     * Returns the student object for the given email.
     */
    public Student getStudentForEmail(String email) {
        return emailToStudents.get(normalizeEmail(email));
    }

    /**
     * Returns the instructor object for the given email.
     */
    public Instructor getInstructorForEmail(String email) {
        return emailToInstructors.get(normalizeEmail(email));
    }

    private void populateStudentList(List<Student> students) {
        if (students == null) {
            return;
        }

        for (Student s : students) {
            emailToStudents.put(normalizeEmail(s.getEmail()), s);
            idToStudents.put(s.getId(), s);
            teamIdToTeam.put(s.getTeamId(), s.getTeam());
        }
    }

    private void populateInstructorList(List<Instructor> instructors) {
        if (instructors == null) {
            return;
        }

        for (Instructor i : instructors) {
            emailToInstructors.put(normalizeEmail(i.getEmail()), i);
            idToInstructors.put(i.getId(), i);
        }
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.toLowerCase(Locale.ROOT);
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
        boolean isTeam = getTeamToMembers().containsKey(identifier);
        if (isStudent) {
            Student student = getStudentForEmail(identifier);

            name = student.getName();
            teamName = student.getTeamName();
            sectionName = student.getSectionName();
        } else if (isInstructor) {
            Instructor instructor = getInstructorForEmail(identifier);

            name = instructor.getName();
            teamName = Const.USER_TEAM_FOR_INSTRUCTOR;
        } else if (isTeam) {
            Student teamMember = getTeamToMembers().get(identifier).iterator().next();

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
