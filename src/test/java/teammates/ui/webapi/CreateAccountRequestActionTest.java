package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountRequestCreateRequest;
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
    @Test
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

        AccountRequestCreateRequest req = buildCreateRequest(nameWithSpaces, instituteWithSpaces, emailWithSpaces);
        CreateAccountRequestAction a = getAction(req);
        JsonResult r = getJsonResult(a);

        AccountRequestAttributes accountRequestAttributes = logic.getAccountRequest(email, institute);

        assertEquals(name, accountRequestAttributes.getName());
        assertEquals(email, accountRequestAttributes.getEmail());
        assertEquals(institute, accountRequestAttributes.getInstitute());
        assertNotNull(accountRequestAttributes.getRegistrationKey());

        String joinLink = accountRequestAttributes.getRegistrationUrl();
        JoinLinkData output = (JoinLinkData) r.getOutput();
        assertEquals(joinLink, output.getJoinLink());

        verifyNumberOfEmailsSent(1);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), name),
                emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(joinLink));

        ______TS("Account request already exists: instructor unregistered, email sent again");

        a = getAction(req);
        r = getJsonResult(a);
        output = (JoinLinkData) r.getOutput();
        assertEquals(joinLink, output.getJoinLink());

        verifyNumberOfEmailsSent(1);
        verifyNoTasksAdded(); // Account request not added to search indexing queue

        emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), name),
                emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(joinLink));

        ______TS("Account request already exists: instructor registered, InvalidOperationException thrown");

        accountRequestAttributes = typicalBundle.accountRequests.get("instructor1OfCourse1");

        req = buildCreateRequest(accountRequestAttributes.getName(),
                accountRequestAttributes.getInstitute(), accountRequestAttributes.getEmail());

        InvalidOperationException ioe = verifyInvalidOperation(req);
        assertEquals("Cannot create account request as instructor has already registered.", ioe.getMessage());

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
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    private AccountRequestCreateRequest buildCreateRequest(String name, String institution, String email) {
        AccountRequestCreateRequest req = new AccountRequestCreateRequest();

        req.setInstructorName(name);
        req.setInstructorInstitute(institution);
        req.setInstructorEmail(email);

        return req;
    }

}
