package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountData;
import teammates.ui.output.AccountsData;
import teammates.ui.webapi.GetAccountsAction;

/**
 * SUT: {@link GetAccountsAction}.
 */
public class GetAccountsActionTest extends BaseActionTest<GetAccountsAction> {
    List<Account> accounts = new ArrayList<>();
    Account typicalAccount = getTypicalAccount();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    protected void prepareTestData() {
        Account accountStub = typicalAccount;
        Account anotherAccountStub = new Account(accountStub.getGoogleId(), accountStub.getEmail(), accountStub.getName());
        accounts.add(accountStub);
        accounts.add(anotherAccountStub);
    }

    @Test
    void testExecute_validParameters_success() {
        loginAsAdmin();
        when(mockLogic.getAccountsForEmail(typicalAccount.getEmail())).thenReturn(accounts);
        String[] params = {
                Const.ParamsNames.USER_EMAIL, typicalAccount.getEmail(),
        };
        GetAccountsAction action = getAction(params);
        AccountsData actionOutput = (AccountsData) getJsonResult(action).getOutput();
        List<AccountData> accountDataList = actionOutput.getAccounts();
        assertEquals(accountDataList.size(), accounts.size());
        for (int i = 0; i < accountDataList.size(); i++) {
            AccountData accountData = accountDataList.get(i);
            Account account = accounts.get(i);
            assertEquals(accountData.getGoogleId(), account.getGoogleId());
            assertEquals(accountData.getEmail(), account.getEmail());
            assertEquals(accountData.getName(), account.getName());
        }
    }
}
