package teammates.ui.webapi;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteAccountAction}.
 */
@Ignore
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
                Const.ParamsNames.INSTRUCTOR_ID, acc.getGoogleId(),
        };

        DeleteAccountAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        MessageOutput msg = (MessageOutput) result.getOutput();

        assertEquals(msg.getMessage(), "Account is successfully deleted.");

        assertNull(logic.getAccount(acc.getGoogleId()));

        ______TS("Typical case, delete non-existing account");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, "non-existing-account",
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        msg = (MessageOutput) result.getOutput();

        // should fail silently.
        assertEquals(msg.getMessage(), "Account is successfully deleted.");

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
