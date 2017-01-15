package teammates.test.cases.ui.browsertests;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AdminAccountDetailsPage;
import teammates.test.pageobjects.AdminAccountManagementPage;
import teammates.test.pageobjects.AdminActivityLogPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Covers the 'accounts management' view for admins.
 * SUT: {@link AdminAccountManagementPage}
 */
public class AdminAccountManagementPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AppUrl accountsPageUrl;
    private static AdminAccountManagementPage accountsPage;
    private static DataBundle testData;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/AdminAccountManagementPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        browser = BrowserPool.getBrowser();
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
    
    public void testContent() {
        
        ______TS("content: typical page");
        
        String instructor1GoogleId = "AAMgtUiT.instr1";
        loginToAdminAccountsManagementPage(instructor1GoogleId);
        accountsPage.verifyIsCorrectPage();
        assertTrue(accountsPage.isTableVisible());
        
        List<String> expectedTableHeaders =
                Arrays.asList("Account Info", "Instructor for", "Institute", "Create At", "Options");
        assertEquals(expectedTableHeaders, accountsPage.getTableHeaders());
    }

    public void testViewAccountDetailsLink() {

        ______TS("link: view account details");
        
        String instructorId = "AAMgtUiT.instr1";
        AdminAccountDetailsPage detailsPage = accountsPage.clickViewInstructorDetails(instructorId);
        detailsPage.verifyIsCorrectPage(instructorId);
        detailsPage.closeCurrentWindowAndSwitchToParentWindow();
    }
    
    public void testViewRecentActionsLink() {

        ______TS("link: view recent actions");
        
        String instructorId = "AAMgtUiT.instr1";
        AdminActivityLogPage logPage = accountsPage.clickViewRecentActions(instructorId);
        logPage.verifyIsCorrectPage();
        logPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    public void testDeleteInstructorStatusAction() {
        
        ______TS("action: delete instructor status");
        
        String idOfInstructorToDelete = "AAMgtUiT.instr1";
        accountsPage.clickDeleteInstructorStatus(idOfInstructorToDelete)
            .verifyStatus(Const.StatusMessages.INSTRUCTOR_STATUS_DELETED);
        assertFalse(BackDoor.getAccount(idOfInstructorToDelete).isInstructor);
    }

    public void testDeleteInstructorAccountAction() {
        
        ______TS("action: delete account");
        
        String instructor3GoogleId = "AAMgtUiT.instr3";
        loginToAdminAccountsManagementPage(instructor3GoogleId);
        
        accountsPage.clickAndCancelDeleteAccountLink(instructor3GoogleId);
        assertNotNull(BackDoor.getAccount(instructor3GoogleId));
        
        accountsPage.clickAndConfirmDeleteAccountLink(instructor3GoogleId);
        assertNull(BackDoor.getAccount(instructor3GoogleId));
        
    }
    
    private void loginToAdminAccountsManagementPage(String instructorIdToShow) {
        accountsPageUrl = createUrl(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE
                                    + "?all=true&googleId=" + instructorIdToShow);
        accountsPage = loginAdminToPage(browser, accountsPageUrl, AdminAccountManagementPage.class);
        accountsPage.waitForAdminAccountsManagementPageToFinishLoading();
        accountsPage.verifyIsCorrectPage();
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
}
