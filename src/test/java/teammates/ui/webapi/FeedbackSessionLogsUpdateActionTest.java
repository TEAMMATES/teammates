package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.test.ThreadHelper;

/**
 * SUT: {@link FeedbackSessionLogsUpdateAction}.
 */
public class FeedbackSessionLogsUpdateActionTest
        extends BaseActionTest<FeedbackSessionLogsUpdateAction> {

    private static final int MIN_WINDOW_PERIOD = 2 * 1000;
    private static final int ISOLATION_PERIOD = 5 * 1000;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_UPDATE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        // verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        FeedbackSessionLogsUpdateAction action = getAction();

        ______TS("creating 2 logs in the same window should return only 1 log");

        // create a window exceeding max size
        for (int i = 0; i < 2; i++) {
            mockLogsProcessor.createFeedbackSessionLog("idOfTypicalCourse1",
                    "student1InCourse1@gmail.tmt", "First feedback session", FeedbackSessionLogType.ACCESS.getLabel());
            ThreadHelper.waitFor(MIN_WINDOW_PERIOD/2);
        }

        action.execute();

        Instant currentTime = Instant.now();
        List<FeedbackSessionLogEntryAttributes> allLogEntries = mockLogsProcessor.getFeedbackSessionLogs(
                "idOfTypicalCourse1", "student1InCourse1@gmail.tmt",
                currentTime.minus(MIN_WINDOW_PERIOD/1000 + 1, ChronoUnit.SECONDS).toEpochMilli(),
                currentTime.toEpochMilli(), null);
        List<FeedbackSessionLogEntryAttributes> logs = logic.getFeedbackSessionLogs(
                "idOfTypicalCourse1", null,
                currentTime.minus(MIN_WINDOW_PERIOD/1000 + 1, ChronoUnit.SECONDS).toEpochMilli(),
                currentTime.toEpochMilli(), null);

        assertEquals(1, logs.size());
        assertEquals(2, allLogEntries.size());

        FeedbackSessionLogEntryAttributes firstLog = logs.get(0);
        FeedbackSessionLogEntryAttributes secondLog = allLogEntries.get(1);
        long windowSize = secondLog.getTimestamp() - firstLog.getTimestamp();

        // check that the first log is taken
        // and the window size is smaller than the min value
        assertTrue(windowSize > 0);
        assertTrue(windowSize < MIN_WINDOW_PERIOD);

        assertEquals(firstLog.getTimestamp(), allLogEntries.get(0).getTimestamp());
        assertEquals("student1InCourse1@gmail.tmt", firstLog.getStudentEmail());
        assertEquals("First feedback session", firstLog.getFeedbackSessionName());
        assertEquals(FeedbackSessionLogType.ACCESS.getLabel(), firstLog.getFeedbackSessionLogType());

        // wait 5s to isolate the previous log window
        ThreadHelper.waitFor(ISOLATION_PERIOD);

        ______TS("creating 2 logs of different types in the same window should return both logs");

        mockLogsProcessor.createFeedbackSessionLog("idOfTypicalCourse1",
                "student1InCourse1@gmail.tmt", "First feedback session", FeedbackSessionLogType.ACCESS.getLabel());
        mockLogsProcessor.createFeedbackSessionLog("idOfTypicalCourse1",
                "student1InCourse1@gmail.tmt", "First feedback session", FeedbackSessionLogType.SUBMISSION.getLabel());

        action.execute();

        currentTime = Instant.now();
        logs = logic.getFeedbackSessionLogs("idOfTypicalCourse1", null,
                currentTime.minus(MIN_WINDOW_PERIOD/1000, ChronoUnit.SECONDS).toEpochMilli(),
                currentTime.toEpochMilli(), null);

        assertEquals(2, logs.size());

        FeedbackSessionLogEntryAttributes accessLog = logs.get(0);
        FeedbackSessionLogEntryAttributes submissionLog = logs.get(1);

        assertEquals("student1InCourse1@gmail.tmt", accessLog.getStudentEmail());
        assertEquals("First feedback session", accessLog.getFeedbackSessionName());
        assertEquals(FeedbackSessionLogType.ACCESS.getLabel(), accessLog.getFeedbackSessionLogType());

        assertEquals("student1InCourse1@gmail.tmt", submissionLog.getStudentEmail());
        assertEquals("First feedback session", submissionLog.getFeedbackSessionName());
        assertEquals(FeedbackSessionLogType.SUBMISSION.getLabel(), submissionLog.getFeedbackSessionLogType());

        ThreadHelper.waitFor(ISOLATION_PERIOD);

        ______TS("creating 2 logs in different windows should return both logs");

        mockLogsProcessor.createFeedbackSessionLog("idOfTypicalCourse1",
                "student1InCourse1@gmail.tmt", "First feedback session", FeedbackSessionLogType.ACCESS.getLabel());

        ThreadHelper.waitFor(MIN_WINDOW_PERIOD);

        mockLogsProcessor.createFeedbackSessionLog("idOfTypicalCourse1",
                "student1InCourse1@gmail.tmt", "First feedback session", FeedbackSessionLogType.ACCESS.getLabel());

        action.execute();

        currentTime = Instant.now();
        logs = logic.getFeedbackSessionLogs("idOfTypicalCourse1", null,
                currentTime.minus(MIN_WINDOW_PERIOD/1000 + 1, ChronoUnit.SECONDS).toEpochMilli(),
                currentTime.toEpochMilli(), null);

        assertEquals(2, logs.size());

        FeedbackSessionLogEntryAttributes accessLog1 = logs.get(0);
        FeedbackSessionLogEntryAttributes accessLog2 = logs.get(1);

        assertEquals("student1InCourse1@gmail.tmt", accessLog1.getStudentEmail());
        assertEquals("First feedback session", accessLog1.getFeedbackSessionName());
        assertEquals(FeedbackSessionLogType.ACCESS.getLabel(), accessLog1.getFeedbackSessionLogType());

        assertEquals("student1InCourse1@gmail.tmt", accessLog2.getStudentEmail());
        assertEquals("First feedback session", accessLog2.getFeedbackSessionName());
        assertEquals(FeedbackSessionLogType.ACCESS.getLabel(), accessLog2.getFeedbackSessionLogType());
    }

}
