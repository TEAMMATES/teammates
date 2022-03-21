package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
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

    private FeedbackResponsesLogic feedbackResponsesLogic;

    private UsageStatisticsLogic() {
        // prevent initialization
    }

    public static UsageStatisticsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    }

    /**
     * Gets the list of statistics objects between start time and end time.
     */
    public List<UsageStatisticsAttributes> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime);
    }

    /**
     * Calculates the statistics of created entities for the given time range.
     */
    public UsageStatisticsAttributes calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
        int numResponses = feedbackResponsesLogic.getNumFeedbackResponsesByTimeRange(startTime, endTime);

        return UsageStatisticsAttributes.builder(startTime, 1) // both startTime and timePeriod do not matter here
                .withNumResponses(numResponses)
                .build();
    }

    /**
     * Creates a statistics object.
     *
     * @return the created statistics object
     * @throws InvalidParametersException if the statistics object is not valid
     * @throws EntityAlreadyExistsException if the statistics object already exists in the database
     */
    public UsageStatisticsAttributes createUsageStatistics(UsageStatisticsAttributes attributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        return usageStatisticsDb.createEntity(attributes);
    }

}
