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
        AccountRequestAttributes registeredAccountRequest = typicalBundle.accountRequests.get("instructor1OfCourse1");
        AccountRequestAttributes unregisteredAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Deleting an account request of a registered instructor should fail");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, registeredAccountRequest.getInstitute(),
        };

        InvalidOperationException ex = verifyInvalidOperation(submissionParams);
        assertEquals("Account request of a registered instructor cannot be deleted.", ex.getMessage());
        assertNotNull(logic.getAccountRequest(registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute()));

        ______TS("Typical case, delete an existing account request");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, unregisteredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, unregisteredAccountRequest.getInstitute(),
        };

        DeleteAccountRequestAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        MessageOutput msg = (MessageOutput) result.getOutput();

        assertEquals("Account request successfully deleted.", msg.getMessage());
        assertNull(logic.getAccountRequest(unregisteredAccountRequest.getEmail(),
                unregisteredAccountRequest.getInstitute()));

        ______TS("Typical case, delete non-existing account request");

        action = getAction(submissionParams);
        result = getJsonResult(action);
        msg = (MessageOutput) result.getOutput();

        // should fail silently.
        assertEquals("Account request successfully deleted.", msg.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
