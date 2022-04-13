package teammates.client.scripts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import teammates.client.connector.DatastoreClient;
import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;

/**
 * Generates usage statistics objects, mostly for testing purpose.
 */
public class GenerateUsageStatisticsObjects extends DatastoreClient {

    // Enough number of statistics for one whole month
    private static final int NUM_OF_STATISTICS_OBJECTS = 24 * 31;

    private final Logic logic = Logic.inst();

    public static void main(String[] args) {
        new GenerateUsageStatisticsObjects().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Instant inst = Instant.now();
        Random rand = new Random();

        for (int i = 1; i <= NUM_OF_STATISTICS_OBJECTS; i++) {
            Instant endTime = TimeHelper.getInstantNearestHourBefore(inst);
            Instant startTime = endTime.minus(60, ChronoUnit.MINUTES);

            UsageStatisticsAttributes stats = UsageStatisticsAttributes.builder(startTime, 60)
                    .withNumResponses(rand.nextInt(500))
                    .withNumCourses(rand.nextInt(8))
                    .withNumStudents(rand.nextInt(200))
                    .withNumInstructors(rand.nextInt(15))
                    .withNumAccountRequests(rand.nextInt(5))
                    .withNumEmails(rand.nextInt(400))
                    .withNumSubmissions(rand.nextInt(300))
                    .build();
            try {
                logic.createUsageStatistics(stats);
            } catch (EntityAlreadyExistsException | InvalidParametersException e) {
                e.printStackTrace();
            }

            inst = inst.minus(1, ChronoUnit.HOURS);
        }
    }

}
