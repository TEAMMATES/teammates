package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.ui.webapi.CalculateUsageStatisticsAction;

/**
 * SUT: {@link CalculateUsageStatisticsAction}.
 */
public class CalculateUsageStatisticsActionTest extends BaseActionTest<CalculateUsageStatisticsAction> {
    private static final int NUMBER_OF_RESPONSES = 2;
    private static final int NUMBER_OF_COURSES = 2;
    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_INSTRUCTORS = 2;
    private static final int NUMBER_OF_ACCOUNT_REQUESTS = 2;
    private static final int COLLECTION_TIME_PERIOD = 60;
    Instant endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
    Instant startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);
    UsageStatistics testUsageStatistics;
    UsageStatisticsAttributes testUsageStatisticsAttributes;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_USAGE_STATISTICS_COLLECTION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testAccessControl_admin_canAccess() {
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainers_cannotAccess() {
        logoutUser();
        loginAsMaintainer();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_instructor_cannotAccess() {
        logoutUser();
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        logoutUser();
        loginAsStudent(Const.ParamsNames.STUDENT_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        logoutUser();
        loginAsUnregistered(Const.ParamsNames.USER_ID);
        verifyCannotAccess();
    }

    @BeforeMethod
    void setUp() {
        loginAsAdmin();
        testUsageStatistics = new UsageStatistics(
                startTime,
                COLLECTION_TIME_PERIOD,
                NUMBER_OF_RESPONSES,
                NUMBER_OF_COURSES,
                NUMBER_OF_STUDENTS,
                NUMBER_OF_INSTRUCTORS,
                NUMBER_OF_ACCOUNT_REQUESTS,
                0,
                0);
        testUsageStatisticsAttributes =
                UsageStatisticsAttributes.builder(startTime, COLLECTION_TIME_PERIOD)
                        .withNumResponses(NUMBER_OF_RESPONSES)
                        .withNumCourses(NUMBER_OF_COURSES)
                        .withNumStudents(NUMBER_OF_STUDENTS)
                        .withNumInstructors(NUMBER_OF_INSTRUCTORS)
                        .withNumAccountRequests(NUMBER_OF_ACCOUNT_REQUESTS)
                        .build();
    }

    @Test
    public void testExecute_normalCase_shouldSucceed() {
        when(mockLogic.calculateEntitiesStatisticsForTimeRange(isA(Instant.class), isA(Instant.class)))
                .thenReturn(testUsageStatistics);
        when(mockDatastoreLogic.calculateEntitiesStatisticsForTimeRange(isA(Instant.class), isA(Instant.class)))
                .thenReturn(testUsageStatisticsAttributes);
        when(mockLogic.getUsageStatisticsForTimeRange(isA(Instant.class), isA(Instant.class)))
                .thenReturn(List.of(testUsageStatistics));

        CalculateUsageStatisticsAction action = getAction();
        action.execute();

        List<UsageStatistics> statsObjects = mockLogic.getUsageStatisticsForTimeRange(
                TimeHelper.getInstantDaysOffsetBeforeNow(1L),
                TimeHelper.getInstantDaysOffsetFromNow(1L));

        // Only check that there is a stats object created.
        // Everything else is not predictable.
        assertEquals(1, statsObjects.size());

        UsageStatistics statsObject = statsObjects.get(0);
        assertEquals(COLLECTION_TIME_PERIOD, statsObject.getTimePeriod());

        // Note that there is a slim possibility that this assertion may fail, if the hour has changed
        // between when the stats was gathered and the line where Instant.now is called.
        // However, as the execution happens in milliseconds precision, the risk is too small to justify
        // the additional code needed to handle this case.
        Instant pastHour = TimeHelper.getInstantNearestHourBefore(Instant.now()).minus(1, ChronoUnit.HOURS);
        assertEquals(pastHour, statsObject.getStartTime());

    }
}
