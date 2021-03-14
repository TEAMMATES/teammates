package teammates.ui.output;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogEntry {
    private final StudentData studentData;

    private final String feedbackSessionLogType;

    private final long timestamp;

    public FeedbackSessionLogEntry(StudentData studentData,
                                   String feedbackSessionLogType,
                                   long timestamp) {
        this.studentData = studentData;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public StudentData getStudentData() {
        return studentData;
    }

    public String getFeedbackSessionLogType() {
        // TODO: change type from String to LogType
        return feedbackSessionLogType;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
