package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Student;
import teammates.test.TestGroups;

/**
 * SUT: {@link CleanupFeedbackSessionLogsAction}.
 */
public class CleanupFeedbackSessionLogsActionIT extends BaseActionIT<CleanupFeedbackSessionLogsAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_CLEANUP;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test(groups = TestGroups.INTEGRATION)
    @Override
    protected void testExecute() throws Exception {
        ______TS("Should delete logs older than 90 days and preserve recent logs");

        Student student = typicalBundle.students.get("student1InCourse1");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = typicalBundle.courses.get("course1");
        // Align to whole seconds to avoid precision differences across database and backend
        Instant referenceNow = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant retentionCutoff = referenceNow.minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD);

        // Create logs around retention boundary for exact cutoff behavior checks.
        Instant oldTimestamp = retentionCutoff.minusSeconds(1);
        Instant atCutoffTimestamp = retentionCutoff;
        Instant boundaryTimestamp = retentionCutoff.plusSeconds(1);
        Instant recentTimestamp = referenceNow.minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD).plusSeconds(1);
        Instant veryRecentTimestamp = referenceNow.minusSeconds(60);

        FeedbackSessionLog[] logs = inTransaction(() -> new FeedbackSessionLog[] {
                logic.createFeedbackSessionLog(feedbackSession, student,
                        typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                        oldTimestamp),
                logic.createFeedbackSessionLog(feedbackSession, student,
                        typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                        atCutoffTimestamp),
                logic.createFeedbackSessionLog(feedbackSession, student,
                        typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                        boundaryTimestamp),
                logic.createFeedbackSessionLog(feedbackSession, student,
                        typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                        recentTimestamp),
                logic.createFeedbackSessionLog(feedbackSession, student,
                        typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                        veryRecentTimestamp),
        });
        FeedbackSessionLog oldLog = logs[0];
        FeedbackSessionLog atCutoffLog = logs[1];
        FeedbackSessionLog boundaryLog = logs[2];

        // Get logs before cleanup to verify our test logs are created
        List<FeedbackSessionLog> logsBefore = inTransaction(() -> logic.getOrderedFeedbackSessionLogs(
                course.getId(), null, null, Instant.EPOCH, referenceNow.plusSeconds(60)));
        boolean oldLogExistsBefore = logsBefore.stream().anyMatch(log -> log.getId().equals(oldLog.getId()));
        boolean atCutoffLogExistsBefore = logsBefore.stream().anyMatch(log -> log.getId().equals(atCutoffLog.getId()));
        assertTrue(oldLogExistsBefore, "Old log with timestamp " + oldTimestamp + " should exist before cleanup");
        assertTrue(atCutoffLogExistsBefore, "Log with timestamp exactly at cutoff should exist before cleanup");

        // Execute cleanup
        CleanupFeedbackSessionLogsAction action = getAction();
        Field clockField = CleanupFeedbackSessionLogsAction.class.getDeclaredField("clock");
        clockField.setAccessible(true);
        clockField.set(action, Clock.fixed(referenceNow, ZoneOffset.UTC));
        getJsonResult(action);

        // Get logs after cleanup
        List<FeedbackSessionLog> logsAfter = inTransaction(() -> logic.getOrderedFeedbackSessionLogs(
                course.getId(), null, null, Instant.EPOCH, referenceNow.plusSeconds(60)));

        // Verify the old log was deleted
        assertFalse(logsAfter.stream().anyMatch(log -> log.getId().equals(oldLog.getId())),
                "Old log with timestamp " + oldTimestamp + " should be deleted after cleanup");

        // Verify log at exact cutoff is preserved because deletion is strictly older than cutoff.
        assertTrue(logsAfter.stream().anyMatch(log -> log.getId().equals(atCutoffLog.getId())),
                "Log with timestamp exactly at cutoff should be preserved");

        // Verify log just inside boundary is preserved.
        assertTrue(logsAfter.stream().anyMatch(log -> log.getId().equals(boundaryLog.getId())),
                "Log just inside cutoff should be preserved");

        // Verify all remaining logs are not older than the retention boundary (inclusive).
        for (FeedbackSessionLog log : logsAfter) {
            assertTrue(!log.getTimestamp().isBefore(retentionCutoff),
                    "All remaining logs should be within 90 days");
        }
    }

    @Test(groups = TestGroups.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
