package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetAccountRequestAction;

/**
 * SUT: {@link GetAccountRequestAction}.
 */
public class GetAccountRequestActionTest extends BaseActionTest<GetAccountRequestAction> {

    private AccountRequest accountRequest;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
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

        GetAccountRequestAction action = getAction(params);
        AccountRequestData output = (AccountRequestData) getJsonResult(action).getOutput();

        verifyAccountRequest(output, accountRequest);
    }

    @Test
    void testExecute_nonExistingAccountRequest_throwsEntityNotFoundException() {
        UUID id = UUID.fromString("11110000-0000-0000-0000-000000000000");

        when(mockLogic.getAccountRequest(id)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request with id: 11110000-0000-0000-0000-000000000000 does not exist.",
                enfe.getMessage());
    }

    @Test
    void testExecute_noParams_throwsInvalidHttpParameterException() {
        String[] params = {};

        verifyHttpParameterFailure(params);
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

    private void verifyAccountRequest(AccountRequestData output, AccountRequest accountRequest) {
        assertEquals(output.getId(), accountRequest.getId().toString());
        assertEquals(output.getEmail(), accountRequest.getEmail());
        assertEquals(output.getName(), accountRequest.getName());
        assertEquals(output.getInstitute(), accountRequest.getInstitute());
        assertEquals(output.getRegistrationKey(), accountRequest.getRegistrationKey());
        assertEquals(output.getStatus(), accountRequest.getStatus());
        assertEquals(output.getComments(), accountRequest.getComments());
    }
}
