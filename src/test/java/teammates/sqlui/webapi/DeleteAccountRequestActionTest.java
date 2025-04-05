package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteAccountRequestAction;
import teammates.ui.webapi.InvalidOperationException;

/**
 * SUT: {@link DeleteAccountRequestAction}.
 */
public class DeleteAccountRequestActionTest extends BaseActionTest<DeleteAccountRequestAction> {

    private AccountRequest accountRequest;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        accountRequest = getTypicalAccountRequest();
    }

    @Test
    void testExecute_existingAccountRequest_success() {
        UUID id = accountRequest.getId();

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        DeleteAccountRequestAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Account request successfully deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1)).deleteAccountRequest(id);
    }

    @Test
    void testExecute_registeredInstructor_throwsInvalidOperationException() {
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
        accountRequest = null;
        UUID id = UUID.fromString("11110000-0000-0000-0000-000000000000");

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

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
    void testAccessControl() {
        UUID id = accountRequest.getId();
        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };
        verifyOnlyAdminsCanAccess(params);
    }
}
