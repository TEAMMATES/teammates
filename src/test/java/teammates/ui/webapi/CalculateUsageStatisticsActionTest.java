package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

        List<UsageStatisticsAttributes> statsObjects = logic.getUsageStatisticsForTimeRange(
                TimeHelper.getInstantDaysOffsetBeforeNow(1L),
                TimeHelper.getInstantDaysOffsetFromNow(1L));

        // Only check that there is a stats object created.
        // Everything else is not predictable.
        assertEquals(1, statsObjects.size());

        UsageStatisticsAttributes statsObject = statsObjects.get(0);
        assertEquals(CalculateUsageStatisticsAction.COLLECTION_TIME_PERIOD, statsObject.getTimePeriod());

        // Note that there is a slim possibility that this assertion may fail, if the hour has changed
        // between when the stats was gathered and the line where Instant.now is called.
        // However, as the execution happens in milliseconds precision, the risk is too small to justify
        // the additional code needed to handle this case.
        Instant pastHour = TimeHelper.getInstantNearestHourBefore(Instant.now()).minus(1, ChronoUnit.HOURS);
        assertEquals(pastHour, statsObject.getStartTime());

    }

}
