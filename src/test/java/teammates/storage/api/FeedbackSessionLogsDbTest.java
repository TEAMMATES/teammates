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
        var feedbackSessionLog = given.feedbackSessionLog("feedback-session-log");
        persistGivenData(given);

        FeedbackSessionLog actual = inTransaction(
                () -> feedbackSessionLogsDb.getFeedbackSessionLog(feedbackSessionLog.id()));

        assertNotNull(actual);
        assertEquals(feedbackSessionLog.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackSessionLog_feedbackSessionLogIsNew_feedbackSessionLogIsPersisted() {
        var feedbackSessionRef = given.feedbackSession("feedback-session");
        var studentRef = given.student("student");
        persistGivenData(given);
        var feedbackSessionLogId = given.uuid("feedback-session-log");

        FeedbackSessionLog actual = inTransaction(() -> {
            FeedbackSession feedbackSession = getEntity(FeedbackSession.class, feedbackSessionRef.id());
            Student student = getEntity(Student.class, studentRef.id());
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
        var course = given.course("course");
        var feedbackSession = given.feedbackSession("feedback-session", fs -> fs.course(course.alias()));
        var studentA = given.student("student-a", s -> s.course(course.alias()).email("a@example.com"));
        var studentB = given.student("student-b", s -> s.course(course.alias()).email("b@example.com"));
        var laterFeedbackSessionLog = given.feedbackSessionLog("later-feedback-session-log",
                log -> log.feedbackSession(feedbackSession.alias())
                        .student(studentA.alias())
                        .timestamp(now.plus(1, ChronoUnit.HOURS)));
        var earlierFeedbackSessionLog = given.feedbackSessionLog("earlier-feedback-session-log",
                log -> log.feedbackSession(feedbackSession.alias())
                        .student(studentB.alias())
                        .timestamp(now.minus(1, ChronoUnit.HOURS)));
        var sameTimestampEarlierEmailFeedbackSessionLog =
                given.feedbackSessionLog("same-timestamp-earlier-email-log",
                        log -> log.feedbackSession(feedbackSession.alias())
                        .student(studentA.alias())
                        .timestamp(now));
        var sameTimestampLaterEmailFeedbackSessionLog = given.feedbackSessionLog("same-timestamp-later-email-log",
                log -> log.feedbackSession(feedbackSession.alias())
                        .student(studentB.alias())
                        .timestamp(now));
        persistGivenData(given);

        List<FeedbackSessionLog> actual = inTransaction(() -> feedbackSessionLogsDb.getOrderedFeedbackSessionLogs(
                course.id(), null, null, now.minus(2, ChronoUnit.HOURS), now.plus(2, ChronoUnit.HOURS)));

        assertEquals(List.of(
                earlierFeedbackSessionLog.id(),
                sameTimestampEarlierEmailFeedbackSessionLog.id(),
                sameTimestampLaterEmailFeedbackSessionLog.id(),
                laterFeedbackSessionLog.id()),
                actual.stream().map(FeedbackSessionLog::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getOrderedFeedbackSessionLogs_userFilterSpecified_returnsLogsForUser() {
        Instant now = Instant.now();
        var course = given.course("course");
        var student = given.student("student", s -> s.course(course.alias()));
        var feedbackSession = given.feedbackSession("feedback-session", fs -> fs.course(course.alias()));
        var feedbackSessionLog = given.feedbackSessionLog("feedback-session-log",
                log -> log.feedbackSession(feedbackSession.alias()).student(student.alias()).timestamp(now));
        given.feedbackSessionLog("another-student-feedback-session-log",
                log -> log.feedbackSession(feedbackSession.alias()).student("another-student").timestamp(now));
        persistGivenData(given);

        List<FeedbackSessionLog> actual = inTransaction(() -> feedbackSessionLogsDb.getOrderedFeedbackSessionLogs(
                course.id(), student.id(), null, now.minus(1, ChronoUnit.HOURS), now.plus(1, ChronoUnit.HOURS)));

        assertEquals(List.of(feedbackSessionLog.id()), actual.stream().map(FeedbackSessionLog::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getOrderedFeedbackSessionLogs_feedbackSessionFilterSpecified_returnsLogsForFeedbackSession() {
        Instant now = Instant.now();
        var course = given.course("course");
        var feedbackSession = given.feedbackSession("feedback-session", fs -> fs.course(course.alias()));
        var feedbackSessionLog = given.feedbackSessionLog("feedback-session-log",
                log -> log.feedbackSession(feedbackSession.alias()).timestamp(now));
        given.feedbackSessionLog("another-feedback-session-log",
                log -> log.feedbackSession("another-feedback-session").timestamp(now));
        persistGivenData(given);

        List<FeedbackSessionLog> actual = inTransaction(() -> feedbackSessionLogsDb.getOrderedFeedbackSessionLogs(
                course.id(), null, feedbackSession.id(), now.minus(1, ChronoUnit.HOURS), now.plus(1, ChronoUnit.HOURS)));

        assertEquals(List.of(feedbackSessionLog.id()), actual.stream().map(FeedbackSessionLog::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void deleteFeedbackSessionLogsOlderThan_logsExist_deletesOlderLogs() {
        Instant now = Instant.now();
        var oldFeedbackSessionLog = given.feedbackSessionLog("old-feedback-session-log",
                log -> log.timestamp(now.minus(2, ChronoUnit.HOURS)));
        var recentFeedbackSessionLog = given.feedbackSessionLog("recent-feedback-session-log",
                log -> log.timestamp(now.plus(1, ChronoUnit.HOURS)));
        persistGivenData(given);

        int actual = inTransaction(() -> feedbackSessionLogsDb.deleteFeedbackSessionLogsOlderThan(now));

        assertEquals(1, actual);
        verifyAbsentInDatabase(FeedbackSessionLog.class, oldFeedbackSessionLog.id());
        verifyPresentInDatabase(FeedbackSessionLog.class, recentFeedbackSessionLog.id());
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
