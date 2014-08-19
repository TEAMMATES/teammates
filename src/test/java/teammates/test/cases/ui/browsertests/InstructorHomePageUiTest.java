package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
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
import teammates.test.pageobjects.InstructorEvalEditPage;
import teammates.test.pageobjects.InstructorEvalPreview;
import teammates.test.pageobjects.InstructorEvalResultsPage;
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
    
    private static EvaluationAttributes firstEval_OPEN;
    private static EvaluationAttributes secondEval_PUBLISHED;
    private static EvaluationAttributes thirdEval_CLOSED;
    private static EvaluationAttributes fourthEval_AWAITING;
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
        
        firstEval_OPEN = testData.evaluations.get("First Eval");
        secondEval_PUBLISHED = testData.evaluations.get("Second Eval");
        thirdEval_CLOSED = testData.evaluations.get("Third Eval");
        fourthEval_AWAITING = testData.evaluations.get("Fourth Eval");
        
        feedbackSession_AWAITING = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSession_OPEN = testData.feedbackSessions.get("First Feedback Session");
        feedbackSession_CLOSED = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSession_PUBLISHED = testData.feedbackSessions.get("Fourth Feedback Session");
    }
    
    @Test
    public void allTests() throws Exception{
        testLogin();
        testContent();
        testShowFeedbackStatsLink();
        testHelpLink();
        testCourseLinks();
        testEvaluationLinks();
        testSearchAction();
        testSortAction();
        testRemindAction();
        testPublishUnpublishActions();
        testDeleteEvalAction();
        testArchiveCourseAction();
        testDeleteCourseAction();
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


    public void testLogin(){
        
        ______TS("login");
        
        loginAsCommonInstructor();
        assertTrue(browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
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
        homePage.verifyHtmlMainContent("/InstructorHomeNewInstructorWithSampleCourse.html");
        
        ______TS("content: multiple courses");
        
        loadFinalHomePageTestData();
        homePage.clickHomeTab();
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
    
    
    public void testEvaluationLinks(){
        
        String courseId = testData.courses.get("CHomeUiT.CS1101").id;
        String evaluation = testData.evaluations.get("Fourth Eval").name;
        
        ______TS("link: evaluation view results");
        InstructorEvalResultsPage evalResultsPage = homePage.clickSessionViewResultsLink(courseId, evaluation);
        evalResultsPage.verifyContains("Evaluation Results");
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: evaluation edit");
        InstructorEvalEditPage evalEditPage = homePage.clickSessionEditLink(courseId, evaluation);
        evalEditPage.verifyContains("Edit Evaluation");
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: evaluation preview");
        InstructorEvalPreview evalPreviewPage = homePage.clickSessionPreviewLink(courseId, evaluation);
        evalPreviewPage.verifyContains("Previewing Evaluation as");
        evalPreviewPage.closeCurrentWindowAndSwitchToParentWindow();
    }
    
    public void testRemindAction(){
        
        ______TS("remind action: AWAITING evaluation");
        
        homePage.verifyUnclickable(homePage.getRemindLink(fourthEval_AWAITING.courseId, fourthEval_AWAITING.name));
        
        ______TS("remind action: OPEN evaluation");
        
        homePage.clickAndCancel(homePage.getRemindLink(firstEval_OPEN.courseId, firstEval_OPEN.name));
        homePage.clickAndConfirm(homePage.getRemindLink(firstEval_OPEN.courseId, firstEval_OPEN.name))
            .verifyStatus(Const.StatusMessages.EVALUATION_REMINDERSSENT);
        
        //go back to previous page because 'send reminder' redirects to the 'Evaluations' page.
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind action: CLOSED evaluation");
        
        homePage.verifyUnclickable(homePage.getRemindLink(thirdEval_CLOSED.courseId, thirdEval_CLOSED.name));
        
        ______TS("remind action: PUBLISHED evaluation");
        
        homePage.verifyUnclickable(homePage.getRemindLink(secondEval_PUBLISHED.courseId, secondEval_PUBLISHED.name));
        
        ______TS("remind action: AWAITING feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSession_AWAITING.courseId, feedbackSession_AWAITING.feedbackSessionName));
        
        ______TS("remind action: OPEN feedback session");
        
        homePage.clickAndCancel(homePage.getRemindLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName));
        homePage.clickAndConfirm(homePage.getRemindLink(feedbackSession_OPEN.courseId, feedbackSession_OPEN.feedbackSessionName));
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        
        //go back to previous page because 'send reminder' redirects to the 'Feedbacks' page.
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind action: CLOSED feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSession_CLOSED.courseId, feedbackSession_CLOSED.feedbackSessionName));
        
        ______TS("remind action: PUBLISHED feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSession_PUBLISHED.courseId, feedbackSession_PUBLISHED.feedbackSessionName));

    }

    public void testPublishUnpublishActions(){
        
        ______TS("publish action: AWAITING evaluation");
        
        homePage.verifyUnclickable(homePage.getPublishLink(fourthEval_AWAITING.courseId, fourthEval_AWAITING.name));
        
        ______TS("publish action: OPEN evaluation");
        
        homePage.verifyUnclickable(homePage.getPublishLink(firstEval_OPEN.courseId, firstEval_OPEN.name));
        
        ______TS("publish action: CLOSED evaluation");
        
        String courseId = thirdEval_CLOSED.courseId;
        String evalName = thirdEval_CLOSED.name;
        
        homePage.clickAndCancel(homePage.getPublishLink(courseId, evalName));
        assertEquals(EvalStatus.CLOSED, BackDoor.getEvaluation(courseId, evalName).getStatus());
        
        homePage.clickAndConfirm(homePage.getPublishLink(courseId, evalName))
            .verifyStatus(Const.StatusMessages.EVALUATION_PUBLISHED);
        assertEquals(EvalStatus.PUBLISHED, BackDoor.getEvaluation(courseId, evalName).getStatus());
        
        ______TS("unpublish action: PUBLISHED evaluation");
        
        homePage.clickAndCancel(homePage.getUnpublishLink(courseId, evalName));
        assertEquals(EvalStatus.PUBLISHED, BackDoor.getEvaluation(courseId, evalName).getStatus());
        
        homePage.clickAndConfirm(homePage.getUnpublishLink(courseId, evalName))
            .verifyStatus(Const.StatusMessages.EVALUATION_UNPUBLISHED);
        assertEquals(EvalStatus.CLOSED, BackDoor.getEvaluation(courseId, evalName).getStatus());
    }

    public void testDeleteEvalAction() throws Exception{
        
        ______TS("delete evaluation action");
        
        homePage.clickAndCancel(homePage.getDeleteEvalLink(firstEval_OPEN.courseId, firstEval_OPEN.name));
        assertNotNull(BackDoor.getEvaluation(firstEval_OPEN.courseId, firstEval_OPEN.name));
        
        homePage.clickAndConfirm(homePage.getDeleteEvalLink(firstEval_OPEN.courseId, firstEval_OPEN.name));
        ThreadHelper.waitFor(500);
        assertTrue(BackDoor.isEvaluationNonExistent(firstEval_OPEN.courseId, firstEval_OPEN.name));
        homePage.verifyHtmlAjax("/instructorHomeEvalDeleteSuccessful.html");
        
    }
    
    public void testArchiveCourseAction() throws Exception {
        
        ______TS("archive course action");
        
        String courseIdForCS1101 = testData.courses.get("CHomeUiT.CS1101").id;
        
        homePage.clickArchiveCourseLink(courseIdForCS1101);
        
        assertTrue(BackDoor.getCourse(courseIdForCS1101).isArchived);
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
        homePage.clickArchiveCourseLink(courseIdForCS1101);
        homePage.clickHomeTab();
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

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
