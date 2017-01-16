package teammates.test.cases.ui.browsertests;

import java.util.Calendar;

import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.test.pageobjects.AdminActivityLogPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminActivityLogPageUiTest extends BaseUiTestCase {
    
    private static Browser browser;
    private static AdminActivityLogPage logPage;
       
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() throws Exception {
        testContent();
        testUserTimezone();
        testReference();
        testViewActionsLink();
        testInputValidation();
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

    public void testContent() throws Exception {
        
        ______TS("content: typical page");
        
        AppUrl logPageUrl = createUrl(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE);
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
        assertTrue(logPage.isLogsTableVisible());
        assertEquals(2, logPage.getNumberOfTableHeaders());
        
        ______TS("content: ensure default search period is not more than one day");
        Calendar yesterday = TimeHelper.now(Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        
        assertTrue(logPage.getDateOfEarliestLog()
                                        .after(yesterday.getTime()));
        
        ______TS("content: show the earliest log's date in both Admin Time Zone and local Time Zone");
        assertTrue(logPage.getStatus().contains("The earliest log entry checked on"));
        assertTrue(logPage.getStatus().contains("in Admin Time Zone"));
        assertTrue(logPage.getStatus().contains("in Local Time Zone")
                   || logPage.getStatus().contains("Local Time Unavailable"));
    }

    public void testViewActionsLink() {
        
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
            ignorePossibleException();
        } catch (IndexOutOfBoundsException exceptionFromInvisibleTmtLogs) {
            /*
             * This can happen if all the log entries are from test accounts
             * (i.e emails ending with .tmt) because they are invisible.
             * In this case, no view actions can be done.
             */
            ignorePossibleException();
        }
    }
    
    public void testInputValidation() {
        
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
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
    private void assertEqualsIfQueryStringNotEmpty(String expected, String actual) {
        String emptyQuery = "person:";
        if (!expected.equals(emptyQuery)) {
            assertEquals(expected, actual);
        }
    }
    
}
