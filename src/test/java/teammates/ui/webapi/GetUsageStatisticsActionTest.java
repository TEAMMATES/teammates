package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.UsageStatisticsData;
import teammates.ui.output.UsageStatisticsRangeData;

/**
 * Tests for {@link GetUsageStatisticsAction}.
 */
public class GetUsageStatisticsActionTest extends BaseActionTest<GetUsageStatisticsAction, UsageStatisticsRangeData> {

    private static final String ADMIN_EMAIL = "app_admin@gmail.com";

    @Test(groups = GroupNames.ACTION)
    public void getUsageStatisticsAction_adminUser_returnsStatistics() {
        var adminAccount = given.account("admin", a -> a.email(ADMIN_EMAIL));
        given.course("course");
        persistGivenData(given);

        Instant now = Instant.now();
        long startTime = now.minus(2, ChronoUnit.HOURS).toEpochMilli();
        long endTime = now.plus(1, ChronoUnit.HOURS).toEpochMilli();

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTime))
                .withParam(Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTime))
                .withAdminAuth(adminAccount.id());

        UsageStatisticsRangeData result = execute(request);

        List<UsageStatisticsData> stats = result.getResult();
        assertFalse(stats.isEmpty());
        int totalCourses = stats.stream().mapToInt(UsageStatisticsData::getNumCourses).sum();
        assertEquals(1, totalCourses);
    }

    @Test(groups = GroupNames.ACTION)
    public void getUsageStatisticsAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var regularAccount = given.account("regular");
        persistGivenData(given);

        long endTime = Instant.now().toEpochMilli();
        long startTime = Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli();

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTime))
                .withParam(Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTime))
                .withAccountAuth(regularAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
