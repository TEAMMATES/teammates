package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.Test;

import teammates.storage.entity.UsageStatistics;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.TestGroups;

/**
 * SUT: {@link UsageStatisticsDb}.
 */
public class UsageStatisticsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final UsageStatisticsDb usageStatisticsDb = UsageStatisticsDb.inst();

    @Test(groups = TestGroups.INTEGRATION)
    public void testGetUsageStatisticsForTimeRange() {
        ______TS("returns empty array for no usageStatistics in time range");
        Instant startTime = Instant.parse("2010-01-01T00:00:00Z");
        List<UsageStatistics> actualUsageStatistics = inTransaction(() -> usageStatisticsDb
                .getUsageStatisticsForTimeRange(startTime, startTime.plus(1, ChronoUnit.DAYS)));

        assertEquals(actualUsageStatistics.size(), 0);

        ______TS("returns correct number of usageStatistics in time range");
        Instant startTimeOne = Instant.parse("2012-01-01T00:00:00Z");
        UsageStatistics usageStatisticsOne = new UsageStatistics(
                startTimeOne, 1, 0, 0, 0, 0, 0, 0, 0);

        Instant startTimeTwo = Instant.parse("2012-01-02T00:00:00Z");
        UsageStatistics usageStatisticsTwo = new UsageStatistics(
                startTimeTwo, 1, 0, 0, 0, 0, 0, 0, 0);

        inTransaction(() -> {
            usageStatisticsDb.createUsageStatistics(usageStatisticsOne);
            usageStatisticsDb.createUsageStatistics(usageStatisticsTwo);
        });

        List<UsageStatistics> actulUsageStatisticsOne = inTransaction(() -> usageStatisticsDb
                .getUsageStatisticsForTimeRange(startTimeOne, startTimeOne.plus(1, ChronoUnit.DAYS)));
        assertEquals(actulUsageStatisticsOne.size(), 1);

        List<UsageStatistics> actulUsageStatisticsTwo = inTransaction(() -> usageStatisticsDb
                .getUsageStatisticsForTimeRange(startTimeOne, startTimeOne.plus(2, ChronoUnit.DAYS)));
        assertEquals(actulUsageStatisticsTwo.size(), 2);
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testCreateUsageStatistics() {
        ______TS("success: create new usage statistics");
        Instant startTime = Instant.parse("2011-01-01T00:00:00Z");
        UsageStatistics newUsageStatistics = new UsageStatistics(
                startTime, 1, 0, 0, 0, 0, 0, 0, 0);

        inTransaction(() -> usageStatisticsDb.createUsageStatistics(newUsageStatistics));

        List<UsageStatistics> actualUsageStatistics = inTransaction(() -> usageStatisticsDb
                .getUsageStatisticsForTimeRange(startTime, startTime.plus(1, ChronoUnit.SECONDS)));

        assertNotEquals(actualUsageStatistics.size(), 0);
        assertEquals(newUsageStatistics, actualUsageStatistics.get(0));
    }
}
