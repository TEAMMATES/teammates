package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorSearchPage;

public class InstructorSearchPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorSearchPage searchPage;
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
        searchPage = getInstructorSearchPage(instructorId);
        searchPage.verifyHtml("/InstructorSearchPageDefault.html");
        
    }
    
    private void testSearch() {
        
        ______TS("search for nothing");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        String searchContent = "comment";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchNone.html");
        
        ______TS("search for student comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchStudentComments.html");
        
        ______TS("search for feedback response comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchFeedbackResponseComments.html");
        
        ______TS("search for all comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchComments.html");
        
        ______TS("search for all comments as helper");
        
        String instructorHelperId = testData.accounts.get("helperOfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorHelperId);
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchCommentsAsHelper.html");
        
        
        searchPage = getInstructorSearchPage(instructorId);
        
        ______TS("search for students");
        
        searchPage.clearSearchBox();
        searchContent = "student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchStudentsForStudent1.html");
        searchPage.clearSearchBox();
        searchContent = "student2";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchStudentsForStudent2.html");
        
    }

    private InstructorSearchPage getInstructorSearchPage(String instructorId) {
        Url commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE)
                .withUserId(instructorId);

        return loginAdminToPage(browser, commentsPageUrl, InstructorSearchPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
