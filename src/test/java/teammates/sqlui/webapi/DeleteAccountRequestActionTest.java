package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteAccountRequestAction;
import teammates.ui.webapi.InvalidOperationException;

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

    @Test
    void testExecute_existingAccountRequest_success() {
        AccountRequest accountRequest = new AccountRequest("email", "name", "institute", AccountRequestStatus.PENDING,
                "comments");
        UUID id = accountRequest.getId();

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        DeleteAccountRequestAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Account request successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_registeredInstructor_throwsInvalidOperationException() {
        AccountRequest accountRequest = new AccountRequest("email", "name", "institute",
                AccountRequestStatus.REGISTERED, "comments");
        accountRequest.setRegisteredAt(Instant.now());
        UUID id = accountRequest.getId();

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        InvalidOperationException ex = verifyInvalidOperation(params);
        assertEquals("Account request of a registered instructor cannot be deleted.", ex.getMessage());
    }

    @Test
    void testExecute_nonExistingAccountRequest_failSilently() {
        UUID id = UUID.randomUUID();

        when(mockLogic.getAccountRequest(id)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        DeleteAccountRequestAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Account request successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_missingAccountRequestId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        AccountRequest accountRequest = new AccountRequest("email", "name", "institute", AccountRequestStatus.PENDING,
                "comments");
        UUID id = accountRequest.getId();

        loginAsAdmin();
        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notAdmin_cannotAccess() {
        AccountRequest accountRequest = new AccountRequest("email", "name", "institute", AccountRequestStatus.PENDING,
                "comments");
        UUID id = accountRequest.getId();

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        loginAsUnregistered("unregistered");
        verifyCannotAccess(params);

        loginAsStudent("student");
        verifyCannotAccess(params);

        loginAsInstructor("instructor");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
