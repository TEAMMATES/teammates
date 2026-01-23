package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

/**
 * SUT: {@link CalculateUsageStatisticsAction}.
 */
public class CalculateUsageStatisticsActionTest extends BaseActionTest<CalculateUsageStatisticsAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_USAGE_STATISTICS_COLLECTION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test(enabled = false)
    public void testExecute() throws Exception {

        CalculateUsageStatisticsAction action = getAction();
        action.execute();

        Instant startTime = TimeHelper.getInstantDaysOffsetBeforeNow(1L);
        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(1L);

        List<UsageStatisticsAttributes> statsObjects = logic.getUsageStatisticsForTimeRange(startTime, endTime);

        // Only check that there is a stats object created.
        // Everything else is not predictable.
        assertEquals(1, statsObjects.size());

        UsageStatisticsAttributes statsObject = statsObjects.get(0);
        assertEquals(CalculateUsageStatisticsAction.COLLECTION_TIME_PERIOD, statsObject.getTimePeriod());

        assertEquals(startTime, statsObject.getStartTime());

    }

}
