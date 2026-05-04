package teammates.ui.output;

import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Student;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogEntryData {
    private final UUID feedbackSessionLogEntryId;
    private final StudentData studentData;
    private final FeedbackSessionLogType feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntryData(FeedbackSessionLog logEntry, Student student) {
        this.feedbackSessionLogEntryId = logEntry.getId();
        this.studentData = new StudentData(student);
        this.feedbackSessionLogType = logEntry.getFeedbackSessionLogType();
        this.timestamp = logEntry.getTimestamp().toEpochMilli();
    }

    public UUID getFeedbackSessionLogEntryId() {
        return feedbackSessionLogEntryId;
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
