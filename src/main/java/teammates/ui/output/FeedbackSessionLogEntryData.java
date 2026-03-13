package teammates.ui.output;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogEntryData {
    private final StudentData studentData;
    private final FeedbackSessionLogType feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntryData(FeedbackSessionLog logEntry, Student student) {
        StudentData studentData = new StudentData(student);
        FeedbackSessionLogType logType = logEntry.getFeedbackSessionLogType();
        long timestamp = logEntry.getTimestamp().toEpochMilli();
        this.studentData = studentData;
        this.feedbackSessionLogType = logType;
        this.timestamp = timestamp;
    }

    public StudentData getStudentData() {
        return studentData;
    }

    public FeedbackSessionLogType getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
