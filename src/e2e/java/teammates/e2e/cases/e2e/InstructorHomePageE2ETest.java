package teammates.e2e.cases.e2e;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorHomePage;
import teammates.e2e.util.BackDoor;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageE2ETest extends BaseE2ETestCase {
    private InstructorHomePage homePage;

    private FeedbackSessionAttributes feedbackSessionAwaiting;
    private FeedbackSessionAttributes feedbackSessionOpen;
    private FeedbackSessionAttributes feedbackSessionClosed;
    private FeedbackSessionAttributes feedbackSessionPublished;

    // TODO: refactor this test. try to use admin login or create instructors and courses not using json

    @Override
    protected void prepareTestData() {
        removeAndRestoreDataBundle(loadDataBundle("/InstructorHomePageE2ETest1.json"));
        testData = loadDataBundle("/InstructorHomePageE2ETest2.json");
        removeAndRestoreDataBundle(testData);

        feedbackSessionAwaiting = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSessionOpen = testData.feedbackSessions.get("First Feedback Session");
        feedbackSessionClosed = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSessionPublished = testData.feedbackSessions.get("Fourth Feedback Session");

        // Remove entities created during test
        BackDoor.deleteCourse("newIns.wit-demo");
        BackDoor.deleteInstructor("newIns.wit-demo", "CHomeUiT.instructor.tmms@gmail.tmt");
    }

    @Test
    public void allTests() throws Exception {
        testContent();
        testShowFeedbackStatsLink(); // useful --> interaction
        testAjaxCourseTableLoad(); // 4 courses in total, assertFalse first then click, click further to test
        testSearchAction();
        testSortAction();
        testDownloadAction();
//        testRemindActions();
//        testPublishUnpublishResendLinkActions();
        testArchiveCourseAction();
        testCopyToFsAction();
        testDeleteCourseAction();
    }

    private void testAjaxCourseTableLoad() {
        loginAsInstructor("CHomeUiT.instructor.tmms.unloaded");

        // homePage.loadInstructorHomeTab(); //(purpose is to refresh the page


        loginAsCommonInstructor();
    }


    private void testShowFeedbackStatsLink() {
// show response rate .__.
        // only need to focus on 1 course so can be anything, much easier
        WebElement viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        



        ______TS("test case: pass with valid url");
        viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.clickViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        // verify the item is loaded
    }

    private void testContent() {

        ______TS("content: multiple courses");

        getHomePage().clickInstructorLogin().loginAsInstructor("CHomeUiT.instructor.tmms", "");



        // need to verify content
        // 1. check feedback session name & course id similar to studentHome style (dont need cos can click functions inside)
        // 2. click on a course tab & expand --> use information on there to check
    // CHomeUiT.instructor.tmms.unloaded straight away load
        // assert fail at first --> unloaded modal
        // 2. download action url correct
        // 3.
        // loginAsCommonInstructor(); // (either use this or use sth else)

        logout();
    }

    private void testDownloadAction() {

        // Test that download result button exist in homePage
        homePage.verifyDownloadResultButtonExists(feedbackSessionClosed.getCourseId(),
                feedbackSessionClosed.getFeedbackSessionName());

        ______TS("Typical case: download report");

        AppUrl reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
                .withUserId("CHomeUiT.instructor.tmms")
                .withCourseId(feedbackSessionClosed.getCourseId())
                .withSessionName(feedbackSessionClosed.getFeedbackSessionName());

        homePage.verifyDownloadLink(reportUrl); // just check button clickable & leads to the correct url
    }

    private void testRemindActions() {

        ______TS("remind action: AWAITING feedback session");

        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionAwaiting.getCourseId(),
                feedbackSessionAwaiting.getFeedbackSessionName()));

        ______TS("remind action: OPEN feedback session - outer button");

        homePage.clickAndCancel(homePage.getRemindLink(feedbackSessionOpen.getCourseId(),
                feedbackSessionOpen.getFeedbackSessionName()));
        homePage.clickAndConfirm(homePage.getRemindLink(feedbackSessionOpen.getCourseId(),
                feedbackSessionOpen.getFeedbackSessionName()));

        homePage.waitForPageToLoad();
        // homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);

        ______TS("remind particular users action: OPEN feedback session");

        // this function defaults to opening the particular users modal
        homePage.getRemindLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.cancelRemindParticularUsersForm();

        homePage.getRemindLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
//        homePage.waitForAjaxLoaderGifToDisappear();
        homePage.submitRemindParticularUsersForm();

        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT);
        homePage.getRemindLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
//        homePage.waitForAjaxLoaderGifToDisappear();
        homePage.fillRemindParticularUsersForm();
        homePage.submitRemindParticularUsersForm();
        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);

//        ______TS("remind action: CLOSED feedback session - inner button");
//
//        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
//        homePage.clickAndCancel(homePage.getRemindInnerLink(feedbackSessionClosed.getCourseId(),
//                feedbackSessionOpen.getFeedbackSessionName()));
//        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
//        homePage.clickAndConfirm(homePage.getRemindInnerLink(feedbackSessionClosed.getCourseId(),
//                feedbackSessionClosed.getFeedbackSessionName()));
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN);
//
//        ______TS("remind particular users action: CLOSED feedback session");
//
//        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
//        homePage.clickRemindParticularUsersLink(feedbackSessionClosed.getCourseId(),
//                feedbackSessionClosed.getFeedbackSessionName());
//        homePage.cancelRemindParticularUsersForm();
//
//        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
//        homePage.clickRemindParticularUsersLink(feedbackSessionClosed.getCourseId(),
//                feedbackSessionClosed.getFeedbackSessionName());
//        homePage.waitForAjaxLoaderGifToDisappear();
//        homePage.submitRemindParticularUsersForm();
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN);
//
//        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
//        homePage.clickRemindParticularUsersLink(feedbackSessionClosed.getCourseId(),
//                feedbackSessionClosed.getFeedbackSessionName());
//        homePage.waitForAjaxLoaderGifToDisappear();
//        homePage.fillRemindParticularUsersForm();
//        homePage.submitRemindParticularUsersForm();
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN);

//        ______TS("remind action: PUBLISHED feedback session");
//
//        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName()));
//        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName()));
//
    }

//    private void testPublishUnpublishResendLinkActions() {
//        ______TS("publish action: AWAITING feedback session");
//
//        homePage.verifyUnclickable(homePage.getSessionResultsOptionsCaretElement(feedbackSessionAwaiting.getCourseId(),
//                feedbackSessionAwaiting.getFeedbackSessionName()));
//
//        ______TS("publish action: OPEN feedback session");
//
//        homePage.clickAndCancel(homePage.getPublishLink(feedbackSessionOpen.getCourseId(),
//                feedbackSessionOpen.getFeedbackSessionName()));
//
//        ______TS("publish action: CLOSED feedback session");
//
//        homePage.clickAndCancel(homePage.getPublishLink(feedbackSessionClosed.getCourseId(),
//                feedbackSessionClosed.getFeedbackSessionName()));
//
//        ______TS("unpublish action: PUBLISHED feedback session");
//        homePage.clickFeedbackSessionUnpublishLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
//        assertFalse(BackDoor.getFeedbackSession(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName()).isPublished());
//
//        ______TS("publish action: PUBLISHED feedback session");
//        homePage.clickFeedbackSessionPublishLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
//        assertTrue(BackDoor.getFeedbackSession(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName()).isPublished());
//
//        ______TS("resend link action: PUBLISHED feedback session");
//        // Test that the resend published link button exists for this published session
//        homePage.verifyResendPublishedEmailButtonExists(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//
//        // Test that the resend published link button can be clicked and the form can be cancelled
//        homePage.clickSessionResultsOptionsCaretElement(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.clickResendPublishedEmailLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.cancelResendPublishedEmailForm();
//
//        // Test the status message when the form is submitted with empty recipient list
//        homePage.clickSessionResultsOptionsCaretElement(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.clickResendPublishedEmailLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.waitForAjaxLoaderGifToDisappear();
//        homePage.submitResendPublishedEmailForm();
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(
//                Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_EMPTY_RECIPIENT);
//        homePage.clickSessionResultsOptionsCaretElement(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.clickResendPublishedEmailLink(feedbackSessionPublished.getCourseId(),
//                feedbackSessionPublished.getFeedbackSessionName());
//        homePage.waitForAjaxLoaderGifToDisappear();
//        homePage.fillResendPublishedEmailForm();
//        homePage.submitResendPublishedEmailForm();
//        homePage.waitForPageToLoad();
//        homePage.waitForTextsForAllStatusMessagesToUserEquals(
//                Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_EMPTY_RECIPIENT);
//
//        ______TS("resend link action: NOT PUBLISHED feedback session");
//        // Test that the resend published link button does not exist for this not published session
//        homePage.verifyResendPublishedEmailButtonDoesNotExist(feedbackSessionAwaiting.getCourseId(),
//                feedbackSessionAwaiting.getFeedbackSessionName());
//    }

    private void testArchiveCourseAction() throws Exception {
        String courseIdForCS1101 = testData.courses.get("CHomeUiT.CS1101").getId();

        ______TS("archive course action: click and cancel");

        homePage.clickArchiveCourseLinkAndCancel(courseIdForCS1101);

//        InstructorAttributes instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
//        InstructorAttributes helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);

        // Both will be false before it is archived for testing
//        assertFalse(instructor.isArchived);
//        assertFalse(helper.isArchived);

        ______TS("archive course action: click and confirm");

        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);

//        instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
//        helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);
//        assertTrue(instructor.isArchived);
//        assertFalse(helper.isArchived);

//        homePage.verifyHtmlMainContent("/instructorHomeCourseArchiveSuccessful.html");

        ______TS("archive action failed");

        String courseIdForCS2104 = testData.courses.get("CHomeUiT.CS2104").getId();

        //delete the course, then submit archive request to it
        BackDoor.deleteCourse(courseIdForCS2104);
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS2104);
        // assertTrue(browser.driver.getCurrentUrl().contains(Url.addParamToUrl(Const.ViewURIs.UNAUTHORIZED,
        //         Const.ParamsNames.ERROR_FEEDBACK_URL_REQUESTED, Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE)));
        // recover the deleted course and its related entities
        testData = loadDataBundle("/InstructorHomePageUiTest2.json");
        removeAndRestoreDataBundle(testData);
        loginAsCommonInstructor();
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);
        // homePage.loadInstructorHomeTab();
    }

    private void testCopyToFsAction() throws Exception {
        String feedbackSessionName = "First Feedback Session";
        String courseId = testData.courses.get("CHomeUiT.CS2104").getId();

        ______TS("Submit empty course list: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.verifyUnclickable(homePage.getFsCopyModal().getSubmitButton());

        homePage.getFsCopyModal().clickCancelButton();

        ______TS("Copying fails due to fs with same name in course selected: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        // DIFF: cannot fill up all courses, only 1 course can be filled at any point of time
        homePage.getFsCopyModal().fillFormWithLastCourseSelected(feedbackSessionName);

        homePage.getFsCopyModal().clickSubmitButton(); // automatically closes upon submit (checks closing action alr)

        String error = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                feedbackSessionName, courseId);
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());
        homePage.getFsCopyModal().verifyStatusMessage(error);

        ______TS("Successful case: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().fillFormWithLastCourseSelected("New name!");

        homePage.getFsCopyModal().clickSubmitButton();

        homePage.waitForPageToLoad();
        //refactor this code for sure (checks status message and done)
        // homePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
    }

    private void testDeleteCourseAction() throws Exception {

        ______TS("delete course action");

        String courseId = testData.courses.get("CHomeUiT.CS2104").getId();
        // homePage.clickAndCancel(homePage.getDeleteCourseLink(courseId));
        assertNotNull(BackDoor.getCourse(courseId));

        homePage.clickAndConfirm(homePage.getDeleteCourseLink(courseId));
        assertNotNull(BackDoor.getCourse(courseId));

        // homePage.verifyHtmlMainContent("/instructorHomeCourseDeleteSuccessful.html");

        BackDoor.deleteCourse(courseId);

        //delete the other course as well
        courseId = testData.courses.get("CHomeUiT.CS1101").getId();
        BackDoor.deleteCourse(courseId);

        // homePage.loadInstructorHomeTab();

    }

    private void testSearchAction() {
        // Tested in student list page
    }

    private void testSortAction() throws Exception {
        ______TS("sort courses by id");
        homePage.clickSortByIdButton();
//        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortById.html");

        ______TS("sort courses by name");
        homePage.clickSortByNameButton();
//        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortByName.html");

        ______TS("sort courses by date");
        homePage.clickSortByDateButton();
//        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortByDate.html");

        ______TS("sort sessions by session name");
        homePage.sortTablesByName();
//        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortSessionsByName.html");

        ______TS("sort sessions by session start date");
        homePage.sortTablesByStartDate();
//        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortSessionsByStartDate.html");

        ______TS("sort sessions by session end date");
        homePage.sortTablesByEndDate();
//        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortSessionsByEndDate.html");
    }

    private void loginAsNewInstructor() {
        String newInstructor = "CHomeUiT.instructor.tmms.new";
        loginAsInstructor(newInstructor);
    }

    private void loginAsCommonInstructor() {
        String commonInstructor = "CHomeUiT.instructor.tmms";
        loginAsInstructor(commonInstructor);
    }

    private void loginAsInstructor(String googleId) {
        AppUrl editUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                .withUserId(googleId);

        homePage = loginAdminToPage(editUrl, InstructorHomePage.class);
    }
}
