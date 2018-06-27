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

    Map<String, StudentAttributes> studentListByEmail = new HashMap<>();
    Map<String, InstructorAttributes> instructorListByEmail = new HashMap<>();

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

    public String getCommentGiverNameFromEmail(String email, FeedbackParticipantType giverType) {
        if (giverType.equals(FeedbackParticipantType.TEAMS)) {
            return email;
        }
        if (giverType.equals(FeedbackParticipantType.STUDENTS)) {
            return studentListByEmail.get(email).name;
        }
        if (giverType.equals(FeedbackParticipantType.INSTRUCTORS)) {
            return instructorListByEmail.get(email).name;
        }
        return email;
    }

    public String getCommentRecipientNameFromEmail(String email, FeedbackParticipantType recipientType) {
        StudentAttributes student = getStudentForEmail(email);
        if (student != null) {
            return studentListByEmail.get(email).name;
        }
        InstructorAttributes instructor = getInstructorForEmail(email);
        if (instructor != null) {
            return instructorListByEmail.get(email).name;
        }
        if (recipientType.equals(FeedbackParticipantType.TEAMS)
                    || recipientType.equals((FeedbackParticipantType.OWN_TEAM))
                    || recipientType.equals(FeedbackParticipantType.SELF)) {
            return email;
        }
        if (email.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        return Const.USER_UNKNOWN_TEXT;
    }
}
