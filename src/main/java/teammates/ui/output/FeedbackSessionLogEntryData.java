package teammates.ui.output;

import teammates.ui.constants.LogType;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogEntryData {
    private final StudentData studentData;

    private final LogType feedbackSessionLogType;

    private final long timestamp;

    public FeedbackSessionLogEntryData(StudentData studentData,
                                   LogType feedbackSessionLogType,
                                   long timestamp) {
        this.studentData = studentData;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public StudentData getStudentData() {
        return studentData;
    }

    public LogType getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
