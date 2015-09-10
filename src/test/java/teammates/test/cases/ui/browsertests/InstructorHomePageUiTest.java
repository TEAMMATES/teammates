package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.pageobjects.InstructorHelpPage;
import teammates.test.pageobjects.InstructorHomePage;

/**
 * Tests Home page and login page for instructors. 
 * SUT: {@link InstructorHomePage}.<br>
 * Uses a real account.
 * 
 */
public class InstructorHomePageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private static InstructorHomePage homePage;
    
    private static FeedbackSessionAttributes feedbackSession_AWAITING;
    private static FeedbackSessionAttributes feedbackSession_OPEN;
    private static FeedbackSessionAttributes feedbackSession_CLOSED;
    private static FeedbackSessionAttributes feedbackSession_PUBLISHED;

    // TODO: refactor this test. try to use admin login or create instructors and courses not using json 
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorHomePageUiTest1.json");
        removeTestDataOnServer(loadDataBundle("/InstructorHomePageUiTest3.json"));
        restoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    private static void loadFinalHomePageTestData() throws Exception {
        
        testData = loadDataBundle("/InstructorHomePageUiTest3.json");
        removeAndRestoreTestDataOnServer(testData);
        
        feedbackSession_AWAITING = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSession_OPEN = testData.feedbackSessions.get("First Feedback Session");
        feedbackSession_CLOSED = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSession_PUBLISHED = testData.feedbackSessions.get("Fourth Feedback Session");
    }
    
    @Test
    public void allTests() throws Exception{
        testPersistenceCheck();
        testLogin();
        testContent();
        testAjaxCourseTableLoad();
        testShowFeedbackStatsLink();
        testHelpLink();
        testCourseLinks();
        testSearchAction();
        testSortAction();
        testRemindActions();
        testPublishUnpublishActions();
        testArchiveCourseAction();
        testCopyToFsAction();
        testDeleteCourseAction();
    }
    
    private void testAjaxCourseTableLoad() throws Exception {
        DataBundle unloadedCourseTestData = loadDataBundle("/InstructorHomePageUiTestUnloadedCourse.json");
        removeAndRestoreTestDataOnServer(unloadedCourseTestData);
        loginAsInstructor("CHomeUiT.instructor.tmms.unloaded");
        
        homePage.clickHomeTab();
        homePage.verifyHtmlAjax("/InstructorHomeHTMLWithUnloadedCourse.html");
        
        loginAsCommonInstructor();
        removeTestDataOnServer(unloadedCourseTestData);
    }

    private void testPersistenceCheck() {
        
        ______TS("persistence check");
        
        loginWithPersistenceProblem();

        // This is the full HTML verification for Instructor Home Page, the rest can all be verifyMainHtml
        homePage.verifyHtml("/InstructorHomeHTMLPersistenceCheck.html");
    }

    public void testLogin(){
        
        ______TS("login");
        
        loginAsCommonInstructor();
        assertTrue(browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
    }

    private void testShowFeedbackStatsLink() throws Exception {
        WebElement viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        
        String currentValidUrl = viewResponseLink.getAttribute("href");
        
        ______TS("test case: fail, fetch response rate of invalid url");
        homePage.setViewResponseLinkValue(viewResponseLink, "/invalid/url");
        viewResponseLink.click();
        homePage.verifyHtmlAjax("/InstructorHomeHTMLResponseRateFail.html");
        
        ______TS("test case: fail to fetch response rate again, check consistency of fail message");
        viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        viewResponseLink.click();
        homePage.verifyHtmlAjax("/InstructorHomeHTMLResponseRateFail.html");
        
        ______TS("test case: pass with valid url after multiple fails");
        viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.setViewResponseLinkValue(viewResponseLink, currentValidUrl);
        viewResponseLink.click();
        homePage.verifyHtmlAjax("/instructorHomeHTMLResponseRatePass.html");
    }
    
    public void testContent() throws Exception{
        
        ______TS("content: no courses");
        
        //this case is implicitly tested when testing for 'delete course' action and
        //new instructor without sample course
        //loginAsInstructor(testData.accounts.get("newInstructorWithSampleCourse").email);
        ______TS("content: new instructor, with status message HINT_FOR_NEW_INSTRUCTOR");
        
        //already logged in
        homePage.clickHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeNewInstructorWithoutSampleCourse.html");
        
        testData = loadDataBundle("/InstructorHomePageUiTest2.json");
        removeAndRestoreTestDataOnServer(testData);
        homePage.clickHomeTab();
        homePage.verifyHtmlAjax("/InstructorHomeNewInstructorWithSampleCourse.html");
        
        ______TS("content: multiple courses");
        
        loadFinalHomePageTestData();
        homePage.clickHomeTab();
        // Should not see private session
        homePage.verifyHtmlAjax("/InstructorHomeHTMLWithHelperView.html");
        updateInstructorToCoownerPrivileges();
        homePage.clickHomeTab();
        homePage.verifyHtmlAjax("/InstructorHomeHTML.html");
    }

    private void updateInstructorToCoownerPrivileges() {
        // update current instructor for CS1101 to have Co-owner privileges
        InstructorAttributes instructor = testData.instructors.get("CHomeUiT.instr.CS1101");
        BackDoor.deleteInstructor(instructor.courseId, instructor.email);
        instructor.privileges = instructor.getInstructorPrivilegesFromText();
        instructor.privileges.setDefaultPrivilegesForCoowner();
        instructor.instructorPrivilegesAsText = instructor.getTextFromInstructorPrivileges();
        BackDoor.createInstructor(instructor);
    }
    
    public void testHelpLink() throws Exception{
        
        ______TS("link: help page");
        
        InstructorHelpPage helpPage = homePage.clickHelpLink();
        helpPage.closeCurrentWindowAndSwitchToParentWindow();
        
    }
    
    public void testCourseLinks(){
        String courseId = testData.courses.get("CHomeUiT.CS1101").id;
        String instructorId = testData.accounts.get("account").googleId;
        
        ______TS("link: course enroll");
        InstructorCourseEnrollPage enrollPage = homePage.clickCourseErollLink(courseId);
        enrollPage.verifyContains("Enroll Students for CHomeUiT.CS1101");
        String expectedEnrollLinkText = TestProperties.inst().TEAMMATES_URL + 
                Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE + 
                "?" + Const.ParamsNames.COURSE_ID + "=" + courseId + 
                "&" + Const.ParamsNames.USER_ID + "=" + instructorId;
        assertEquals(expectedEnrollLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: course view");
        InstructorCourseDetailsPage detailsPage = homePage.clickCourseViewLink(courseId);
        detailsPage.verifyContains("Course Details");
        String expectedViewLinkText = TestProperties.inst().TEAMMATES_URL + 
                Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE + 
                "?" + Const.ParamsNames.COURSE_ID + "=" + courseId + 
                "&" + Const.ParamsNames.USER_ID + "=" + instructorId;
        assertEquals(expectedViewLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: course edit");
        InstructorCourseEditPage editPage = homePage.clickCourseEditLink(courseId);
        editPage.verifyContains("Edit Course Details");
        String expectedEditLinkText = TestProperties.inst().TEAMMATES_URL + 
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE + 
                "?" + Const.ParamsNames.COURSE_ID + "=" + courseId + 
                "&" +  Const.ParamsNames.USER_ID + "=" + instructorId;
        assertEquals(expectedEditLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: course add session");
        InstructorFeedbacksPage feedbacksPage =  homePage.clickCourseAddEvaluationLink(courseId);
        feedbacksPage.verifyContains("Add New Feedback Session");
        String expectedAddSessionLinkText = TestProperties.inst().TEAMMATES_URL + 
                Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + 
                "?" + Const.ParamsNames.USER_ID + "=" + instructorId +
                "&" + Const.ParamsNames.COURSE_ID + "=" + courseId;
        assertEquals(expectedAddSessionLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
    }
    
    
    
    public void testRemindActions(){
        
        ______TS("remind action: AWAITING feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSession_AWAITING.courseId, feedbackSession_AWAITING.feedbackSessionName));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSession_AWAITING.courseId, feedbackSession_AWAITING.feedbackSessionName));
        
        ______TS("remind action: OPEN feedback session - outer button");
        
        homePage.clickAndCancel(homePage.getRemindLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName));
        homePage.clickAndConfirm(homePage.getRemindLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName));
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        
        //go back to previous page because 'send reminder' redirects to the 'Feedbacks' page.
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind action: OPEN feedback session - inner button");
        
        homePage.clickRemindOptionsLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.clickAndCancel(homePage.getRemindInnerLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName));
        homePage.clickRemindOptionsLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.clickAndConfirm(homePage.getRemindInnerLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName));
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        
        //go back to previous page because 'send reminder' redirects to the 'Feedbacks' page.
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind particular users action: OPEN feedback session");
        
        homePage.clickRemindOptionsLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.clickRemindParticularUsersLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.cancelRemindParticularUsersForm();
        
        homePage.clickRemindOptionsLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.clickRemindParticularUsersLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.submitRemindParticularUsersForm();
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT);
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        homePage.clickRemindOptionsLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.clickRemindParticularUsersLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName);
        homePage.fillRemindParticularUsersForm();
        homePage.submitRemindParticularUsersForm();
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind action: CLOSED feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSession_CLOSED.courseId, feedbackSession_CLOSED.feedbackSessionName));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSession_CLOSED.courseId, feedbackSession_CLOSED.feedbackSessionName));
        
        ______TS("remind action: PUBLISHED feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSession_PUBLISHED.courseId, feedbackSession_PUBLISHED.feedbackSessionName));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSession_PUBLISHED.courseId, feedbackSession_PUBLISHED.feedbackSessionName));

    }

    public void testPublishUnpublishActions(){
        //TODO add test for publishing and unpublishing feedback sessions
    }
    
    public void testArchiveCourseAction() throws Exception {
        String courseIdForCS1101 = testData.courses.get("CHomeUiT.CS1101").id;

        ______TS("archive course action: click and cancel");
        
        homePage.clickArchiveCourseLinkAndCancel(courseIdForCS1101);

        InstructorAttributes instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
        InstructorAttributes helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);

        // Both will be null before it is archived for testing
        assertNull(instructor.isArchived);
        assertNull(helper.isArchived);

        assertFalse(BackDoor.getCourse(courseIdForCS1101).isArchived);
        
        ______TS("archive course action: click and confirm");
        
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);
        
        // archiving should only modify the isArchived status on the instructor
        instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
        helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);
        assertTrue(instructor.isArchived);
        assertTrue(helper.isArchived == null || !helper.isArchived);
        
        // the course's isArchived status should not be modified
        assertFalse(BackDoor.getCourse(courseIdForCS1101).isArchived);
        
        homePage.verifyHtmlAjax("/instructorHomeCourseArchiveSuccessful.html");
        
        ______TS("archive action failed");
        
        String courseIdForCS2104 = testData.courses.get("CHomeUiT.CS2104").id;
        
        //delete the course, then submit archive request to it
        Url urlToArchive = homePage.getArchiveCourseLink(courseIdForCS2104);
        homePage.clickAndConfirm(homePage.getDeleteCourseLink(courseIdForCS2104));
        browser.driver.get(urlToArchive.toString());
        assertTrue(browser.driver.getCurrentUrl().endsWith(Const.ViewURIs.UNAUTHORIZED));
        
        //restore
        testData = loadDataBundle("/InstructorHomePageUiTest3.json");
        removeAndRestoreTestDataOnServer(testData);
        loginAsCommonInstructor();
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);
        homePage.clickHomeTab();
    }
    
    public void testCopyToFsAction() throws Exception {
        String feedbackSessionName = "First Feedback Session";
        String courseId = testData.courses.get("CHomeUiT.CS2104").id;
        
        ______TS("Submit empty course list: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.waitForModalToLoad();
        homePage.clickFsCopySubmitButton();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        
        ______TS("Copying fails due to fs with same name in course selected: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.waitForModalToLoad();
        homePage.fillCopyToOtherCoursesForm(feedbackSessionName);
        
        homePage.clickFsCopySubmitButton();
        
        String error = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS, feedbackSessionName, courseId);
        
        homePage.verifyStatus(error);
        
        ______TS("Copying fails due to fs with invalid name: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.waitForModalToLoad();
        homePage.fillCopyToOtherCoursesForm("Invalid name | for feedback session");
        
        homePage.clickFsCopySubmitButton();
        
        homePage.verifyStatus("\"Invalid name | for feedback session\" is not acceptable to TEAMMATES as feedback session name because it contains invalid characters. All feedback session name must start with an alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).");
        
        ______TS("Successful case: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.waitForModalToLoad();
        homePage.fillCopyToOtherCoursesForm("New name!");
        
        homePage.clickFsCopySubmitButton();
        
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("Failure case: Ajax error");
        
        // Change action link so that ajax will fail
        homePage.changeFsCopyButtonActionLink(courseId, feedbackSessionName, "/page/nonExistentPage?");
        // Click copy
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        // Wait for modal to appear and show error.
        homePage.waitForModalErrorToLoad();
        
        
    }

    public void testDeleteCourseAction() throws Exception{
        
        ______TS("delete course action");
        
        String courseId = testData.courses.get("CHomeUiT.CS2104").id;
        homePage.clickAndCancel(homePage.getDeleteCourseLink(courseId));
        assertNotNull(BackDoor.getCourse(courseId));
        
        homePage.clickAndConfirm(homePage.getDeleteCourseLink(courseId));
        assertTrue(BackDoor.isCourseNonExistent(courseId));
        homePage.verifyHtmlAjax("/instructorHomeCourseDeleteSuccessful.html");
        
        //delete the other course as well
        courseId = testData.courses.get("CHomeUiT.CS1101").id;
        BackDoor.deleteCourse(courseId);
        
        homePage.clickHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLEmpty.html");
        
    }
    
    public void testSearchAction() throws Exception{
        // Tested in student list page
    }
    
    public void testSortAction() throws Exception{
        ______TS("sort by id");
        homePage.clickSortByIdButton();
        homePage.verifyHtmlAjax("/InstructorHomeHTMLSortById.html");
        
        ______TS("sort by name");
        homePage.clickSortByNameButton();
        homePage.verifyHtmlAjax("/InstructorHomeHTMLSortByName.html");
        
        ______TS("sort by date");
        homePage.clickSortByDateButton();
        homePage.verifyHtmlAjax("/InstructorHomeHTMLSortByDate.html");
    }
    
    private void loginAsCommonInstructor(){
        String commonInstructor = "CHomeUiT.instructor.tmms";
        loginAsInstructor(commonInstructor);
    }
    
    private void loginAsInstructor(String googleId){
         Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                    .withUserId(googleId);
        
        homePage = loginAdminToPage(browser, editUrl, InstructorHomePage.class);
    }

    private void loginWithPersistenceProblem() {
        Url homeUrl = createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                    .withParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "something")
                    .withUserId("unreg_user");
        
        homePage = loginAdminToPage(browser, homeUrl, InstructorHomePage.class);
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
