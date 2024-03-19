package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlentity.FeedbackSessionLog;

/**
 * SUT: {@link FeedbackSessionLogsDb}.
 */
public class FeedbackSessionLogsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackSessionLogsDb fslDb = FeedbackSessionLogsDb.inst();

    @Test
    public void test_createFeedbackSessionLog_success() {
        Instant logTimestamp = Instant.parse("2011-01-01T00:00:00Z");
        FeedbackSessionLog newLog = new FeedbackSessionLog("student1@teammates.tmt", "session1",
                FeedbackSessionLogType.ACCESS, logTimestamp);

        fslDb.createFeedbackSessionLog(newLog);

        List<FeedbackSessionLog> actualLogs = fslDb.getFeedbackSessionLogs("student1@teammates.tmt", "session1",
                logTimestamp, logTimestamp.plusSeconds(1));

        assertEquals(actualLogs.size(), 1);
        assertEquals(newLog, actualLogs.get(0));
    }

    @Test
    public void test_getFeedbackSessionLogs_success() {
        Instant startTime = Instant.parse("2011-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2011-01-01T01:00:00Z");

        FeedbackSessionLog student1Session1Log1 = new FeedbackSessionLog("student1@teammates.tmt", "session1",
                FeedbackSessionLogType.ACCESS, startTime);
        FeedbackSessionLog student1Session1Log2 = new FeedbackSessionLog("student1@teammates.tmt", "session1",
                FeedbackSessionLogType.SUBMISSION, startTime);
        FeedbackSessionLog student1Session1Log3 = new FeedbackSessionLog("student1@teammates.tmt", "session1",
                FeedbackSessionLogType.VIEW_RESULT, startTime);

        FeedbackSessionLog student2Session1Log1 = new FeedbackSessionLog("student2@teammates.tmt", "session1",
                FeedbackSessionLogType.ACCESS, startTime);
        FeedbackSessionLog student2Session2Log1 = new FeedbackSessionLog("student2@teammates.tmt", "session2",
                FeedbackSessionLogType.ACCESS, startTime);

        FeedbackSessionLog outOfRangeLog1 = new FeedbackSessionLog("student1@teammates.tmt", "session1",
                FeedbackSessionLogType.ACCESS, startTime.minusSeconds(1));
        FeedbackSessionLog outOfRangeLog2 = new FeedbackSessionLog("student1@teammates.tmt", "session1",
                FeedbackSessionLogType.ACCESS, endTime);

        List<FeedbackSessionLog> newLogs = new ArrayList<>();
        newLogs.add(student1Session1Log1);
        newLogs.add(student1Session1Log2);
        newLogs.add(student1Session1Log3);
        newLogs.add(student2Session1Log1);
        newLogs.add(student2Session2Log1);
        newLogs.add(outOfRangeLog1);
        newLogs.add(outOfRangeLog2);

        for (FeedbackSessionLog log : newLogs) {
            fslDb.createFeedbackSessionLog(log);
        }

        ______TS("Return all logs in time range");
        List<FeedbackSessionLog> expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);
        expectedLogs.add(student2Session1Log1);
        expectedLogs.add(student2Session2Log1);

        List<FeedbackSessionLog> actualLogs = fslDb.getFeedbackSessionLogs(null, null, startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return all logs belonging to a student in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student2Session1Log1);
        expectedLogs.add(student2Session2Log1);

        actualLogs = fslDb.getFeedbackSessionLogs("student2@teammates.tmt", null, startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return all logs belonging to a feedbacksession in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student2Session2Log1);

        actualLogs = fslDb.getFeedbackSessionLogs(null, "session2", startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return all logs belonging to a student and feedbacksession in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student2Session1Log1);

        actualLogs = fslDb.getFeedbackSessionLogs("student2@teammates.tmt", "session1", startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("No logs in time range, return empty list");
        expectedLogs = new ArrayList<>();

        actualLogs = fslDb.getFeedbackSessionLogs(null, null, endTime.plusSeconds(3600), endTime.plusSeconds(7200));

        assertEquals(expectedLogs, actualLogs);
    }
}
