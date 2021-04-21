package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.LogServiceException;

/**
 * Holds functions for operations related to logs reading/writing in local dev environment.
 *
 * <p>It is expected that most the implementation will be no-op or returning null/empty list
 * as there is no logs retention locally.
 *
 * <p>Writing a local logs ingestion service is possible, but is an overkill at this point of time.
 */
public class LocalLoggingService implements LogService {

    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private static final List<FeedbackSessionLogEntry> feedbackSessionLogEntriesStorage = new ArrayList<>();

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        // Not supported in dev server
        return new ArrayList<>();
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        // Not supported in dev server
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, email);
        FeedbackSessionAttributes feedbackSession = fsLogic.getFeedbackSession(fsName, courseId);

        FeedbackSessionLogEntry logEntry = new FeedbackSessionLogEntry(student, feedbackSession, fslType, Instant.now().toEpochMilli());
        feedbackSessionLogEntriesStorage.add(logEntry);

        System.out.println("\n\ntesting\n\n" + logEntry);
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime) {
        // Not supported in dev server
        System.out.println("\n\ntesting\n\n" + feedbackSessionLogEntriesStorage);
        return feedbackSessionLogEntriesStorage
                .stream()
                .filter(log -> courseId == null || log.getFeedbackSession().getCourseId().equals(courseId))
                .filter(log -> email == null || log.getStudent().getEmail().equals(email))
                .filter(log -> startTime == null || log.getTimestamp() >= startTime.toEpochMilli())
                .filter(log -> endTime == null || log.getTimestamp() <= endTime.toEpochMilli())
                .collect(Collectors.toList());
    }

}
