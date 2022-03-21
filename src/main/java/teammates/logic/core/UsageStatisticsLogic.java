package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.storage.api.UsageStatisticsDb;

/**
 * Handles operations related to system usage statistics objects.
 *
 * @see UsageStatisticsAttributes
 * @see UsageStatisticsDb
 */
public final class UsageStatisticsLogic {

    private static final UsageStatisticsLogic instance = new UsageStatisticsLogic();

    private final UsageStatisticsDb usageStatisticsDb = UsageStatisticsDb.inst();

    private UsageStatisticsLogic() {
        // prevent initialization
    }

    public static UsageStatisticsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // TODO add the necessary logic classes later
    }

    /**
     * Gets the list of statistics objects between start time and end time.
     */
    public List<UsageStatisticsAttributes> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime);
    }

}
