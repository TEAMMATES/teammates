package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.AdminSearchPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminSearchPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AdminSearchPage searchPage;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        putDocuments(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test 
    public void allTests() throws Exception{    
        testContent();
        testSearch();        
    }
    
    private void testContent() {
        
        ______TS("content: default search page");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        searchPage = getAdminSearchPage(instructorId);
        
        // This is the full HTML verification for Admin Search Page, the rest can all be verifyMainHtml      
        searchPage.verifyHtml("/adminSearchPageDefault.html");
        
    }
    
    private void testSearch() {
        
        ______TS("search for nothing");
        
        String searchContent = "";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/adminSearchPageSearchNone.html");
        
        ______TS("search for student1");
        
        searchPage.clearSearchBox();
        searchContent = "student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/adminSearchPageSearchStudent1.html");
        
    }

    private AdminSearchPage getAdminSearchPage(String instructorId) {
        Url commentsPageUrl = createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE);

        return loginAdminToPage(browser, commentsPageUrl, AdminSearchPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
