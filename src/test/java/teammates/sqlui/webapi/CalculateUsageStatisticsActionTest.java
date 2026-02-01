package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.ui.webapi.CalculateUsageStatisticsAction;

/**
 * SUT: {@link CalculateUsageStatisticsAction}.
 */
public class CalculateUsageStatisticsActionTest extends BaseActionTest<CalculateUsageStatisticsAction> {
    private final Instant startTime =
            TimeHelper.getInstantNearestHourBefore(Instant.now()).minus(1, ChronoUnit.HOURS);
    private final UsageStatistics testUsageStatistics =
            getTypicalUsageStatistics(startTime);
    private final int collectionTimePeriod = testUsageStatistics.getTimePeriod();

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_USAGE_STATISTICS_COLLECTION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
        verifyMaintainersCannotAccess();
    }

    @BeforeMethod
    void setUp() {
        loginAsAdmin();
    }

    @Test
    public void testExecute_normalCase_shouldSucceed() {
        when(mockLogic.calculateEntitiesStatisticsForTimeRange(isA(Instant.class), isA(Instant.class)))
                .thenReturn(testUsageStatistics);
        when(mockLogic.getUsageStatisticsForTimeRange(isA(Instant.class), isA(Instant.class)))
                .thenReturn(List.of(testUsageStatistics));

        CalculateUsageStatisticsAction action = getAction();
        action.execute();

        List<UsageStatistics> statsObjects = mockLogic.getUsageStatisticsForTimeRange(
                TimeHelper.getInstantDaysOffsetBeforeNow(1L),
                TimeHelper.getInstantDaysOffsetFromNow(1L));

        // Only check that there is a stats object created.
        // Everything else is not predictable.
        assertEquals(1, statsObjects.size());

        UsageStatistics statsObject = statsObjects.get(0);
        assertEquals(collectionTimePeriod, statsObject.getTimePeriod());

        assertEquals(startTime, statsObject.getStartTime());

    }
}
