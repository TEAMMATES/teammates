package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
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
        AccountRequestAttributes accountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case, delete an existing account request");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };

        DeleteAccountRequestAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        MessageOutput msg = (MessageOutput) result.getOutput();

        assertEquals(msg.getMessage(), "Account request successfully deleted.");

        assertNull(logic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()));

        ______TS("Typical case, delete non-existing account request");

        action = getAction(submissionParams);
        result = getJsonResult(action);
        msg = (MessageOutput) result.getOutput();

        // should fail silently.
        assertEquals(msg.getMessage(), "Account request successfully deleted.");

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
