package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountData;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link GetAccountAction}.
 */
public class GetAccountActionTest extends BaseActionTest<GetAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountAttributes instructor1OfCourse1 = typicalBundle.accounts.get("instructor1OfCourse1");

        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("account not exist");

        String[] nonExistParams = {
                Const.ParamsNames.INSTRUCTOR_ID, "non-exist-account",
        };

        GetAccountAction a = getAction(nonExistParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());
        MessageOutput messageOutput = (MessageOutput) r.getOutput();

        assertEquals("Account does not exist.", messageOutput.getMessage());

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        AccountData response = (AccountData) r.getOutput();

        assertEquals(response.getGoogleId(), instructor1OfCourse1.getGoogleId());
        assertEquals(response.getName(), instructor1OfCourse1.getName());
        assertEquals(response.getEmail(), instructor1OfCourse1.getEmail());
        assertEquals(response.getInstitute(), instructor1OfCourse1.getInstitute());
        assertTrue(response.isInstructor());

        ______TS("Failure: invalid account not found");

        String[] invalidParams = {
                Const.ParamsNames.INSTRUCTOR_ID, "invalid-google-id",
        };

        a = getAction(invalidParams);
        r = getJsonResult(a);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());
        assertEquals("Account does not exist.", output.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
