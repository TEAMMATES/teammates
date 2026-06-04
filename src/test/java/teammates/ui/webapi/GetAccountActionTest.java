package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.AccountData;

/**
 * SUT: {@link GetAccountAction}.
 */
public class GetAccountActionTest extends BaseActionTest<GetAccountAction> {
    String googleId = "test.googleId";
    UUID accountId = UUID.fromString("00000000-0000-4000-8000-000000000001");

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
        Account account = new Account(googleId, Provider.TEAMMATES_DEV, "testSubject",
                "validTenantId", "name", "email");
        when(mockLogic.getAccount(accountId)).thenReturn(account);
        String[] params = {
                Const.ParamsNames.ACCOUNT_ID, accountId.toString(),
        };
        GetAccountAction a = getAction(params);
        AccountData output = (AccountData) getJsonResult(a).getOutput();
        assertEquals(output.getAccountId(), accountId);
    }

    @Test
    void testExecute_accountDoesNotExist_throwsEntityNotFoundException() {
        loginAsAdmin();
        when(mockLogic.getAccount(accountId)).thenReturn(null);
        String[] params = {
                Const.ParamsNames.ACCOUNT_ID, accountId.toString(),
        };
        EntityNotFoundException e = verifyEntityNotFound(params);
        assertEquals("Account does not exist.", e.getMessage());
        verify(mockLogic, times(1)).getAccount(accountId);
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
