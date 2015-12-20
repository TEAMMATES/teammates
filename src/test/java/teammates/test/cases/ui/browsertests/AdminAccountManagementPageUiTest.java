package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AdminAccountDetailsPage;
import teammates.test.pageobjects.AdminAccountManagementPage;
import teammates.test.pageobjects.AdminActivityLogPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.util.Url;

/**
 * Covers the 'accounts management' view for admins.
 * SUT: {@link AdminAccountManagementPage}
 */
public class AdminAccountManagementPageUiTest extends BaseUiTestCase{
    private static Browser browser;
    private static Url accountsPageUrl;
    private static AdminAccountManagementPage accountsPage;
    private static DataBundle testData;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/AdminAccountManagementPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll(){
        testContent();
        //no input validation to check
        testViewAccountDetailsLink();
        testViewRecentActionsLink();
        testDeleteInstructorStatusAction();
        testDeleteInstructorAccountAction();
    }
    
    public void testContent() {
        
        ______TS("content: typical page");
        
        loginToAdminAccountsManagementPage();
        accountsPage.verifyIsCorrectPage();
        assertTrue(accountsPage.isTableVisible());
        
        List<String> expectedTableHeaders = Arrays.asList("Account Info", "Instructor for", "Institute", "Create At", "Options");
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

    public void testDeleteInstructorStatusAction(){
        
        ______TS("action: delete instructor status");
        
        String idOfInstructorToDelete = "AAMgtUiT.instr1";
        accountsPage.clickDeleteInstructorStatus(idOfInstructorToDelete)
            .verifyStatus(Const.StatusMessages.INSTRUCTOR_STATUS_DELETED);
        assertEquals(false, BackDoor.getAccount(idOfInstructorToDelete).isInstructor);
    }

    public void testDeleteInstructorAccountAction(){
        
        ______TS("action: delete account");
        
        browser.driver.get(accountsPageUrl.toAbsoluteString());;
        
        String googleId = "AAMgtUiT.instr3";
        accountsPage.clickAndCancelDeleteAccountLink(googleId);
        assertNotNull(BackDoor.getAccount(googleId));
        
        accountsPage.clickAndConfirmDeleteAccountLink(googleId);
        assertNull(BackDoor.getAccount(googleId));
        
    }

    private void loginToAdminAccountsManagementPage() {
        accountsPageUrl = createUrl(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE + "?all=true");
        accountsPage = loginAdminToPageForAdminUiTests(browser, accountsPageUrl, AdminAccountManagementPage.class);
        // Extra 60 seconds of wait as it can take a longer time to load in non-dev environments
        accountsPage.waitForAdminAccountsManagementPageToFinishLoading();
        accountsPage.verifyIsCorrectPage();
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
}
