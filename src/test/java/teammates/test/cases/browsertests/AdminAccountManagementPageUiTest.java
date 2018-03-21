package teammates.test.cases.browsertests;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AdminAccountDetailsPage;
import teammates.test.pageobjects.AdminAccountManagementPage;
import teammates.test.pageobjects.AdminActivityLogPage;

/**
 * SUT: {@link Const.ActionURIs#ADMIN_ACCOUNT_MANAGEMENT_PAGE}.
 */
public class AdminAccountManagementPageUiTest extends BaseUiTestCase {
    private AdminAccountManagementPage accountsPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminAccountManagementPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {
        testContent();
        //no input validation to check
        testViewAccountDetailsLink();
        testViewRecentActionsLink();
        testDeleteInstructorStatusAction();
        testDeleteInstructorAccountAction();
    }

    private void testContent() {

        ______TS("content: typical page");

        String instructor1GoogleId = "AAMgtUiT.instr1";
        loginToAdminAccountsManagementPage(instructor1GoogleId);
        accountsPage.verifyIsCorrectPage();
        assertTrue(accountsPage.isTableVisible());

        List<String> expectedTableHeaders =
                Arrays.asList("Account Info", "Instructor for", "Institute", "Create At", "Options");
        assertEquals(expectedTableHeaders, accountsPage.getTableHeaders());
    }

    private void testViewAccountDetailsLink() {

        ______TS("link: view account details");

        String instructorId = "AAMgtUiT.instr1";
        AdminAccountDetailsPage detailsPage = accountsPage.clickViewInstructorDetails(instructorId);
        detailsPage.verifyIsCorrectPage(instructorId);
        detailsPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testViewRecentActionsLink() {

        ______TS("link: view recent actions");

        String instructorId = "AAMgtUiT.instr1";
        AdminActivityLogPage logPage = accountsPage.clickViewRecentActions(instructorId);
        logPage.verifyIsCorrectPage();
        logPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testDeleteInstructorStatusAction() {

        ______TS("action: delete instructor status");

        String idOfInstructorToDelete = "AAMgtUiT.instr1";
        accountsPage.clickDeleteInstructorStatus(idOfInstructorToDelete)
            .waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.INSTRUCTOR_STATUS_DELETED);
        assertFalse(BackDoor.getAccount(idOfInstructorToDelete).isInstructor);
    }

    private void testDeleteInstructorAccountAction() {

        ______TS("action: delete account");

        String instructor3GoogleId = "AAMgtUiT.instr3";
        loginToAdminAccountsManagementPage(instructor3GoogleId);

        accountsPage.clickAndCancelDeleteAccountLink(instructor3GoogleId);
        assertNotNull(BackDoor.getAccount(instructor3GoogleId));

        accountsPage.clickAndConfirmDeleteAccountLink(instructor3GoogleId);
        assertNull(BackDoor.getAccount(instructor3GoogleId));

    }

    private void loginToAdminAccountsManagementPage(String instructorIdToShow) {
        AppUrl accountsPageUrl = createUrl(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE
                                           + "?all=true&googleId=" + instructorIdToShow);
        accountsPage = loginAdminToPage(accountsPageUrl, AdminAccountManagementPage.class);
        accountsPage.waitForAdminAccountsManagementPageToFinishLoading();
        accountsPage.verifyIsCorrectPage();
    }

}
