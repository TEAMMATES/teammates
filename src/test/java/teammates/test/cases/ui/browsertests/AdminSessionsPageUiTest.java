package teammates.test.cases.ui.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.AdminSessionsPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Covers the home page for admins.
 * SUT: {@link AdminSessionsPage}
 */
public class AdminSessionsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    AdminSessionsPage sessionsPage;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();      
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
    }
    
    @Test
    public void testAll() throws InvalidParametersException, EntityDoesNotExistException, Exception {
        testContent();
    }
    
    private void testContent() {
        
        ______TS("content: typical page");
        
        Url sessionsUrl = createUrl(Const.ActionURIs.ADMIN_SESSIONS_PAGE);
        sessionsPage = loginAdminToPage(browser, sessionsUrl, AdminSessionsPage.class);
        sessionsPage.verifyHtml("/adminSessionsPage.html");
        
        ______TS("content: show filter");
        
        sessionsPage.clickDetailButton();
        sessionsPage.verifyHtmlMainContent("/adminSessionsPageShowFilter.html");
        
        ______TS("content: hide filter");
        
        sessionsPage.clickDetailButton();
        sessionsPage.verifyHtmlMainContent("/adminSessionsPageHideFilter.html");
        
    }
}


