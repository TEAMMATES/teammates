package teammates.ui.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.logic.entity.DeadlineExtension;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;

/**
 * The API output format for deadline extensions.
 */
public class DeadlineExtensionsData extends ApiOutput {
    private Map<String, Long> studentDeadlines;
    private Map<String, Long> instructorDeadlines;

    private DeadlineExtensionsData() {
        // for Jackson deserialization
    }

    public DeadlineExtensionsData(List<DeadlineExtension> deadlineExtensions,
            Map<UUID, Student> studentsByUserId, Map<UUID, Instructor> instructorsByUserId) {
        this.studentDeadlines = new HashMap<>();
        this.instructorDeadlines = new HashMap<>();

        for (DeadlineExtension de : deadlineExtensions) {
            UUID userId = de.getUserId();
            long epochMilli = de.getEndTime().toEpochMilli();

            if (studentsByUserId.containsKey(userId)) {
                this.studentDeadlines.put(studentsByUserId.get(userId).getEmail(), epochMilli);
            }

            if (instructorsByUserId.containsKey(userId)) {
                this.instructorDeadlines.put(instructorsByUserId.get(userId).getEmail(), epochMilli);
            }
        }
    }

    public Map<String, Long> getStudentDeadlines() {
        return studentDeadlines;
    }

    public Map<String, Long> getInstructorDeadlines() {
        return instructorDeadlines;
    }

    public void setStudentDeadlines(Map<String, Long> studentDeadlines) {
        this.studentDeadlines = studentDeadlines;
    }

    public void setInstructorDeadlines(Map<String, Long> instructorDeadlines) {
        this.instructorDeadlines = instructorDeadlines;
    }
}
