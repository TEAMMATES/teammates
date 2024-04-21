package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * The response log of a single feedback session.
 */
public class FeedbackSessionLogData {
    private final FeedbackSessionData feedbackSessionData;
    private final List<FeedbackSessionLogEntryData> feedbackSessionLogEntries;

    // Remove generic types after migration is done (i.e. can just use FeedbackSession, Student, FeedbackSessionLog)
    public <S, T, U> FeedbackSessionLogData(S feedbackSession, List<U> logEntries,
            Map<String, T> studentsMap) {
        if (feedbackSession instanceof FeedbackSessionAttributes) {
            FeedbackSessionAttributes fs = (FeedbackSessionAttributes) feedbackSession;
            FeedbackSessionData fsData = new FeedbackSessionData(fs);
            List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                    .map(log -> {
                        if (log instanceof FeedbackSessionLogEntry) {
                            FeedbackSessionLogEntry convertedLog = (FeedbackSessionLogEntry) log;
                            T student = studentsMap.get(convertedLog.getStudentEmail());
                            if (student instanceof StudentAttributes) {
                                return new FeedbackSessionLogEntryData(convertedLog, (StudentAttributes) student);
                            } else {
                                throw new IllegalArgumentException("Invalid student type");
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid log type");
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
                        if (log instanceof FeedbackSessionLog) {
                            FeedbackSessionLog convertedLog = (FeedbackSessionLog) log;
                            T student = studentsMap.get(convertedLog.getStudent().getEmail());
                            if (student instanceof Student) {
                                return new FeedbackSessionLogEntryData(convertedLog, (Student) student);
                            } else {
                                throw new IllegalArgumentException("Invalid student type");
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid log type");
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
