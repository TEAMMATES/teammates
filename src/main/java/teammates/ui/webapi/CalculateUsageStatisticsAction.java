package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;

/**
 * Gathers usage-related statistics (e.g. new created entities) in the past defined time period and store in the database.'
 */
public class CalculateUsageStatisticsAction extends AdminOnlyAction {

    static final int COLLECTION_TIME_PERIOD = 60; // represents one hour
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        Instant endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
        Instant startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);

        UsageStatisticsAttributes entitiesStats = logic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);

        UsageStatisticsAttributes overallUsageStats = UsageStatisticsAttributes.builder(startTime, COLLECTION_TIME_PERIOD)
                .withNumResponses(entitiesStats.getNumResponses())
                .build();

        try {
            logic.createUsageStatistics(overallUsageStats);
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            log.severe("Unexpected error", e);
        }
        return new JsonResult("Successful");
    }

}
