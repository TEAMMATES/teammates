package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.webapi.CreateAccountRequestAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionIT extends BaseActionIT<CreateAccountRequestAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    @Override
    protected void setUp() {
        // CreateAccountRequestAction handles its own transactions;
        // There is thus no need to setup a transaction.
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        // This is a minimal test; other cases are not tested due to upcoming changes in behaviour.
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("ring-bearer@fellowship.net");
        request.setInstructorName("Frodo Baggins");
        request.setInstructorInstitution("The Fellowship of the Ring");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        JoinLinkData output = (JoinLinkData) result.getOutput();
        HibernateUtil.beginTransaction();
        AccountRequest accountRequest = logic.getAccountRequest("ring-bearer@fellowship.net", "The Fellowship of the Ring");
        HibernateUtil.commitTransaction();
        assertEquals("ring-bearer@fellowship.net", accountRequest.getEmail());
        assertEquals("Frodo Baggins", accountRequest.getName());
        assertEquals("The Fellowship of the Ring", accountRequest.getInstitute());
        assertNull(accountRequest.getRegisteredAt());
        assertEquals(accountRequest.getRegistrationUrl(), output.getJoinLink());
        verifyNumberOfEmailsSent(1);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);
        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), "Frodo Baggins"),
                emailSent.getSubject());
        assertEquals("ring-bearer@fellowship.net", emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(output.getJoinLink()));
    }

    @Override
    protected void testAccessControl() throws Exception {
        // This is not tested due to upcoming changes in behaviour.
    }

}
