package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.AdminActivityLogPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminActivityLogPageUiTest extends BaseUiTestCase {
    
    private static Browser browser;
    private static AdminActivityLogPage logPage;
       
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll(){
        
        testContent();
        testUserTimezone();
        testReference();
        testViewActionsLink();
        testInputValidation();
        // Full page content check is omitted because this is an internal page. 
    }
    
    
    private void testUserTimezone() {
        logPage.clickUserTimezoneAtFirstRow();
        logPage.waitForAjaxLoaderGifToDisappear();
        assertTrue(logPage.isUserTimezoneAtFirstRowClicked());
    }

    private void testReference() {
        ______TS("content: show reference");
        logPage.clickReferenceButton();
        assertTrue(logPage.isFilterReferenceVisible());
        
    }

    public void testContent() {
        
        ______TS("content: typical page");
        
        Url logPageUrl = createUrl(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE);
        logPage = loginAdminToPage(browser, logPageUrl, AdminActivityLogPage.class);
        logPage.verifyIsCorrectPage();
        
        ______TS("content: navigate to other pages to get some logs");
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_HOME_PAGE));
        logPage.waitForPageToLoad();
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE));
        logPage.waitForPageToLoad();
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE));
        logPage.waitForPageToLoad();
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE));
        logPage.waitForPageToLoad();
        assertNotNull(logPage.getFirstActivityLogRow());
        logPage.verifyHtml("/adminActivityLogPage.html");
    }
    
    
    public void testViewActionsLink(){
        
        ______TS("Link: recent actions link");
        
        try {
            String expectedPersonInfo = logPage.getPersonInfoOfFirstEntry();      
            logPage.clickViewActionsButtonOfFirstEntry();
            String actualPersonInfo = logPage.getFilterBoxString();
            assertEqualsIfQueryStringNotEmpty(expectedPersonInfo, actualPersonInfo);            
        } catch (NoSuchElementException exceptionFromEmptyLogs) {
            /*
             * This can happen if this test is run right after the server is started.
             * In this case, no view actions can be done.
             */
        } catch (IndexOutOfBoundsException exceptionFromInvisibleTmtLogs) {
            /*
             * This can happen if all the log entries are from test accounts
             * (i.e emails ending with .tmt) because they are invisible.
             * In this case, no view actions can be done.
             */
        }
    }
    
    public void testInputValidation(){
        
        ______TS("invalid query format");
        
        logPage.fillQueryBoxWithText("this is a invalid query");
        logPage.clickSearchSubmitButton();
        
        assertEquals("Error with the query: Invalid format", logPage.getQueryMessage());
        
        ______TS("valid query format");
        
        logPage.fillQueryBoxWithText("role:instructor");
        logPage.clickSearchSubmitButton();
        
        assertTrue(logPage.getStatus().contains("Total Logs gone through in last search:"));
        
    }
    
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private void assertEqualsIfQueryStringNotEmpty(String expected, String actual) {
        String emptyQuery = "person:";
        if (!expected.equals(emptyQuery)) {
            assertEquals(expected, actual);
        }
    }
    
}
