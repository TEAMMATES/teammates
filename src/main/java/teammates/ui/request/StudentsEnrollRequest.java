package teammates.ui.request;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The request for enrolling a list of students.
 */
public class StudentsEnrollRequest extends BasicRequest {

    private static final String ERROR_MESSAGE_DUPLICATE_EMAIL =
            "Error, duplicated email addresses detected in the input: %s";
    private static final String ERROR_MESSAGE_EMPTY_ENROLLMENT = "The enroll line is empty. "
            + "Please input at least one student detail.";

    // Initialize to handle users make a http request with empty body.
    private List<StudentEnrollRequest> studentEnrollRequests;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public StudentsEnrollRequest(List<StudentEnrollRequest> studentEnrollRequests) {
        this.studentEnrollRequests = studentEnrollRequests;
    }

    public List<StudentEnrollRequest> getStudentEnrollRequests() {
        return studentEnrollRequests;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(!studentEnrollRequests.isEmpty(), ERROR_MESSAGE_EMPTY_ENROLLMENT);
        for (StudentEnrollRequest request : studentEnrollRequests) {
            request.validate();
        }

        Set<String> emails = new HashSet<>();
        for (StudentEnrollRequest request : studentEnrollRequests) {
            String normalizedEmail = request.getEmail();
            assertTrue(!emails.contains(normalizedEmail),
                    String.format(ERROR_MESSAGE_DUPLICATE_EMAIL, request.getEmail()));
            emails.add(normalizedEmail);
        }
    }
}
