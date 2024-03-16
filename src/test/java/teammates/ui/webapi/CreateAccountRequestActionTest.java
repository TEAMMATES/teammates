package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionTest extends BaseActionTest<CreateAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test(enabled = false)
    protected void testExecute() {
        loginAsAdmin();
        String name = "JamesBond";
        String email = "jamesbond89@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        ______TS("Null parameters");

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(buildCreateRequest(null, institute, email));
        assertEquals("name cannot be null", ex.getMessage());

        ex = verifyHttpRequestBodyFailure(buildCreateRequest(name, null, email));
        assertEquals("institute cannot be null", ex.getMessage());

        ex = verifyHttpRequestBodyFailure(buildCreateRequest(name, institute, null));

        assertEquals("email cannot be null", ex.getMessage());

        verifyNoTasksAdded();

        ______TS("Normal case");

        String nameWithSpaces = "   " + name + "   ";
        String emailWithSpaces = "   " + email + "   ";
        String instituteWithSpaces = "   " + institute + "   ";

        AccountCreateRequest req = buildCreateRequest(nameWithSpaces, instituteWithSpaces, emailWithSpaces);
        CreateAccountRequestAction a = getAction(req);
        JsonResult r = getJsonResult(a);

        AccountRequestAttributes accountRequestAttributes = logic.getAccountRequest(email, institute);

        assertEquals(name, accountRequestAttributes.getName());
        assertEquals(email, accountRequestAttributes.getEmail());
        assertEquals(institute, accountRequestAttributes.getInstitute());
        assertNotNull(accountRequestAttributes.getRegistrationKey());

        String registrationKey = accountRequestAttributes.getRegistrationKey();
        AccountRequestData output = (AccountRequestData) r.getOutput();
        assertEquals(registrationKey, output.getRegistrationKey());

        verifyNoEmailsSent();
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("Account request already exists: instructor unregistered");

        a = getAction(req);
        r = getJsonResult(a);
        output = (AccountRequestData) r.getOutput();
        assertEquals(registrationKey, output.getRegistrationKey());

        verifyNoEmailsSent();
        verifyNoTasksAdded(); // Account request not added to search indexing queue

        ______TS("Error: invalid parameter");

        String invalidName = "James%20Bond99";

        req = buildCreateRequest(invalidName, institute, emailWithSpaces);

        ex = verifyHttpRequestBodyFailure(req);
        assertEquals("\"" + invalidName + "\" is not acceptable to TEAMMATES as a/an person name because "
                + "it contains invalid characters. A/An person name must start with an "
                + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).",
                ex.getMessage());

        verifyNoEmailsSent();
        verifyNoTasksAdded();
    }

    @Override
    @Test(enabled = false)
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    private AccountCreateRequest buildCreateRequest(String name, String institution, String email) {
        AccountCreateRequest req = new AccountCreateRequest();

        req.setInstructorName(name);
        req.setInstructorInstitution(institution);
        req.setInstructorEmail(email);

        return req;
    }

}
