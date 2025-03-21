package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinLinkData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.ResetAccountRequestAction;

/**
 * SUT: {@link ResetAccountRequestAction}.
 */
public class ResetAccountRequestActionTest extends BaseActionTest<ResetAccountRequestAction> {

    private AccountRequest accountRequest;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_RESET;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Instant.now());
    }

    @Test
    void testExecute_registeredInstructor_success() throws EntityDoesNotExistException, InvalidParametersException {
        UUID id = accountRequest.getId();

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);
        when(mockLogic.resetAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        ResetAccountRequestAction action = getAction(params);
        JoinLinkData output = (JoinLinkData) getJsonResult(action).getOutput();
        assertEquals(accountRequest.getRegistrationUrl(), output.getJoinLink());
        verify(mockLogic, times(1)).resetAccountRequest(id);
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_unregisteredInstructor_throwsInvalidOperationException()
            throws EntityDoesNotExistException, InvalidParametersException {
        accountRequest.setRegisteredAt(null);
        UUID id = accountRequest.getId();

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        InvalidOperationException ex = verifyInvalidOperation(params);
        assertEquals("Unable to reset account request as instructor is still unregistered.", ex.getMessage());
        verify(mockLogic, never()).resetAccountRequest(id);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_nonExistingAccountRequest_throwsEntityNotFoundException()
            throws EntityDoesNotExistException, InvalidParametersException {
        accountRequest = null;
        UUID id = UUID.fromString("11110000-0000-0000-0000-000000000000");

        when(mockLogic.getAccountRequest(id)).thenReturn(accountRequest);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request with id: 11110000-0000-0000-0000-000000000000 does not exist.",
                enfe.getMessage());
        verify(mockLogic, never()).resetAccountRequest(id);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_noParams_throwsInvalidHttpParameterException() {
        String[] params = {};

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingAccountRequestId_throwsInvalidHttpParameterException()
            throws EntityDoesNotExistException, InvalidParametersException {
        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, null,
        };

        verifyHttpParameterFailure(params);
        verify(mockLogic, never()).resetAccountRequest(any(UUID.class));
        verifyNoEmailsSent();
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notAdmin_cannotAccess() {
        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString(),
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
