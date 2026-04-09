package teammates.sqlui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.GetUsageStatisticsAction;

/**
 * SUT: {@link GetUsageStatisticsAction}.
 */
public class GetUsageStatisticsActionTest extends BaseActionTest<GetUsageStatisticsAction> {
    private static final long START_TIME_FOR_FAIL_CASES = Instant.now().minusSeconds(60).toEpochMilli();
    private static final long END_TIME_FOR_FAIL_CASES = START_TIME_FOR_FAIL_CASES - 1000;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.USAGE_STATISTICS;
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
    void testAccessControl_maintainers_canAccess() {
        logoutUser();
        loginAsMaintainer();
        verifyCanAccess();
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
    public void setUp() {
        loginAsAdmin();
    }

    @Test
    void testExecute_success() {
        GetUsageStatisticsAction action = getAction(
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(START_TIME_FOR_FAIL_CASES),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(START_TIME_FOR_FAIL_CASES + 1000)
        );
        // For now, we stop at simply checking that the request is successful,
        // as we do not have means to reliably create test usage attributes data yet.
        getJsonResult(action);
    }

    @Test
    void testExecute_endTimeBeforeStart_shouldFail() {
        String[] paramsInvalid = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(START_TIME_FOR_FAIL_CASES),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(END_TIME_FOR_FAIL_CASES),
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

    @Test
    void testExecute_invalidSearchStartTime_shouldFail() {
        String[] paramsInvalid = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, "abc",
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(END_TIME_FOR_FAIL_CASES),
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

    @Test
    void testExecute_invalidSearchEndTime_shouldFail() {
        String[] paramsInvalid = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(START_TIME_FOR_FAIL_CASES),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, " ",
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

    @Test
    void testExecute_searchWindowTooLong_shouldFail() {
        long daysExceedingSearchWindow = 200L;
        long startTime = Instant.now().minus(daysExceedingSearchWindow, ChronoUnit.DAYS).toEpochMilli();
        long millisExceedingSearchWindow = Duration.ofDays(daysExceedingSearchWindow).toMillis();

        String[] paramsInvalid = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(startTime + millisExceedingSearchWindow),
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

    @Test
    void testExecute_endTimeAfterCurrentTime_shouldFail() {
        long millisExceedingNow = Instant.now().plusMillis(1000).toEpochMilli();
        String[] paramsInvalid = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(START_TIME_FOR_FAIL_CASES),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(millisExceedingNow),
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

}
