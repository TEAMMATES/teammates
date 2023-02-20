package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.UsageStatistics;

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
        UsageStatistics sqlEntitiesStats = sqlLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);

        int numEmailsSent = logsProcessor.getNumberOfLogsForEvent(startTime, endTime, LogEvent.EMAIL_SENT, "");
        int numSubmissions = logsProcessor.getNumberOfLogsForEvent(startTime, endTime, LogEvent.FEEDBACK_SESSION_AUDIT,
                "jsonPayload.accessType=\"submission\"");

        UsageStatistics overallUsageStats = new UsageStatistics(
                startTime, COLLECTION_TIME_PERIOD,
                entitiesStats.getNumResponses() + sqlEntitiesStats.getNumResponses(),
                entitiesStats.getNumCourses() + sqlEntitiesStats.getNumCourses(),
                entitiesStats.getNumStudents() + sqlEntitiesStats.getNumStudents(),
                entitiesStats.getNumInstructors() + sqlEntitiesStats.getNumInstructors(),
                entitiesStats.getNumAccountRequests() + sqlEntitiesStats.getNumAccountRequests(),
                numEmailsSent, numSubmissions);

        try {
            sqlLogic.createUsageStatistics(overallUsageStats);
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }

}
