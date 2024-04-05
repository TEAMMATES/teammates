package teammates.it.ui.webapi;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestRejectionRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RejectAccountRequestAction;

/**
 * SUT: {@link RejectAccountRequestAction}.
 */
public class RejectAccountRequestActionIT extends BaseActionIT<RejectAccountRequestAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_REJECTION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    public void testExecute() throws Exception {
        // See individual test methods below
    }

    @Test
    protected void testExecute_withReasonTitleAndBody_shouldRejectWithEmail()
            throws InvalidHttpParameterException, InvalidOperationException, InvalidHttpRequestBodyException {
        AccountRequest accountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        accountRequest.setStatus(AccountRequestStatus.PENDING);
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest("title", "body");
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        RejectAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(result.getStatusCode(), 200);

        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.REJECTED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());

        // TODO: test email number and contents
    }

    @Test
    protected void testExecute_withoutReasonTitleAndBody_shouldRejectWithoutEmail()
            throws InvalidHttpParameterException, InvalidOperationException, InvalidHttpRequestBodyException {
        AccountRequest accountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        accountRequest.setStatus(AccountRequestStatus.PENDING);
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        RejectAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(result.getStatusCode(), 200);
        
        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.REJECTED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_withReasonBodyButNoTitle_shouldThrow() throws InvalidHttpParameterException {
        AccountRequest accountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, "body");
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("If reason body is not null, reason title cannot be null", ihrbe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_withReasonTitleButNoBody_shouldThrow() throws InvalidHttpParameterException {
        AccountRequest accountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest("title", null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("If reason title is not null, reason body cannot be null", ihrbe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_alreadyRejected_shouldNotSendEmail()
            throws InvalidHttpParameterException, InvalidOperationException, InvalidHttpRequestBodyException {
        AccountRequest accountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        accountRequest.setStatus(AccountRequestStatus.REJECTED);
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest("title", "body");
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        RejectAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(result.getStatusCode(), 200);

        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(accountRequest.getStatus(), data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_invalidUuid_shouldThrow() throws InvalidHttpParameterException {
        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, "invalid"};

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(requestBody, params);
        assertEquals("Invalid UUID string: invalid", ihpe.getMessage());
        verifyNoEmailsSent();
    }


    @Test
    protected void testExecute_accountRequestNotFound_shouldThrow() throws InvalidHttpParameterException {
        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, null);
        String uuid = UUID.randomUUID().toString();
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, uuid};

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody, params);
        assertEquals(String.format("Account request with id = %s not found", uuid), enfe.getMessage());
        verifyNoEmailsSent();
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
