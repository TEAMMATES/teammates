package teammates.ui.webapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountData;
import teammates.ui.output.AccountsData;

/**
 * SUT: {@link GetAccountsAction}.
 */
@Ignore
public class GetAccountsActionTest extends BaseActionTest<GetAccountsAction> {

    private static final String EMAIL = "valid@gmail.tmt";

    private DataBundle testData;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    public void prepareTestData() {
        testData = getTypicalDataBundle();
        testData.accounts.get("instructor1OfCourse2").setEmail(EMAIL);
        testData.accounts.get("instructor2OfCourse1").setEmail(EMAIL);
        testData.accounts.get("student1InCourse1").setEmail(EMAIL);
        removeAndRestoreDataBundle(testData);
    }

    @Override
    @Test
    protected void testExecute() {
        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("typical success case: no accounts with email");

        String[] params = {
                Const.ParamsNames.USER_EMAIL, "non-exist@gmail.tmt",
        };

        GetAccountsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        AccountsData response = (AccountsData) r.getOutput();
        assertEquals(Collections.emptyList(), response.getAccounts());

        ______TS("typical success case: one account with email");

        AccountAttributes account = testData.accounts.get("instructor1OfCourse1");

        params = new String[] {
                Const.ParamsNames.USER_EMAIL, account.getEmail(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        response = (AccountsData) r.getOutput();
        assertEqualAccounts(Arrays.asList(account), response.getAccounts());

        ______TS("typical success case: multiple accounts with email");
        AccountAttributes firstAccount = testData.accounts.get("instructor1OfCourse2");
        AccountAttributes secondAccount = testData.accounts.get("instructor2OfCourse1");
        AccountAttributes thirdAccount = testData.accounts.get("student1InCourse1");

        params = new String[] {
                Const.ParamsNames.USER_EMAIL, EMAIL,
        };

        a = getAction(params);
        r = getJsonResult(a);

        response = (AccountsData) r.getOutput();
        assertEqualAccounts(Arrays.asList(firstAccount, secondAccount, thirdAccount), response.getAccounts());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    private void assertEqualAccounts(List<AccountAttributes> accounts, List<AccountData> accountDataList) {
        accounts.sort(Comparator.comparing(AccountAttributes::getGoogleId));
        accountDataList.sort(Comparator.comparing(AccountData::getGoogleId));
        for (int i = 0; i < accounts.size(); i++) {
            AccountAttributes accountAttributes = accounts.get(i);
            AccountData accountData = accountDataList.get(i);
            assertEquals(accountAttributes.getGoogleId(), accountData.getGoogleId());
            assertEquals(accountAttributes.getName(), accountData.getName());
            assertEquals(accountAttributes.getEmail(), accountData.getEmail());
        }
    }
}
