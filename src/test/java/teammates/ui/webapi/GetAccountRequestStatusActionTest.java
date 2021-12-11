package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.JoinStatus;

/**
 * SUT: {@link GetAccountRequestStatusAction}.
 */
public class GetAccountRequestStatusActionTest extends BaseActionTest<GetAccountRequestStatusAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_STATUS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {

        loginAsUnregistered("unreg.user");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Normal case: account request not used");

        String registrationKey = logic.getAccountRequest("unregisteredinstructor1@gmail.tmt",
                "TEAMMATES Test Institute 1").getRegistrationKey();

        String[] params = new String[] { Const.ParamsNames.REGKEY, registrationKey, };

        GetAccountRequestStatusAction getAccountRequestStatusAction = getAction(params);
        JsonResult result = getJsonResult(getAccountRequestStatusAction);

        JoinStatus output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Normal case: account request already used");

        String usedRegistrationKey =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1").getRegistrationKey();

        params = new String[] { Const.ParamsNames.REGKEY, usedRegistrationKey, };

        getAccountRequestStatusAction = getAction(params);
        result = getJsonResult(getAccountRequestStatusAction);

        output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Failure case: regkey is not valid");

        params = new String[] { Const.ParamsNames.REGKEY, "invalid-registration-key", };

        verifyEntityNotFound(params);

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
