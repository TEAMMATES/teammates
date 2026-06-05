package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;

/**
 * Tests for {@link FeedbackSessionLogsDb}.
 */
public class FeedbackSessionLogsDbTest extends BaseDbTestcase {
    private final FeedbackSessionLogsDb feedbackSessionLogsDb = FeedbackSessionLogsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionLog_feedbackSessionLogExists_returnsFeedbackSessionLog() {
        UUID feedbackSessionLogId = given.feedbackSessionLog("feedback-session-log");
        persistGivenData(given);

        FeedbackSessionLog actual = inTransaction(() -> feedbackSessionLogsDb.getFeedbackSessionLog(feedbackSessionLogId));

        assertNotNull(actual);
        assertEquals(feedbackSessionLogId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackSessionLog_feedbackSessionLogIsNew_feedbackSessionLogIsPersisted() {
        UUID feedbackSessionId = given.feedbackSession("feedback-session");
        UUID studentId = given.student("student");
        persistGivenData(given);
        UUID feedbackSessionLogId = given.uuid("feedback-session-log");

        FeedbackSessionLog actual = inTransaction(() -> {
            FeedbackSession feedbackSession = getEntity(FeedbackSession.class, feedbackSessionId);
            Student student = getEntity(Student.class, studentId);
            FeedbackSessionLog feedbackSessionLog = buildDefaultFeedbackSessionLog(
                    feedbackSession, student, feedbackSessionLogId);
            return feedbackSessionLogsDb.persistFeedbackSessionLog(feedbackSessionLog);
        });

        assertEquals(feedbackSessionLogId, actual.getId());
        verifyPresentInDatabase(FeedbackSessionLog.class, feedbackSessionLogId);
    }

    @Test(groups = GroupNames.DB)
    public void getOrderedFeedbackSessionLogs_logsExist_returnsLogsOrderedByTimestampThenUserEmail() {
        Instant now = Instant.now();
        String courseId = given.course("course");
        given.feedbackSession("feedback-session", fs -> fs.course("course"));
        given.student("student-a", s -> s.course("course").email("a@example.com"));
        given.student("student-b", s -> s.course("course").email("b@example.com"));
        UUID laterFeedbackSessionLogId = given.feedbackSessionLog("later-feedback-session-log",
                log -> log.feedbackSession("feedback-session")
                        .student("student-a")
                        .timestamp(now.plus(1, ChronoUnit.HOURS)));
        UUID earlierFeedbackSessionLogId = given.feedbackSessionLog("earlier-feedback-session-log",
                log -> log.feedbackSession("feedback-session")
                        .student("student-b")
                        .timestamp(now.minus(1, ChronoUnit.HOURS)));
        UUID sameTimestampEarlierEmailFeedbackSessionLogId = given.feedbackSessionLog("same-timestamp-earlier-email-log",
                log -> log.feedbackSession("feedback-session")
                        .student("student-a")
                        .timestamp(now));
        UUID sameTimestampLaterEmailFeedbackSessionLogId = given.feedbackSessionLog("same-timestamp-later-email-log",
                log -> log.feedbackSession("feedback-session")
                        .student("student-b")
                        .timestamp(now));
        persistGivenData(given);

        List<FeedbackSessionLog> actual = inTransaction(() -> feedbackSessionLogsDb.getOrderedFeedbackSessionLogs(
                courseId, null, null, now.minus(2, ChronoUnit.HOURS), now.plus(2, ChronoUnit.HOURS)));

        assertEquals(List.of(
                earlierFeedbackSessionLogId,
                sameTimestampEarlierEmailFeedbackSessionLogId,
                sameTimestampLaterEmailFeedbackSessionLogId,
                laterFeedbackSessionLogId),
                actual.stream().map(FeedbackSessionLog::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getOrderedFeedbackSessionLogs_userFilterSpecified_returnsLogsForUser() {
        Instant now = Instant.now();
        String courseId = given.course("course");
        UUID studentId = given.student("student", s -> s.course("course"));
        given.feedbackSession("feedback-session", fs -> fs.course("course"));
        UUID feedbackSessionLogId = given.feedbackSessionLog("feedback-session-log",
                log -> log.feedbackSession("feedback-session").student("student").timestamp(now));
        given.feedbackSessionLog("another-student-feedback-session-log",
                log -> log.feedbackSession("feedback-session").student("another-student").timestamp(now));
        persistGivenData(given);

        List<FeedbackSessionLog> actual = inTransaction(() -> feedbackSessionLogsDb.getOrderedFeedbackSessionLogs(
                courseId, studentId, null, now.minus(1, ChronoUnit.HOURS), now.plus(1, ChronoUnit.HOURS)));

        assertEquals(List.of(feedbackSessionLogId), actual.stream().map(FeedbackSessionLog::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getOrderedFeedbackSessionLogs_feedbackSessionFilterSpecified_returnsLogsForFeedbackSession() {
        Instant now = Instant.now();
        String courseId = given.course("course");
        UUID feedbackSessionId = given.feedbackSession("feedback-session", fs -> fs.course("course"));
        UUID feedbackSessionLogId = given.feedbackSessionLog("feedback-session-log",
                log -> log.feedbackSession("feedback-session").timestamp(now));
        given.feedbackSessionLog("another-feedback-session-log",
                log -> log.feedbackSession("another-feedback-session").timestamp(now));
        persistGivenData(given);

        List<FeedbackSessionLog> actual = inTransaction(() -> feedbackSessionLogsDb.getOrderedFeedbackSessionLogs(
                courseId, null, feedbackSessionId, now.minus(1, ChronoUnit.HOURS), now.plus(1, ChronoUnit.HOURS)));

        assertEquals(List.of(feedbackSessionLogId), actual.stream().map(FeedbackSessionLog::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void deleteFeedbackSessionLogsOlderThan_logsExist_deletesOlderLogs() {
        Instant now = Instant.now();
        UUID oldFeedbackSessionLogId = given.feedbackSessionLog("old-feedback-session-log",
                log -> log.timestamp(now.minus(2, ChronoUnit.HOURS)));
        UUID recentFeedbackSessionLogId = given.feedbackSessionLog("recent-feedback-session-log",
                log -> log.timestamp(now.plus(1, ChronoUnit.HOURS)));
        persistGivenData(given);

        int actual = inTransaction(() -> feedbackSessionLogsDb.deleteFeedbackSessionLogsOlderThan(now));

        assertEquals(1, actual);
        verifyAbsentInDatabase(FeedbackSessionLog.class, oldFeedbackSessionLogId);
        verifyPresentInDatabase(FeedbackSessionLog.class, recentFeedbackSessionLogId);
    }

    private static FeedbackSessionLog buildDefaultFeedbackSessionLog(
            FeedbackSession feedbackSession, Student student, UUID feedbackSessionLogId) {
        assertNotNull(feedbackSession);
        assertNotNull(student);
        FeedbackSessionLog feedbackSessionLog = new FeedbackSessionLog(
                student, feedbackSession, FeedbackSessionLogType.ACCESS, Instant.now());
        feedbackSessionLog.setId(feedbackSessionLogId);
        return feedbackSessionLog;
    }
}
