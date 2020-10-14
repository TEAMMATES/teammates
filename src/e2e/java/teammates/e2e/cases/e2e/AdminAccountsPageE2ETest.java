package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminAccountsPage;
import teammates.e2e.util.BackDoor;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_ACCOUNTS_PAGE}.
 */
public class AdminAccountsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminAccountsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        String googleId = "AAMgtE2eT.instr2";

        ______TS("verify loaded data");

        AppUrl accountsPageUrl = createUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, googleId);
        AdminAccountsPage accountsPage = loginAdminToPage(accountsPageUrl, AdminAccountsPage.class);

        AccountAttributes account = getAccount(googleId);
        accountsPage.verifyAccountDetails(account);

    }

}
