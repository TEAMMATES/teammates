package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import teammates.common.util.TimeHelper;
import teammates.storage.entity.UsageStatistics;

/**
 * Gathers usage-related statistics (e.g. new created entities) in the past defined time period and store in the database.'
 */
public class CalculateUsageStatisticsAction extends AutomatedServiceAction {

    static final int COLLECTION_TIME_PERIOD = 60; // represents one hour

    @Override
    public JsonResult execute() {
        Instant endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
        Instant startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);

        UsageStatistics entitiesStats = logic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);

        int numEmailsSent = 0;
        int numSubmissions = 0;

        UsageStatistics overallUsageStats = new UsageStatistics(
                startTime, COLLECTION_TIME_PERIOD,
                entitiesStats.getNumResponses(),
                entitiesStats.getNumCourses(),
                entitiesStats.getNumStudents(),
                entitiesStats.getNumInstructors(),
                entitiesStats.getNumAccountRequests(),
                numEmailsSent, numSubmissions);

        logic.createUsageStatistics(overallUsageStats);

        return new JsonResult("Successful");
    }

}
