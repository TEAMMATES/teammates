package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    private void verifyAccounts(List<AccountData> accountDataList, List<Account> accounts) {
        assertEquals(accountDataList.size(), accounts.size());
        for (int i = 0; i < accountDataList.size(); i++) {
            AccountData accountData = accountDataList.get(i);
            Account account = accounts.get(i);
            assertEquals(accountData.getGoogleId(), account.getGoogleId());
            assertEquals(accountData.getEmail(), account.getEmail());
            assertEquals(accountData.getName(), account.getName());
        }
    }

    @Test
    void testExecute_multipleAccountsWithEmail_multipleAccountsFetched() {
        List<Account> accounts = new ArrayList<>();
        Account accountStub = getTypicalAccount();
        Account anotherAccountStub = new Account("anotherGoogleId", "anotherEmail",
                accountStub.getEmail());
        accounts.add(accountStub);
        accounts.add(anotherAccountStub);
        when(mockLogic.getAccountsForEmail(accountStub.getEmail())).thenReturn(accounts);
        String[] params = {
                Const.ParamsNames.USER_EMAIL, accountStub.getEmail(),
        };
        GetAccountsAction action = getAction(params);
        AccountsData actionOutput = (AccountsData) getJsonResult(action).getOutput();
        List<AccountData> accountDataList = actionOutput.getAccounts();
        verifyAccounts(accountDataList, accounts);
    }

    @Test
    void testExecute_oneAccountWithEmail_accountFetched() {
        List<Account> accounts = new ArrayList<>();
        Account accountStub = getTypicalAccount();
        accounts.add(accountStub);
        when(mockLogic.getAccountsForEmail(accountStub.getEmail())).thenReturn(accounts);
        String[] params = {
                Const.ParamsNames.USER_EMAIL, accountStub.getEmail(),
        };
        GetAccountsAction action = getAction(params);
        AccountsData actionOutput = (AccountsData) getJsonResult(action).getOutput();
        List<AccountData> accountDataList = actionOutput.getAccounts();
        verifyAccounts(accountDataList, accounts);
    }

    @Test
    void testExecute_noAccountsWithEmail_noAccountsFetched() {
        when(mockLogic.getAccountsForEmail("email")).thenReturn(Collections.emptyList());
        String[] params = {
                Const.ParamsNames.USER_EMAIL, "email",
        };
        GetAccountsAction action = getAction(params);
        AccountsData actionOutput = (AccountsData) getJsonResult(action).getOutput();
        List<AccountData> accountDataList = actionOutput.getAccounts();
        verifyAccounts(accountDataList, Collections.emptyList());
    }

    @Test
    void textExecute_invalidParams_throwsInvalidParametersException() {
        String[] params = {
                Const.ParamsNames.USER_EMAIL, null,
        };
        verifyHttpParameterFailure(params);

        String[] params2 = {};

        verifyHttpParameterFailure(params2);
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
