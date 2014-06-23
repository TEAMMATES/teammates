package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCommentsPage;

public class InstructorCommentsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorCommentsPage commentsPage;
    private static DataBundle testData;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCommentsPageUiTest.json");
        restoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test 
    public void allTests() throws Exception{
        testConent();
        testLinks();
        testActions();
    }

    private void testActions() {
        // TODO Auto-generated method stub
        
    }

    private void testLinks() {
        // TODO Auto-generated method stub
        
    }

    private void testConent() {
        ______TS("content: no course");
        
        Url commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructorWithoutCourses").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/instructorCommentsPageForEmptyCourse.html");
        
        ______TS("content: course with no comment");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructorWithOnlyOneSampleCourse").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/instructorCommentsPageForCourseWithoutComment.html");
        
        ______TS("content: typical course with comments");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructor1OfCourse1").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/instructorCommentsPageForTypicalCourseWithComments.html");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
