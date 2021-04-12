package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteAccountAction}.
 */
public class DeleteAccountActionTest extends BaseActionTest<DeleteAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountAttributes acc = typicalBundle.accounts.get("instructor1OfCourse1");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case, delete an existing account");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, acc.googleId,
        };

        DeleteAccountAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        MessageOutput msg = (MessageOutput) result.getOutput();

        assertEquals(msg.getMessage(), "Account is successfully deleted.");
        assertEquals(result.getStatusCode(), HttpStatus.SC_OK);

        assertNull(logic.getAccount(acc.googleId));

        ______TS("Typical case, delete non-existing account");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, "non-existing-account",
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);

        // should fail silently.
        assertEquals(msg.getMessage(), "Account is successfully deleted.");
        assertEquals(result.getStatusCode(), HttpStatus.SC_OK);

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
