package teammates.ui.request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.util.Const;

/**
 * The request for enrolling a list of students.
 */
public class StudentsEnrollRequest extends BasicRequest {

    static final int SIZE_LIMIT_PER_ENROLLMENT = 100;
    private static final String ERROR_MESSAGE_DUPLICATE_EMAIL =
            "Error, duplicated email addresses detected in the input: %s";
    private static final String ERROR_MESSAGE_TOO_MANY_ENROLLMENTS = "You are trying to enroll more than 100 students. "
            + "To avoid performance problems, please enroll no more than 100 students at a time.";
    private static final String ERROR_MESSAGE_EMPTY_ENROLLMENT = "The enroll line is empty. "
            + "Please input at least one student detail.";

    // Initialize to handle users make a http request with empty body.
    private List<StudentEnrollRequest> studentEnrollRequests = new ArrayList<>();

    public StudentsEnrollRequest(List<StudentEnrollRequest> studentEnrollRequests) {
        this.studentEnrollRequests = studentEnrollRequests;
    }

    public List<StudentEnrollRequest> getStudentEnrollRequests() {
        return studentEnrollRequests;
    }

    @Override
    public void validate() {
        assertTrue(!studentEnrollRequests.isEmpty(), ERROR_MESSAGE_EMPTY_ENROLLMENT);
        assertTrue(studentEnrollRequests.size() <= SIZE_LIMIT_PER_ENROLLMENT, ERROR_MESSAGE_TOO_MANY_ENROLLMENTS);
        for (StudentEnrollRequest request : studentEnrollRequests) {
            request.validate();
        }

        Set<String> emails = new HashSet<>();
        for (StudentEnrollRequest request : studentEnrollRequests) {
            assertTrue(!emails.contains(request.getEmail()),
                    String.format(ERROR_MESSAGE_DUPLICATE_EMAIL, request.getEmail()));
            emails.add(request.getEmail());
        }
    }

    /**
     * The request for enrolling a student.
     */
    public static class StudentEnrollRequest extends BasicRequest {

        private String name;
        private String email;
        private String team;
        private String section;
        private String comments;

        public StudentEnrollRequest(String name, String email, String team, String section, String comments) {
            this.name = name;
            this.email = email;
            this.team = team;
            this.section = section;
            this.comments = comments;
        }

        @Override
        public void validate() {
            assertTrue(name != null && !name.isEmpty(), "Student name cannot be empty");
            assertTrue(email != null && !email.isEmpty(), "Student email cannot be empty");
            assertTrue(team != null && !team.isEmpty(), "Team cannot be empty");
            assertTrue(section != null, "Section cannot be null");
            assertTrue(comments != null, "Comments cannot be null");
        }

        public String getName() {
            return this.name;
        }

        public String getEmail() {
            return this.email;
        }

        public String getTeam() {
            return this.team;
        }

        public String getSection() {
            return this.section.isEmpty() ? Const.DEFAULT_SECTION : this.section;
        }

        public String getComments() {
            return this.comments;
        }
    }
}
