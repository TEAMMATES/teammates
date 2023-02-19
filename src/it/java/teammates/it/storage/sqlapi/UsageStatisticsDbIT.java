package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.Test;

import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.UsageStatisticsDb;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * SUT: {@link UsageStatisticsDb}.
 */
public class UsageStatisticsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsageStatisticsDb usageStatisticsDb = UsageStatisticsDb.inst();

    @Test
    public void testCreateUsageStatistics() {
        ______TS("success: create new usage statistics");
        Instant startTime = Instant.parse("2011-01-01T00:00:00Z");
        UsageStatistics newUsageStatistics = new UsageStatistics(
                startTime, 1, 0, 0, 0, 0, 0, 0, 0);

        usageStatisticsDb.createUsageStatistics(newUsageStatistics);

        List<UsageStatistics> actualUsageStatistics = usageStatisticsDb.getUsageStatisticsForTimeRange(
                startTime, startTime.plus(1, ChronoUnit.SECONDS));

        assertNotEquals(actualUsageStatistics.size(), 0);
        verifyEquals(newUsageStatistics, actualUsageStatistics.get(0));
    }
}
