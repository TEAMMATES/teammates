package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;
import teammates.ui.request.AccountRequestsGetIntent;

/**
 * SUT: {@link GetAccountRequestsAction}.
 */
public class GetAccountRequestsActionTest extends BaseActionTest<GetAccountRequestsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    protected void testExecute_getAccountRequestsPendingProcessing() {
        ______TS("typical success case");

        AccountRequestAttributes accountRequest1 =
                logic.getAccountRequest("submittedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes accountRequest2 =
                logic.getAccountRequest("submittedInstructor2@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes accountRequest3 =
                logic.getAccountRequest("submittedInstructor3@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes accountRequest4 =
                logic.getAccountRequest("submittedInstructor4@tmt.tmt", "TMT, Singapore");

        String[] params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.PENDING_PROCESSING.toString(),
        };
        GetAccountRequestsAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestsData output = (AccountRequestsData) result.getOutput();
        List<AccountRequestData> actual = output.getAccountRequests();

        assertEquals(4, actual.size());
        // correctness of AccountRequestData has been implicitly verified in GetAccountRequestActionTest
        assertTrue(actual.contains(new AccountRequestData(accountRequest1)));
        assertTrue(actual.contains(new AccountRequestData(accountRequest2)));
        assertTrue(actual.contains(new AccountRequestData(accountRequest3)));
        assertTrue(actual.contains(new AccountRequestData(accountRequest4)));

        ______TS("failure: null parameters");

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure();
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INTENT), ihpe.getMessage());
    }

    @Test
    protected void testExecute_getAccountRequestsSubmittedWithinPeriod() {
        ______TS("typical success case");

        AccountRequestAttributes accountRequest1 =
                logic.getAccountRequest("iwc@yahoo.tmt", "TMT, Singapore");
        AccountRequestAttributes accountRequest2 =
                logic.getAccountRequest("submittedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes accountRequest3 =
                logic.getAccountRequest("submittedInstructor2@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes accountRequest4 =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");

        String startTime = String.valueOf(TimeHelper.parseInstant("2012-03-30T00:00:00Z").toEpochMilli());
        String endTime = String.valueOf(TimeHelper.parseInstant("2012-04-02T00:00:00Z").toEpochMilli());
        String[] params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.WITHIN_PERIOD.toString(),
                Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME, startTime,
                Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME, endTime,
        };
        GetAccountRequestsAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestsData output = (AccountRequestsData) result.getOutput();
        List<AccountRequestData> actual = output.getAccountRequests();

        assertEquals(4, actual.size());
        // correctness of AccountRequestData has been implicitly verified in GetAccountRequestActionTest
        assertTrue(actual.contains(new AccountRequestData(accountRequest1)));
        assertTrue(actual.contains(new AccountRequestData(accountRequest2)));
        assertTrue(actual.contains(new AccountRequestData(accountRequest3)));
        assertTrue(actual.contains(new AccountRequestData(accountRequest4)));

        ______TS("failure: invalid start time and/or end time");

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.WITHIN_PERIOD.toString(),
                Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME, "NaN",
                Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME, "NaN",
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals("Invalid start time", ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.WITHIN_PERIOD.toString(),
                Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME, startTime,
                Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME, "NaN",
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals("Invalid end time", ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.WITHIN_PERIOD.toString(),
                Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME, endTime,
                Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME, startTime,
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals("End time cannot be earlier than start time", ihpe.getMessage());

        ______TS("failure: null parameters");

        params = new String[] {
                Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME, startTime,
                Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME, endTime,
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INTENT), ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.WITHIN_PERIOD.toString(),
                Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME, endTime,
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals("Invalid start time", ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestsGetIntent.WITHIN_PERIOD.toString(),
                Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME, startTime,
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals("Invalid end time", ihpe.getMessage());
    }

    @Override
    @Test
    protected void testExecute() {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
