package teammates.ui.request;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The request specifying deadline extensions to create, update or revoke.
 */
public class DeadlineExtensionsRequest extends BasicRequest {

    private String courseId;
    private String feedbackSessionName;
    private boolean notifyUsers;
    private Map<String, Long> oldStudentDeadlines;
    private Map<String, Long> newStudentDeadlines;
    private Map<String, Long> oldInstructorDeadlines;
    private Map<String, Long> newInstructorDeadlines;

    public DeadlineExtensionsRequest(String courseId, String feedbackSessionName, boolean notifyUsers,
            Map<String, Instant> oldStudentDeadlines, Map<String, Instant> newStudentDeadlines,
            Map<String, Instant> oldInstructorDeadlines, Map<String, Instant> newInstructorDeadlines) {
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.notifyUsers = notifyUsers;
        this.oldStudentDeadlines = convertDeadlinesFromInstantToLong(oldStudentDeadlines);
        this.newStudentDeadlines = convertDeadlinesFromInstantToLong(newStudentDeadlines);
        this.oldInstructorDeadlines = convertDeadlinesFromInstantToLong(oldInstructorDeadlines);
        this.newInstructorDeadlines = convertDeadlinesFromInstantToLong(newInstructorDeadlines);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean getNotifyUsers() {
        return notifyUsers;
    }

    public Map<String, Instant> getOldStudentDeadlines() {
        return convertDeadlinesFromLongToInstant(oldStudentDeadlines);
    }

    public Map<String, Instant> getNewStudentDeadlines() {
        return convertDeadlinesFromLongToInstant(newStudentDeadlines);
    }

    public Map<String, Instant> getOldInstructorDeadlines() {
        return convertDeadlinesFromLongToInstant(oldInstructorDeadlines);
    }

    public Map<String, Instant> getNewInstructorDeadlines() {
        return convertDeadlinesFromLongToInstant(newInstructorDeadlines);
    }

    private Map<String, Long> convertDeadlinesFromInstantToLong(Map<String, Instant> deadlines) {
        assert deadlines != null;

        return deadlines.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toEpochMilli()));
    }

    private Map<String, Instant> convertDeadlinesFromLongToInstant(Map<String, Long> deadlines) {
        return deadlines.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Instant.ofEpochMilli(entry.getValue())));
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(courseId != null, "Course ID cannot be null");
        assertTrue(feedbackSessionName != null, "Feedback session name cannot be null");
        assertTrue(oldStudentDeadlines != null, "Old student deadlines cannot be null");
        assertTrue(newStudentDeadlines != null, "New student deadlines cannot be null");
        assertTrue(oldInstructorDeadlines != null, "Old instructor deadlines cannot be null");
        assertTrue(newInstructorDeadlines != null, "New instructor deadlines cannot be null");
    }

}
