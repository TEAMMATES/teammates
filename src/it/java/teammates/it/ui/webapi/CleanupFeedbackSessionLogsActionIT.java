package teammates.it.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    protected void testExecute() {
        ______TS("Should delete logs older than 90 days and preserve recent logs");

        Student student = typicalBundle.students.get("student1InCourse1");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = typicalBundle.courses.get("course1");
        Instant referenceNow = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant retentionCutoff = referenceNow.minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD);

        // Create log older than 90 days (should be deleted)
        Instant oldTimestamp = referenceNow.minus(100, ChronoUnit.DAYS);
        FeedbackSessionLog oldLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                oldTimestamp);
        logic.createFeedbackSessionLog(oldLog);

        // Create log just inside the 90-day boundary (should be preserved)
        Instant boundaryTimestamp = retentionCutoff.plusMillis(1);
        FeedbackSessionLog boundaryLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                boundaryTimestamp);
        logic.createFeedbackSessionLog(boundaryLog);

        // Create log within 90 days (should be preserved)
        Instant recentTimestamp = referenceNow.minus(30, ChronoUnit.DAYS);
        FeedbackSessionLog recentLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                recentTimestamp);
        logic.createFeedbackSessionLog(recentLog);

        // Create very recent log (should be preserved)
        Instant veryRecentTimestamp = referenceNow.minus(1, ChronoUnit.DAYS);
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
        getJsonResult(action);

        // Get logs after cleanup
        List<FeedbackSessionLog> logsAfter = logic.getOrderedFeedbackSessionLogs(course.getId(), null, null,
                Instant.EPOCH, referenceNow.plusSeconds(60));

        // Verify the old log was deleted
        assertFalse("Old log with timestamp " + oldTimestamp + " should be deleted after cleanup",
                logsAfter.stream().anyMatch(log -> log.getTimestamp().equals(oldTimestamp)));

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
