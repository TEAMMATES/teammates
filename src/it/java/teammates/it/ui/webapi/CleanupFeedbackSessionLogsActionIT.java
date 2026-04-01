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

        // Create log older than 90 days (should be deleted)
        Instant oldTimestamp = Instant.now().minus(100, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        FeedbackSessionLog oldLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                oldTimestamp);
        logic.createFeedbackSessionLog(oldLog);

        // Create log at exactly 90 days boundary (should be preserved)
        Instant boundaryTimestamp = Instant.now().minus(90, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        FeedbackSessionLog boundaryLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                boundaryTimestamp);
        logic.createFeedbackSessionLog(boundaryLog);

        // Create log within 90 days (should be preserved)
        Instant recentTimestamp = Instant.now().minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        FeedbackSessionLog recentLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                recentTimestamp);
        logic.createFeedbackSessionLog(recentLog);

        // Create very recent log (should be preserved)
        Instant veryRecentTimestamp = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        FeedbackSessionLog veryRecentLog = new FeedbackSessionLog(student, feedbackSession,
                typicalBundle.feedbackSessionLogs.get("student1Session1Log1").getFeedbackSessionLogType(),
                veryRecentTimestamp);
        logic.createFeedbackSessionLog(veryRecentLog);

        HibernateUtil.flushSession();
        HibernateUtil.clearSession();

        // Verify 4 logs exist before cleanup
        List<FeedbackSessionLog> logsBefore = logic.getOrderedFeedbackSessionLogs(course.getId(), null, null,
                Instant.EPOCH, Instant.now().plusSeconds(60));
        assertEquals(logsBefore.size(), 4);

        // Execute cleanup
        CleanupFeedbackSessionLogsAction action = getAction();
        getJsonResult(action);

        // Verify only recent logs remain
        List<FeedbackSessionLog> logsAfter = logic.getOrderedFeedbackSessionLogs(course.getId(), null, null,
                Instant.EPOCH, Instant.now().plusSeconds(60));
        assertEquals(logsAfter.size(), 3);

        // Verify the timestamps of remaining logs
        for (FeedbackSessionLog log : logsAfter) {
            assertTrue("Log with timestamp " + log.getTimestamp() + " should not be older than 90 days",
                    log.getTimestamp().isAfter(Instant.now().minus(90, ChronoUnit.DAYS)));
        }

        // Verify the deleted log is gone
        assertFalse("Old log with timestamp " + oldTimestamp + " should be deleted",
                logsAfter.stream().anyMatch(log -> log.getTimestamp().equals(oldTimestamp)));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
