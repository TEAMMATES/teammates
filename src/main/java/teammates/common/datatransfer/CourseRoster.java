package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * Contains a list of students and instructors in a course. Useful for caching
 * a copy of student and instructor details of a course instead of reading
 * them from the database multiple times.
 */
public class CourseRoster {

    private static final int EMAIL_NAME_PAIR = 0;
    private static final int EMAIL_TEAMNAME_PAIR = 2;

    Map<String, StudentAttributes> studentListByEmail = new HashMap<>();
    Map<String, InstructorAttributes> instructorListByEmail = new HashMap<>();
    Map<String, String> emailToNameTable = new HashMap<>();
    Map<String, String> emailToTeamNameTable = new HashMap<>();

    public CourseRoster(List<StudentAttributes> students, List<InstructorAttributes> instructors) {
        populateStudentListByEmail(students);
        populateInstructorListByEmail(instructors);
    }

    public List<StudentAttributes> getStudents() {
        return new ArrayList<>(studentListByEmail.values());
    }

    public List<InstructorAttributes> getInstructors() {
        return new ArrayList<>(instructorListByEmail.values());
    }

    /**
     * Checks if an instructor is the instructor of a course by providing an email address.
     * @param instructorEmail email of the instructor to be checked.
     * @return true if the instructor is an instructor of the course
     */
    public boolean isInstructorOfCourse(String instructorEmail) {
        return instructorListByEmail.containsKey(instructorEmail);
    }

    public boolean isStudentInCourse(String studentEmail) {
        return studentListByEmail.containsKey(studentEmail);
    }

    public boolean isStudentInTeam(String studentEmail, String targetTeamName) {
        StudentAttributes student = studentListByEmail.get(studentEmail);
        return student != null && student.team.equals(targetTeamName);
    }

    public boolean isStudentsInSameTeam(String studentEmail1, String studentEmail2) {
        StudentAttributes student1 = studentListByEmail.get(studentEmail1);
        StudentAttributes student2 = studentListByEmail.get(studentEmail2);
        return student1 != null && student2 != null
               && student1.team != null && student1.team.equals(student2.team);
    }

    public StudentAttributes getStudentForEmail(String email) {
        return studentListByEmail.get(email);
    }

    public InstructorAttributes getInstructorForEmail(String email) {
        return instructorListByEmail.get(email);
    }

    /**
     * Returns a map of email mapped to name of instructors and students of the course.
     * @return Map in which key is email of student/instructor and value is name.
     */
    public Map<String, String> getEmailToNameTableFromRoster() {
        Map<String, String> commentGiverEmailNameTable = new HashMap<>();
        List<InstructorAttributes> instructorList = getInstructors();
        for (InstructorAttributes instructor : instructorList) {
            commentGiverEmailNameTable.put(instructor.email, instructor.name);
        }

        List<StudentAttributes> studentList = getStudents();
        for (StudentAttributes student : studentList) {
            commentGiverEmailNameTable.put(student.email, student.name);
        }
        return commentGiverEmailNameTable;
    }

    private void populateStudentListByEmail(List<StudentAttributes> students) {

        if (students == null) {
            return;
        }

        for (StudentAttributes s : students) {
            studentListByEmail.put(s.email, s);
        }
    }

    private void populateInstructorListByEmail(List<InstructorAttributes> instructors) {

        if (instructors == null) {
            return;
        }

        for (InstructorAttributes i : instructors) {
            instructorListByEmail.put(i.email, i);
        }
    }

    /**
     * Adds giver and recipient of response to emailToNameTable.
     * @param response Feedback response
     * @param question Feedback question
     */
    public void addEmailNamePairsToTable(FeedbackResponseAttributes response,
                                          FeedbackQuestionAttributes question) {
        // keys of emailToNameTable are participantIdentifiers,
        // which consists of students' email, instructors' email, team names, or %GENERAL%.
        // participants identifiers of anonymous responses are not anonymised in the tables
        addEmailNamePairsToTable(response, question, EMAIL_NAME_PAIR);
    }

    /**
     * Adds giver and recipient of response to emailToTeamNameTable.
     * @param response Feedback response
     * @param question Feedback question
     */
    public void addEmailTeamNamePairsToTable(FeedbackResponseAttributes response, FeedbackQuestionAttributes question) {
        addEmailNamePairsToTable(response, question, EMAIL_TEAMNAME_PAIR);
    }

    private void addEmailNamePairsToTable(FeedbackResponseAttributes response,
                                          FeedbackQuestionAttributes question,
                                          int pairType) {
        if (question.giverType == FeedbackParticipantType.TEAMS
                    && isStudentInCourse(response.giver)) {
            emailToNameTable.putIfAbsent(
                    response.giver + Const.TEAM_OF_EMAIL_OWNER,
                    getNameTeamNamePairForEmail(question.giverType,
                            response.giver)[pairType]);

            StudentAttributes studentGiver = getStudentForEmail(response.giver);
            if (studentGiver != null) {
                emailToNameTable.putIfAbsent(studentGiver.team, getNameTeamNamePairForEmail(
                        question.giverType,
                        response.giver)[pairType]);
            }
        } else {
            emailToNameTable.putIfAbsent(
                    response.giver,
                    getNameTeamNamePairForEmail(question.giverType,
                            response.giver)[pairType]);
        }

        FeedbackParticipantType recipientType = null;
        if (question.recipientType == FeedbackParticipantType.SELF) {
            recipientType = question.giverType;
        } else {
            recipientType = question.recipientType;
        }

        emailToNameTable.putIfAbsent(
                response.recipient,
                getNameTeamNamePairForEmail(recipientType,
                        response.recipient)[pairType]);
    }

    // return a pair of String that contains Giver/Recipient'sName (at index 0)
    // and TeamName (at index 1)
    private String[] getNameTeamNamePairForEmail(FeedbackParticipantType type,
                                                 String email) {
        String giverRecipientName = null;
        String giverRecipientLastName = null;
        String teamName = null;
        String name = null;
        String lastName = null;
        String team = null;

        StudentAttributes student = getStudentForEmail(email);
        boolean isStudent = student != null;
        if (isStudent) {
            name = student.name;
            team = student.team;
            lastName = student.lastName;
        } else {
            InstructorAttributes instructor = getInstructorForEmail(email);
            boolean isInstructor = instructor != null;
            if (isInstructor) {
                name = instructor.name;
                lastName = instructor.name;
                team = Const.USER_TEAM_FOR_INSTRUCTOR;
            } else {
                if (email.equals(Const.GENERAL_QUESTION)) {
                    // Email represents that there is no specific recipient.
                    name = Const.USER_IS_NOBODY;
                    lastName = Const.USER_IS_NOBODY;
                    team = email;
                } else {
                    // The email represents a missing *Attribute.
                    // It might be a team name or the *Attribute has been deleted.
                    name = Const.USER_IS_MISSING;
                    lastName = Const.USER_IS_MISSING;
                    team = email;
                }
            }
        }

        if (type == FeedbackParticipantType.TEAMS || type == FeedbackParticipantType.OWN_TEAM) {
            giverRecipientName = team;
            giverRecipientLastName = team;
            teamName = "";
        } else {
            giverRecipientName = name;
            giverRecipientLastName = lastName;
            if (name.equals(Const.USER_IS_NOBODY) || name.equals(Const.USER_IS_MISSING)) {
                teamName = "";
            } else {
                teamName = team;
            }
        }
        return new String[] { giverRecipientName, giverRecipientLastName, teamName };
    }

    /**
     * Returns name associated with the email. It can be either email of student/instructor or
     * name of a team.
     * @param feedbackParticipantEmail email of student/instructor or name of team
     * @return name of student/instructor/team
     */
    public String getNameForEmail(String feedbackParticipantEmail) {
        String name = emailToNameTable.get(feedbackParticipantEmail);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        }
        if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(feedbackParticipantEmail);
        }
        return name;
    }

    /**
     * Returns team name associated with email
     * @param feedbackParticipantEmail email of the feedback participant
     * @return team name.
     */
    public String getTeamNameForEmail(String feedbackParticipantEmail) {
        String teamName = emailToTeamNameTable.get(feedbackParticipantEmail);
        if (teamName == null || feedbackParticipantEmail.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        return teamName;
    }
}
