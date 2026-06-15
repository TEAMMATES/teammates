package teammates.logic.core;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.InvalidParametersException;
import teammates.ui.output.UsageStatisticsData;

/**
 * Handles operations related to usage statistics.
 */
public final class UsageStatisticsLogic {

    static final int BUCKET_SIZE_MINUTES = 60;
    static final Duration MAX_SEARCH_WINDOW = Duration.ofDays(184L);

    private static final UsageStatisticsLogic instance = new UsageStatisticsLogic();

    private FeedbackResponsesLogic feedbackResponsesLogic;
    private CoursesLogic coursesLogic;
    private UsersLogic usersLogic;
    private AccountVerificationsLogic accountVerificationsLogic;

    private UsageStatisticsLogic() {
        // prevent initialization
    }

    public static UsageStatisticsLogic inst() {
        return instance;
    }

    /**
     * Initialise dependencies for {@code UsageStatisticsLogic} object.
     */
    public void initLogicDependencies(FeedbackResponsesLogic feedbackResponsesLogic,
            CoursesLogic coursesLogic, UsersLogic usersLogic,
            AccountVerificationsLogic accountVerificationRequestsLogic) {
        this.feedbackResponsesLogic = feedbackResponsesLogic;
        this.coursesLogic = coursesLogic;
        this.usersLogic = usersLogic;
        this.accountVerificationsLogic = accountVerificationRequestsLogic;
    }

    /**
     * Calculates usage statistics for the given time range by counting entities
     * created in hourly buckets.
     *
     * @throws InvalidParametersException if the time range is invalid
     */
    public List<UsageStatisticsData> getUsageStatistics(Instant startTime, Instant endTime)
            throws InvalidParametersException {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidParametersException("The end time should be after the start time.");
        }
        if (endTime.toEpochMilli() - startTime.toEpochMilli() > MAX_SEARCH_WINDOW.toMillis()) {
            throw new InvalidParametersException("The search window must not exceed "
                    + MAX_SEARCH_WINDOW.toDays() + " full days.");
        }
        List<Instant> responseTimes =
                feedbackResponsesLogic.getFeedbackResponseCreatedAtTimestampsForTimeRange(startTime, endTime);
        List<Instant> courseTimes =
                coursesLogic.getCourseCreatedAtTimestampsForTimeRange(startTime, endTime);
        List<Instant> studentTimes =
                usersLogic.getStudentCreatedAtTimestampsForTimeRange(startTime, endTime);
        List<Instant> instructorTimes =
                usersLogic.getInstructorCreatedAtTimestampsForTimeRange(startTime, endTime);
        List<Instant> accountVerificationRequestTimes =
                accountVerificationsLogic
                        .getAccountVerificationRequestCreatedAtTimestampsForTimeRange(startTime, endTime);

        List<UsageStatisticsData> stats = new ArrayList<>();
        Instant bucketStart = startTime;
        while (bucketStart.isBefore(endTime)) {
            Instant bucketEnd = bucketStart.plus(BUCKET_SIZE_MINUTES, ChronoUnit.MINUTES);
            stats.add(new UsageStatisticsData(
                    bucketStart.toEpochMilli(),
                    countInBucket(responseTimes, bucketStart, bucketEnd),
                    countInBucket(courseTimes, bucketStart, bucketEnd),
                    countInBucket(studentTimes, bucketStart, bucketEnd),
                    countInBucket(instructorTimes, bucketStart, bucketEnd),
                    countInBucket(accountVerificationRequestTimes, bucketStart, bucketEnd)));
            bucketStart = bucketEnd;
        }
        return stats;
    }

    private static int countInBucket(List<Instant> timestamps, Instant bucketStart, Instant bucketEnd) {
        return (int) timestamps.stream()
                .filter(t -> !t.isBefore(bucketStart) && t.isBefore(bucketEnd))
                .count();
    }

}
