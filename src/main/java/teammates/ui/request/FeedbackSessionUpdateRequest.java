package teammates.ui.request;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The update request of a feedback session.
 */
public class FeedbackSessionUpdateRequest extends FeedbackSessionBasicRequest {

    private Map<String, Long> studentDeadlines;
    private Map<String, Long> instructorDeadlines;

    private boolean isGoingToNotifyAboutDeadlines;

    /**
     * Gets the deadlines for students.
     */
    public Map<String, Instant> getStudentDeadlines() {
        Map<String, Instant> studentDeadlineInstants = new HashMap<>();
        studentDeadlines.forEach((emailAddress, deadline) -> {
            studentDeadlineInstants.put(emailAddress, Instant.ofEpochMilli(deadline));
        });
        return studentDeadlineInstants;
    }

    /**
     * Gets the deadlines for instructors.
     */
    public Map<String, Instant> getInstructorDeadlines() {
        Map<String, Instant> instructorDeadlineInstants = new HashMap<>();
        instructorDeadlines.forEach((emailAddress, deadline) -> {
            instructorDeadlineInstants.put(emailAddress, Instant.ofEpochMilli(deadline));
        });
        return instructorDeadlineInstants;
    }

    public boolean isGoingToNotifyAboutDeadlines() {
        return isGoingToNotifyAboutDeadlines;
    }

    public void setStudentDeadlines(Map<String, Long> studentDeadlines) {
        this.studentDeadlines = studentDeadlines;
    }

    public void setInstructorDeadlines(Map<String, Long> instructorDeadlines) {
        this.instructorDeadlines = instructorDeadlines;
    }

    public void setGoingToNotifyAboutDeadlines(boolean isGoingToNotifyAboutDeadlines) {
        this.isGoingToNotifyAboutDeadlines = isGoingToNotifyAboutDeadlines;
    }
}
