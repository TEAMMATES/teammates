package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.test.GroupNames;
import teammates.ui.output.UsageStatisticsData;

/**
 * Tests for {@link UsageStatisticsLogic}.
 */
public class UsageStatisticsLogicTest extends BaseLogicTestcase {
    private final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();

    @Test(groups = GroupNames.LOGIC)
    public void getUsageStatistics_entitiesCreatedInRange_countedCorrectly() {
        given.course("course-1");
        given.course("course-2");
        given.student("student");
        given.feedbackResponse("feedback-response");
        given.instructor("instructor");
        given.accountVerificationRequest("account-request");
        persistGivenData(given);
        DataBundle dataBundle = given.getDataBundle();

        Instant now = Instant.now();
        Instant start = now.minus(1, ChronoUnit.HOURS);
        Instant end = now.plus(1, ChronoUnit.HOURS);

        List<UsageStatisticsData> result = inTransaction(
                () -> usageStatisticsLogic.getUsageStatistics(start, end));

        assertFalse(result.isEmpty());
        int totalCourses = result.stream().mapToInt(UsageStatisticsData::getNumCourses).sum();
        int totalStudents = result.stream().mapToInt(UsageStatisticsData::getNumStudents).sum();
        int totalResponses = result.stream().mapToInt(UsageStatisticsData::getNumResponses).sum();
        int totalInstructors = result.stream().mapToInt(UsageStatisticsData::getNumInstructors).sum();
        int totalAccountVerificationRequests = result.stream().mapToInt(UsageStatisticsData::getNumAccountVerificationRequests).sum();
        assertEquals(dataBundle.courses.size(), totalCourses);
        assertEquals(dataBundle.students.size(), totalStudents);
        assertEquals(dataBundle.feedbackResponses.size(), totalResponses);
        assertEquals(dataBundle.instructors.size(), totalInstructors);
        assertEquals(dataBundle.accountVerificationRequests.size(), totalAccountVerificationRequests);
    }

    @Test(groups = GroupNames.LOGIC)
    public void getUsageStatistics_noEntitiesInRange_returnsEmptyBuckets() {
        Instant now = Instant.now();
        Instant futureStart = now.plus(10, ChronoUnit.HOURS);
        Instant futureEnd = now.plus(11, ChronoUnit.HOURS);

        List<UsageStatisticsData> result = inTransaction(
                () -> usageStatisticsLogic.getUsageStatistics(futureStart, futureEnd));

        assertEquals(1, result.size());
        UsageStatisticsData bucket = result.get(0);
        assertEquals(0, bucket.getNumCourses());
        assertEquals(0, bucket.getNumStudents());
        assertEquals(0, bucket.getNumResponses());
        assertEquals(0, bucket.getNumInstructors());
        assertEquals(0, bucket.getNumAccountVerificationRequests());
    }

}
