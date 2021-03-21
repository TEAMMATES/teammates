package teammates.ui.output;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.ui.constants.LogType;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogEntryData {
    private final StudentData studentData;
    private final LogType feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntryData(FeedbackSessionLogEntry logEntry) {
        StudentAttributes student = logEntry.getStudent();
        StudentData studentData = new StudentData(student);
        LogType logType = LogType.valueOfLabel(logEntry.getFeedbackSessionLogType());
        long timestamp = logEntry.getTimestamp();
        this.studentData = studentData;
        this.feedbackSessionLogType = logType;
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
