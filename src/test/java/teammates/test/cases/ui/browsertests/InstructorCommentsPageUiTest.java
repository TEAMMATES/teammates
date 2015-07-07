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
import teammates.test.pageobjects.InstructorHomePage;

public class InstructorCommentsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorCommentsPage commentsPage;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCommentsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser(true);
    }
    
    @Test 
    public void allTests() throws Exception{
        testContent();
        testScripts();
        testActions();
        testSearch();
        testEmailPendingComments();
    }

    private void testContent() throws Exception {
        ______TS("content: no course");
        
        Url commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructorWithoutCourses").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);
        
        // This is the full HTML verification for Instructor Comments Page, the rest can all be verifyMainHtml
        commentsPage.verifyHtml("/instructorCommentsPageForEmptyCourse.html");
        
        ______TS("content: course with no comment");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructorWithOnlyOneSampleCourse").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/instructorCommentsPageForCourseWithoutComment.html");
        
        ______TS("content: typical course with comments with helper view");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("helperOfCourse1").googleId);

        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);
        commentsPage.loadResponseComments();
        
        commentsPage.verifyHtmlMainContent("/instructorCommentsForTypicalCourseWithCommentsWithHelperView.html");
        
        ______TS("content: course with comments with session name containing characters to be URI encoded");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructorOfCourseWithUriChars").googleId);
        
        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);
        commentsPage.loadResponseComments();
        
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageForCourseWithURIEncodedSessionName.html");
        
        ______TS("content: typical course with comments");
        
        commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("instructor1OfCourse1").googleId);
        
        commentsPage = loginAdminToPage(browser, commentsPageUrl, InstructorCommentsPage.class);
        commentsPage.loadResponseComments();
        removePreExistComments();
        
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageForTypicalCourseWithComments.html");
    }

    private void removePreExistComments() {
        if(commentsPage.getPageSource().contains("added response comment")
                || commentsPage.getPageSource().contains("edited response comment")){
            commentsPage.clickResponseCommentDelete(1, 1, 1, 1);
            commentsPage.clickCommentsPageLinkInHeader();
        }
    }
    
    private void testScripts() throws Exception {
        ______TS("script: include archived course");
        
        commentsPage.clickIsIncludeArchivedCoursesCheckbox();
        commentsPage.verifyContains("comments.idOfArchivedCourse");
        
        commentsPage.clickNextCourseLink();
        assertTrue("URL: " + browser.driver.getCurrentUrl() ,browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE + 
                "?user=comments.idOfInstructor1OfCourse1&courseid=comments.idOfArchivedCourse"));
        
        commentsPage.clickPreviousCourseLink();
        assertTrue("URL: " + browser.driver.getCurrentUrl(), browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE + 
                "?user=comments.idOfInstructor1OfCourse1&courseid=comments.idOfTypicalCourse1"));
        
        commentsPage.clickIsIncludeArchivedCoursesCheckbox();
        
        ______TS("script: filter comments");
        
        commentsPage.clickShowMoreOptions();
        
        commentsPage.loadResponseComments();
        commentsPage.showCommentsForAll();
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForAll.html");
        
        int studentCommentPanelIdx = 1;
        commentsPage.showCommentsForPanel(studentCommentPanelIdx);
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForStudentCommentPanel.html");
        
        int sessionCommentPanelIdx = 2;
        commentsPage.showCommentsForPanel(sessionCommentPanelIdx);
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForSessionCommentPanel.html");
        
        commentsPage.showCommentsFromAll();
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromAll.html");
        
        commentsPage.showCommentsFromGiver("you");
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromYou.html");
        
        commentsPage.showCommentsFromGiver("others");
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromOthers.html");
        
        commentsPage.showCommentsFromAllStatus();
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsFromAllStatus.html");
        
        commentsPage.showCommentsForStatus("public");
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForPublic.html");
        
        commentsPage.showCommentsForStatus("private");
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageShowCommentsForPrivate.html");
    }

    private void testActions() throws Exception {
        ______TS("action: edit student comment");
        commentsPage.clickStudentCommentEditForRow(1);
        commentsPage.clickStudentCommentVisibilityEdit(1);
        commentsPage.clickAllCheckboxes(1);
        commentsPage.fillTextareaToEditStudentCommentForRow(1, "");
        commentsPage.saveEditStudentCommentForRow(1);
        commentsPage.verifyStatus("Please enter a valid comment. The comment can't be empty.");
        commentsPage.fillTextareaToEditStudentCommentForRow(1, "edited student comment\na new line");
        commentsPage.saveEditStudentCommentForRow(1);
        commentsPage.verifyContains("edited student comment\n<br />a new line");
        commentsPage.verifyStatus(Const.StatusMessages.COMMENT_EDITED);

        ______TS("action: edit anonymous comment");
        commentsPage.clickStudentCommentEditForRow(10);
        commentsPage.saveEditStudentCommentForRow(10);
        commentsPage.verifyStatus(Const.StatusMessages.COMMENT_EDITED);
        
        ______TS("action: delete student comment");
        commentsPage.clickHiddenElementAndCancel("commentdelete-" + 1);
        commentsPage.clickHiddenElementAndConfirm("commentdelete-" + 1);
        commentsPage.verifyStatus(Const.StatusMessages.COMMENT_DELETED);
        
        commentsPage.loadResponseComments();
        ______TS("action: add feedback response comment");
        commentsPage.clickResponseCommentAdd(1, 1, 1);
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, "");
        commentsPage.addResponseComment(1, 1, 1);
        commentsPage.verifyCommentFormErrorMessage("1-1-1", "Comment cannot be empty");
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, "added response comment");
        commentsPage.addResponseComment(1, 1, 1);
        commentsPage.reloadPage();
        commentsPage.loadResponseComments();
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageAddFrc.html");
        
        ______TS("action: edit feedback response comment");
        commentsPage.clickResponseCommentEdit(1, 1, 1, 1);
        commentsPage.clickResponseCommentVisibilityEdit("1-1-1-1");
        commentsPage.clickAllCheckboxes("1-1-1-1");
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, 1, "");
        commentsPage.saveResponseComment(1, 1, 1, 1);
        commentsPage.verifyCommentFormErrorMessage("1-1-1-1", "Comment cannot be empty");
        commentsPage.fillTextareaToEditResponseComment(1, 1, 1, 1, "edited response comment\na new line");
        commentsPage.saveResponseComment(1, 1, 1, 1);
        commentsPage.reloadPage();
        commentsPage.loadResponseComments();
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageEditFrc.html");
        
        ______TS("action: delete feedback response comment");
        commentsPage.clickResponseCommentDelete(1, 1, 1, 1);
        commentsPage.reloadPage();
        commentsPage.loadResponseComments();
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageDeleteFrc.html");
    }
    
    private void testSearch() throws Exception {
        ______TS("search: empty string");
        commentsPage.search("");
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageSearchEmpty.html");
        commentsPage.clickCommentsPageLinkInHeader();
        
        ______TS("search: typical successful case");
        //prepare search document
        // TODO: remove this and use backdoor to put  document
        commentsPage.clickStudentCommentEditForRow(1);
        commentsPage.saveEditStudentCommentForRow(1);
        commentsPage.clickStudentCommentEditForRow(2);
        commentsPage.saveEditStudentCommentForRow(2);
        commentsPage.loadResponseComments();
        commentsPage.clickResponseCommentEdit(1, 1, 1, 1);
        commentsPage.saveResponseComment(1, 1, 1, 1);
        commentsPage.clickCommentsPageLinkInHeader();
        
        commentsPage.search("comments");
        commentsPage.verifyHtmlMainContent("/instructorCommentsPageSearchNormal.html");
        commentsPage.clickCommentsPageLinkInHeader();
    }
    
    private void testEmailPendingComments() {
        InstructorHomePage homePage = commentsPage.clickHomePageLinkInHeader();
        homePage.verifyContains("Send email notification to recipients of 2 pending comments");
        commentsPage.clickCommentsPageLinkInHeader();
        commentsPage.clickSendEmailNotificationButton();
        commentsPage.verifyStatus(Const.StatusMessages.COMMENT_CLEARED);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
