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
    public void testGetUsageStatisticsForTimeRange() {
        ______TS("returns empty array for no usageStatistics in time range");
        Instant startTime = Instant.parse("2010-01-01T00:00:00Z");
        List<UsageStatistics> actualUsageStatistics = usageStatisticsDb.getUsageStatisticsForTimeRange(
                startTime, startTime.plus(1, ChronoUnit.DAYS));

        assertEquals(actualUsageStatistics.size(), 0);

        ______TS("returns correct number of usageStatistics in time range");
        Instant startTimeOne = Instant.parse("2012-01-01T00:00:00Z");
        UsageStatistics usageStatisticsOne = new UsageStatistics(
                startTimeOne, 1, 0, 0, 0, 0, 0, 0, 0);

        Instant startTimeTwo = Instant.parse("2012-01-02T00:00:00Z");
        UsageStatistics usageStatisticsTwo = new UsageStatistics(
                startTimeTwo, 1, 0, 0, 0, 0, 0, 0, 0);

        usageStatisticsDb.createUsageStatistics(usageStatisticsOne);
        usageStatisticsDb.createUsageStatistics(usageStatisticsTwo);

        List<UsageStatistics> actulUsageStatisticsOne = usageStatisticsDb.getUsageStatisticsForTimeRange(
                startTimeOne, startTimeOne.plus(1, ChronoUnit.DAYS));
        assertEquals(actulUsageStatisticsOne.size(), 1);

        List<UsageStatistics> actulUsageStatisticsTwo = usageStatisticsDb.getUsageStatisticsForTimeRange(
                startTimeOne, startTimeOne.plus(2, ChronoUnit.DAYS));
        assertEquals(actulUsageStatisticsTwo.size(), 2);
    }

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
