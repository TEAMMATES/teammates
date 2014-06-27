package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertTrue;

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
        testScripts();
        testActions();
    }

    private void testActions() {
        ______TS("action: edit student comment");
        commentsPage.clickStudentCommentRow(1);
        commentsPage.clickStudentCommentEditForRow(1);
        commentsPage.fillTextareaToEditStudentCommentForRow(1, "");
        commentsPage.saveEditStudentCommentForRow(1);
        commentsPage.verifyStatus("Please enter a valid comment. The comment can't be empty.");
        commentsPage.fillTextareaToEditStudentCommentForRow(1, "edited student comment");
        commentsPage.saveEditStudentCommentForRow(1);
        commentsPage.verifyContains("edited student comment");
        commentsPage.verifyStatus(Const.StatusMessages.COMMENT_EDITED);
        
        ______TS("action: delete student comment");
        commentsPage.clickStudentCommentRow(1);
        commentsPage.clickAndCancel(commentsPage.getStudentCommentDeleteForRow(1));
        commentsPage.clickAndConfirm(commentsPage.getStudentCommentDeleteForRow(1));
        commentsPage.verifyStatus(Const.StatusMessages.COMMENT_DELETED);
        
        ______TS("action: add feedback response comment");
        commentsPage.clickResponseCommentAdd(1, 1, 1);
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, "");
        commentsPage.addResponseComment(1, 1, 1);
        commentsPage.verifyCommentFormErrorMessage("1-1-1", "Comment cannot be empty");
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, "added response comment");
        commentsPage.addResponseComment(1, 1, 1);
        commentsPage.reloadPage();
        commentsPage.verifyContains("added response comment");
        
        ______TS("action: edit feedback response comment");
        commentsPage.clickResponseCommentRow(1, 1, 1, 1);
        commentsPage.clickResponseCommentEdit(1, 1, 1, 1);
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, 1, "");
        commentsPage.saveResponseComment(1, 1, 1, 1);
        commentsPage.verifyCommentFormErrorMessage("1-1-1-1", "Comment cannot be empty");
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, 1, "edited response comment");
        commentsPage.saveResponseComment(1, 1, 1, 1);
        commentsPage.reloadPage();
        commentsPage.verifyContains("edited response comment");
        
        ______TS("action: delete feedback response comment");
        commentsPage.clickResponseCommentRow(1, 1, 1, 1);
        commentsPage.clickResponseCommentDelete(1, 1, 1, 1);
        commentsPage.reloadPage();
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageAfterTestScript.html");
    }

    private void testScripts() {
        ______TS("script: include archived course");
        
        commentsPage.clickIsIncludeArchivedCoursesCheckbox();
        commentsPage.verifyContains("comments.idOfArchivedCourse");
        
        commentsPage.clickNextCourseLink();
        assertTrue(browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE + 
                "?user=comments.idOfInstructor1OfCourse1&courseid=comments.idOfArchivedCourse"));
        
        commentsPage.clickPreviousCourseLink();
        assertTrue(browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE + 
                "?user=comments.idOfInstructor1OfCourse1&courseid=comments.idOfTypicalCourse1"));
        
        commentsPage.clickIsIncludeArchivedCoursesCheckbox();
        
        ______TS("script: filter comments");
        
        commentsPage.clickShowMoreOptions();
        
        commentsPage.showCommentsForAll();
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForAll.html");
        
        int studentCommentPanelIdx = 1;
        commentsPage.showCommentsForPanel(studentCommentPanelIdx);
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForStudentCommentPanel.html");
        
        int sessionCommentPanelIdx = 2;
        commentsPage.showCommentsForPanel(sessionCommentPanelIdx);
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForSessionCommentPanel.html");
        
        commentsPage.showCommentsFromAll();
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromAll.html");
        
        commentsPage.showCommentsFromGiver("you");
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromYou.html");
        
        commentsPage.showCommentsFromGiver("others");
        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromOthers.html");
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

        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageForCourseWithoutComment.html");
        
        ______TS("content: typical course with comments");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructor1OfCourse1").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);

        //commentsPage.verifyHtmlMainContent("/instructorCommentsPageForTypicalCourseWithComments.html");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
