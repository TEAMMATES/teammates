package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;

import teammates.storage.sqlapi.UsageStatisticsDb;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * Handles operations related to system usage statistics objects.
 *
 * @see UsageStatistics
 * @see teammates.storage.api.UsageStatisticsDb
 */
public final class UsageStatisticsLogic {

    private static final UsageStatisticsLogic instance = new UsageStatisticsLogic();

    private UsageStatisticsDb usageStatisticsDb;

    private UsageStatisticsLogic() {
        // prevent initialization
    }

    public static UsageStatisticsLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsageStatisticsDb usageStatisticsDb) {
        this.usageStatisticsDb = usageStatisticsDb;
    }

    /**
     * Gets the list of statistics objects between start time and end time.
     */
    public List<UsageStatistics> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.isBefore(endTime);

        return usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime);
    }

    /**
     * Calculates the usage statistics of created entities for the given time range.
     */
    public UsageStatistics calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.isBefore(endTime);

        int numResponses = 0; //feedbackResponsesLogic.getNumFeedbackResponsesByTimeRange(startTime, endTime);
        int numCourses = 0; //coursesLogic.getNumCoursesByTimeRange(startTime, endTime);
        int numStudents = 0; //studentsLogic.getNumStudentsByTimeRange(startTime, endTime);
        int numInstructors = 0; //instructorsLogic.getNumInstructorsByTimeRange(startTime, endTime);
        int numAccountRequests = 0; //accountRequestsLogic.getNumAccountRequestsByTimeRange(startTime, endTime);

        return new UsageStatistics(
                startTime, 1, numResponses, numCourses,
                numStudents, numInstructors, numAccountRequests, 0, 0);
    }

    /**
     * Creates a usage statistics object.
     *
     * @return the created usage statistics object
     */
    public UsageStatistics createUsageStatistics(UsageStatistics usageStatistics) {
        return usageStatisticsDb.createUsageStatistics(usageStatistics);
    }

}
