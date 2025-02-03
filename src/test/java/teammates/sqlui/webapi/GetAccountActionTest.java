package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
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
    String googleId = "test.googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        loginAsAdmin();
    }

    @Test
    void testExecute_success() {
        Account account = new Account(googleId, "name", "email");
        when(mockLogic.getAccountForGoogleId(googleId)).thenReturn(account);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, googleId,
        };
        GetAccountAction a = getAction(params);
        AccountData output = (AccountData) getJsonResult(a).getOutput();
        assertEquals(output.getGoogleId(), googleId);
    }

    @Test
    void testExecute_accountDoesNotExist_failSilently() {
        when(mockLogic.getAccountForGoogleId(googleId)).thenReturn(null);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, googleId,
        };
        EntityNotFoundException e = verifyEntityNotFound(params);
        assertEquals("Account does not exist.", e.getMessage());
    }
}
