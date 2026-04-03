package teammates.it.ui.webapi;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.CleanupFeedbackSessionLogsAction;

/**
 * SUT: {@link CleanupFeedbackSessionLogsAction}.
 */
public class CleanupFeedbackSessionLogsActionIT extends BaseActionIT<CleanupFeedbackSessionLogsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_CLEANUP;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
        protected void testExecute() throws Exception {
        ______TS("Should delete logs older than 90 days and preserve recent logs");

        Student student = typicalBundle.students.get("student1InCourse1");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = typicalBundle.courses.get("course1");
        Instant referenceNow = Instant.now();
        Instant retentionCutoff = referenceNow.minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD);

        // Create logs around retention boundary for exact cutoff behavior checks.
        Instant oldTimestamp = retentionCutoff.minusMillis(1);
        Instant atCutoffTimestamp = retentionCutoff;
        Instant boundaryTimestamp = retentionCutoff.plusMillis(1);
        Instant recentTimestamp = referenceNow.minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD).plusSeconds(1);
        Instant veryRecentTimestamp = referenceNow.minusSeconds(60);

        FeedbackSessionLog oldLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                oldTimestamp);
        logic.createFeedbackSessionLog(oldLog);

        FeedbackSessionLog atCutoffLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                atCutoffTimestamp);
        logic.createFeedbackSessionLog(atCutoffLog);

        // Create log just inside the 90-day boundary (should be preserved)
        FeedbackSessionLog boundaryLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                boundaryTimestamp);
        logic.createFeedbackSessionLog(boundaryLog);

        // Create log within retention period (should be preserved)
        FeedbackSessionLog recentLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                recentTimestamp);
        logic.createFeedbackSessionLog(recentLog);

        // Create very recent log (should be preserved)
        FeedbackSessionLog veryRecentLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                veryRecentTimestamp);
        logic.createFeedbackSessionLog(veryRecentLog);

        HibernateUtil.flushSession();
        HibernateUtil.clearSession();

        // Get logs before cleanup to verify our test logs are created
        List<FeedbackSessionLog> logsBefore = logic.getOrderedFeedbackSessionLogs(course.getId(), null, null,
                Instant.EPOCH, referenceNow.plusSeconds(60));
        boolean oldLogExistsBefore = logsBefore.stream().anyMatch(log -> log.getTimestamp().equals(oldTimestamp));
        boolean atCutoffLogExistsBefore = logsBefore.stream()
                .anyMatch(log -> log.getTimestamp().equals(atCutoffTimestamp));
        assertTrue("Old log with timestamp " + oldTimestamp + " should exist before cleanup", oldLogExistsBefore);
        assertTrue("Log with timestamp exactly at cutoff should exist before cleanup", atCutoffLogExistsBefore);

        // Execute cleanup
        CleanupFeedbackSessionLogsAction action = getAction();
        Field clockField = CleanupFeedbackSessionLogsAction.class.getDeclaredField("clock");
        clockField.setAccessible(true);
        clockField.set(action, Clock.fixed(referenceNow, ZoneOffset.UTC));
        getJsonResult(action);

        // Get logs after cleanup
        List<FeedbackSessionLog> logsAfter = logic.getOrderedFeedbackSessionLogs(course.getId(), null, null,
                Instant.EPOCH, referenceNow.plusSeconds(60));

        // Verify the old log was deleted
        assertFalse("Old log with timestamp " + oldTimestamp + " should be deleted after cleanup",
                logsAfter.stream().anyMatch(log -> log.getTimestamp().equals(oldTimestamp)));

        // Verify log at exact cutoff is preserved because deletion is strictly older than cutoff.
        assertTrue("Log with timestamp exactly at cutoff should be preserved",
                logsAfter.stream().anyMatch(log -> log.getTimestamp().equals(atCutoffTimestamp)));

        // Verify log just inside boundary is preserved.
        assertTrue("Log just inside cutoff should be preserved",
                logsAfter.stream().anyMatch(log -> log.getTimestamp().equals(boundaryTimestamp)));

        // Verify all remaining logs are not older than the retention boundary (inclusive).
                for (FeedbackSessionLog log : logsAfter) {
                        assertTrue("All remaining logs should be within 90 days",
                                        !log.getTimestamp().isBefore(retentionCutoff));
                }
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
