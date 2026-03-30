package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetAccountAction;

/**
 * SUT: {@link GetAccountAction}.
 */
public class GetAccountActionTest extends BaseActionTest<GetAccountAction> {
    String accountId = "00000000-0000-4000-8000-0000000000b2";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_validParams_success() {
        loginAsAdmin();
        Account account = new Account("name", "email");
        account.setId(UUID.fromString(accountId));
        when(mockLogic.getAccountForId(accountId)).thenReturn(account);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, accountId,
        };
        GetAccountAction a = getAction(params);
        AccountData output = (AccountData) getJsonResult(a).getOutput();
        assertEquals(output.getAccountId(), accountId);
    }

    @Test
    void testExecute_accountDoesNotExist_throwsEntityNotFoundException() {
        loginAsAdmin();
        when(mockLogic.getAccountForId(accountId)).thenReturn(null);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, accountId,
        };
        EntityNotFoundException e = verifyEntityNotFound(params);
        assertEquals("Account does not exist.", e.getMessage());
        verify(mockLogic, times(1)).getAccountForId(accountId);
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        String[] params = {};
        verifyHttpParameterFailure(params);

        verifyHttpParameterFailure();
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
