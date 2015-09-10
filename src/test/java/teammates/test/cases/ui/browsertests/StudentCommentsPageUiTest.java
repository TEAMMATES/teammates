package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentCommentsPage;

public class StudentCommentsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static StudentCommentsPage commentsPage;
    private static DataBundle testData;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCommentsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser(true);
    }
    
    @Test 
    public void allTests() throws Exception {
        testContent();
    }

    private void testContent() {
        
        ______TS("content: typical case");
        
        Url commentsPageUrl = createUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("student1InCourse1").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, StudentCommentsPage.class);

        // This is the full HTML verification for Student Comments Page, the rest can all be verifyMainHtml
        commentsPage.verifyHtml("/studentCommentsPageForStudent1.html");
        
        commentsPageUrl = createUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("student2InCourse1").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, StudentCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/studentCommentsPageForStudent2.html");
        
        commentsPageUrl = createUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("student3InCourse1").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, StudentCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/studentCommentsPageForStudent3.html");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
