package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteAccountRequestAction}.
 */
public class DeleteAccountRequestActionTest extends BaseActionTest<DeleteAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountRequestAttributes rejectedAccountRequest =
                logic.getAccountRequest("rejectedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes registeredAccountRequest =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1");

        ______TS("typical success case");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, rejectedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, rejectedAccountRequest.getInstitute(),
        };
        DeleteAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertNull(logic.getAccountRequest(rejectedAccountRequest.getEmail(), rejectedAccountRequest.getInstitute()));

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals("Account request successfully deleted.", output.getMessage());

        ______TS("silent failure: delete non-existent (same) account request");

        action = getAction(params);
        result = getJsonResult(action);

        output = (MessageOutput) result.getOutput();
        assertEquals("Account request successfully deleted.", output.getMessage());

        ______TS("failure: delete account request with status REGISTERED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, registeredAccountRequest.getInstitute(),
        };

        assertNotNull(logic.getAccountRequest(
                registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute()));

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Account request of a registered instructor cannot be deleted.", ioe.getMessage());

        ______TS("failure: null parameters");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, rejectedAccountRequest.getInstitute(),
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_EMAIL),
                ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, rejectedAccountRequest.getEmail(),
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_INSTITUTION),
                ihpe.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
