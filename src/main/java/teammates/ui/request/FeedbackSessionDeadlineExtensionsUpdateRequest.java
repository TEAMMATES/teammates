package teammates.ui.request;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The update request for deadline extensions of a feedback session.
 */
public class FeedbackSessionDeadlineExtensionsUpdateRequest extends BasicRequest {

    private Map<String, Long> studentDeadlines;
    private Map<String, Long> instructorDeadlines;

    /**
     * Gets the deadlines for students.
     */
    public Map<String, Instant> getStudentDeadlines() {
        return studentDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Instant.ofEpochMilli(entry.getValue())));
    }

    /**
     * Gets the deadlines for instructors.
     */
    public Map<String, Instant> getInstructorDeadlines() {
        return instructorDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Instant.ofEpochMilli(entry.getValue())));
    }

    public void setStudentDeadlines(Map<String, Long> studentDeadlines) {
        this.studentDeadlines = studentDeadlines;
    }

    public void setInstructorDeadlines(Map<String, Long> instructorDeadlines) {
        this.instructorDeadlines = instructorDeadlines;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(studentDeadlines != null, "deadlines for students cannot be null");
        assertTrue(instructorDeadlines != null, "deadlines for instructors cannot be null");
    }
}
