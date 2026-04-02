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
    private static final String ACCOUNT_ID = TEST_ACCOUNT_ID_B2.toString();

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
        account.setId(UUID.fromString(ACCOUNT_ID));
        when(mockLogic.getAccountForId(ACCOUNT_ID)).thenReturn(account);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, ACCOUNT_ID,
        };
        GetAccountAction a = getAction(params);
        AccountData output = (AccountData) getJsonResult(a).getOutput();
        assertEquals(output.getAccountId(), ACCOUNT_ID);
    }

    @Test
    void testExecute_accountDoesNotExist_throwsEntityNotFoundException() {
        loginAsAdmin();
        when(mockLogic.getAccountForId(ACCOUNT_ID)).thenReturn(null);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, ACCOUNT_ID,
        };
        EntityNotFoundException e = verifyEntityNotFound(params);
        assertEquals("Account does not exist.", e.getMessage());
        verify(mockLogic, times(1)).getAccountForId(ACCOUNT_ID);
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
