package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;

/**
 * The response log of a single feedback session.
 */
public class FeedbackSessionLogData {
    private final FeedbackSessionData feedbackSessionData;
    private final List<FeedbackSessionLogEntryData> feedbackSessionLogEntries;

    // Remove generic types after migration is done (i.e. can just use FeedbackSession and Student)
    public <S, T> FeedbackSessionLogData(S feedbackSession, List<FeedbackSessionLogEntry> logEntries,
            Map<String, T> studentsMap) {
        if (feedbackSession instanceof FeedbackSessionAttributes) {
            FeedbackSessionAttributes fs = (FeedbackSessionAttributes) feedbackSession;
            FeedbackSessionData fsData = new FeedbackSessionData(fs);
            List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                    .map(log -> {
                        T student = studentsMap.get(log.getStudentEmail());
                        if (student instanceof StudentAttributes) {
                            return new FeedbackSessionLogEntryData(log, (StudentAttributes) student);
                        } else {
                            throw new IllegalArgumentException("Invalid student type");
                        }
                    })
                    .collect(Collectors.toList());
            this.feedbackSessionData = fsData;
            this.feedbackSessionLogEntries = fsLogEntryDatas;
        } else if (feedbackSession instanceof FeedbackSession) {
            FeedbackSession fs = (FeedbackSession) feedbackSession;
            FeedbackSessionData fsData = new FeedbackSessionData(fs);
            List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                    .map(log -> {
                        T student = studentsMap.get(log.getStudentEmail());
                        if (student instanceof Student) {
                            return new FeedbackSessionLogEntryData(log, (Student) student);
                        } else {
                            throw new IllegalArgumentException("Invalid student type");
                        }
                    })
                    .collect(Collectors.toList());
            this.feedbackSessionData = fsData;
            this.feedbackSessionLogEntries = fsLogEntryDatas;
        } else {
            throw new IllegalArgumentException("Invalid feedback session type");
        }
    }

    public FeedbackSessionData getFeedbackSessionData() {
        return feedbackSessionData;
    }

    /**
     * Returns all feedback session log entries.
     */
    public List<FeedbackSessionLogEntryData> getFeedbackSessionLogEntries() {
        return feedbackSessionLogEntries;
    }
}
