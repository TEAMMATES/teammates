package teammates.logic.core;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.util.TimeHelper;

/**
 * SUT: {@link UsageStatisticsLogic}.
 */
public class UsageStatisticsLogicTest extends BaseLogicTest {

    private final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();

    @Test
    public void testCalculateEntitiesStatisticsForTimeRange() {

        ______TS("typical success case: present day");

        UsageStatisticsAttributes stats = usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(
                TimeHelper.getInstantDaysOffsetBeforeNow(1L),
                TimeHelper.getInstantDaysOffsetFromNow(1L));

        int numCoursesCreatedInDistantPast = 6;
        int numAccountRequestsCreated = 16;

        assertEquals(dataBundle.feedbackResponses.size(), stats.getNumResponses());
        assertEquals(dataBundle.courses.size() - numCoursesCreatedInDistantPast, stats.getNumCourses());
        assertEquals(dataBundle.students.size(), stats.getNumStudents());
        assertEquals(dataBundle.instructors.size(), stats.getNumInstructors());
        assertEquals(dataBundle.accountRequests.size() - numAccountRequestsCreated,
                stats.getNumAccountRequests());

        ______TS("typical success case: distant past");

        stats = usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(
                Instant.parse("2010-12-31T16:00:00Z"), Instant.parse("2013-12-31T16:00:00Z"));

        assertEquals(0, stats.getNumResponses());
        assertEquals(numCoursesCreatedInDistantPast, stats.getNumCourses());
        assertEquals(0, stats.getNumStudents());
        assertEquals(0, stats.getNumInstructors());
        assertEquals(numAccountRequestsCreated, stats.getNumAccountRequests());

    }

}
