package teammates.test.cases.ui.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.AdminEmailLogPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminEmailLogPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AdminEmailLogPage emailLogPage;
       
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll(){
        testContent();
        testFilterReference();
    }
    
    
    private void testFilterReference() {
        emailLogPage.clickReferenceButton();
        assertTrue(emailLogPage.isFilterReferenceVisible());
    }

    public void testContent() {
        
        ______TS("content: typical page");
        
        Url logPageUrl = createUrl(Const.ActionURIs.ADMIN_EMAIL_LOG_PAGE);
        emailLogPage = loginAdminToPage(browser, logPageUrl, AdminEmailLogPage.class);
        emailLogPage.verifyIsCorrectPage();
        emailLogPage.verifyHtml("/adminEmailLogPage.html");
    }
}
