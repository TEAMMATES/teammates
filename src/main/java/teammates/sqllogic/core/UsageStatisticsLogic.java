package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;

import teammates.storage.sqlapi.UsageStatisticsDb;
import teammates.storage.sqlentity.UsageStatistics;

public class UsageStatisticsLogic {

    private static final UsageStatisticsLogic instance = new UsageStatisticsLogic();

    private final UsageStatisticsDb usageStatisticsDb = UsageStatisticsDb.inst();


    private UsageStatisticsLogic() {
        // prevent initialization
    }

    public static UsageStatisticsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // TODO
    }

    /**
     * Gets the list of statistics objects between start time and end time.
     */
    public List<UsageStatistics> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime);
    }

//    /**
//     * Calculates the usage statistics of created entities for the given time range.
//     */
//    public UsageStatistics calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
//        int numResponses = feedbackResponsesLogic.getNumFeedbackResponsesByTimeRange(startTime, endTime);
//        int numCourses = coursesLogic.getNumCoursesByTimeRange(startTime, endTime);
//        int numStudents = studentsLogic.getNumStudentsByTimeRange(startTime, endTime);
//        int numInstructors = instructorsLogic.getNumInstructorsByTimeRange(startTime, endTime);
//        int numAccountRequests = accountRequestsLogic.getNumAccountRequestsByTimeRange(startTime, endTime);
//
//        return new UsageStatistics(
//                startTime, 1, numResponses, numCourses,
//                numStudents, numInstructors, numAccountRequests, 0, 0);
//    }

    /**
     * Creates a usage statistics object.
     *
     * @return the created usage statistics object
     */
    public UsageStatistics createUsageStatistics(UsageStatistics usageStatistics) {
        return usageStatisticsDb.createUsageStatistics(usageStatistics);
    }

}
