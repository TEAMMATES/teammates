package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackSessionLogEntriesDb;
import teammates.storage.sqlentity.FeedbackSessionLogEntry;

/**
 * SUT: {@link FeedbackSessionLogEntriesDb}.
 */
public class FeedbackSessionLogEntriesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackSessionLogEntriesDb feedbackSessionLogEntriesDb = FeedbackSessionLogEntriesDb.inst();

    @Test
    public void testCreateFeedbackSessionLogEntry() throws Exception {
        ______TS("Create feedbackSessionLogEntry, does not exists, succeeds");

        FeedbackSessionLogEntry feedbackSessionLogEntry = new FeedbackSessionLogEntry(
                "student1InCourse1@gmail.tmt", "idOfTypicalCourse1", "First feedback session",
                FeedbackSessionLogType.ACCESS.getLabel(), Instant.now().toEpochMilli());

        feedbackSessionLogEntriesDb.createFeedbackSessionLogs(List.of(feedbackSessionLogEntry));

        Instant currentTime = Instant.now();
        List<FeedbackSessionLogEntry> logs = feedbackSessionLogEntriesDb.getFeedbackSessionLogs(
                "idOfTypicalCourse1", null,
                currentTime.minus(2, ChronoUnit.SECONDS).toEpochMilli(),
                currentTime.toEpochMilli(), null);

        verifyEquals(feedbackSessionLogEntry, logs.get(0));
    }

}
