package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetAccountAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.AccountDetailsData;
import teammates.ui.webapi.output.MessageOutput;


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

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
        };

        GetAccountAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        AccountDetailsData response = (AccountDetailsData) r.getOutput();

        assertEquals(response.getAccountInfo().getGoogleId(), instructor1OfCourse1.getGoogleId());
        assertEquals(response.getAccountInfo().getName(), instructor1OfCourse1.getName());
        assertEquals(response.getAccountInfo().getEmail(), instructor1OfCourse1.getEmail());
        assertEquals(response.getAccountInfo().getInstitute(), instructor1OfCourse1.getInstitute());
        assertTrue(response.getAccountInfo().isInstructor);

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
