package teammates.test.cases.browsertests;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorFeedbackSessionsPage;
import teammates.test.pageobjects.InstructorHelpPage;
import teammates.test.pageobjects.InstructorHomePage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageUiTest extends BaseUiTestCase {
    private InstructorHomePage homePage;

    private FeedbackSessionAttributes feedbackSessionAwaiting;
    private FeedbackSessionAttributes feedbackSessionOpen;
    private FeedbackSessionAttributes feedbackSessionClosed;
    private FeedbackSessionAttributes feedbackSessionPublished;

    // TODO: refactor this test. try to use admin login or create instructors and courses not using json

    @Override
    protected void prepareTestData() {
        removeAndRestoreDataBundle(loadDataBundle("/InstructorHomePageUiTest1.json"));
        testData = loadDataBundle("/InstructorHomePageUiTest2.json");
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
        loginAsInstructor("CHomeUiT.instructor.tmms.unloaded");

        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLWithUnloadedCourse.html");

        loginAsCommonInstructor();
    }

    private void testPersistenceCheck() throws Exception {

        ______TS("persistence check");

        loginWithPersistenceProblem();

        // This is the full HTML verification for Instructor Home Page, the rest can all be verifyMainHtml
        homePage.verifyHtml("/instructorHomeHTMLPersistenceCheck.html");
    }

    private void testLogin() {

        ______TS("login");

        loginAsNewInstructor();
        assertTrue(browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
    }

    private void testShowFeedbackStatsLink() throws Exception {
        WebElement viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        String currentValidUrl = viewResponseLink.getAttribute("href");

        ______TS("test case: fail, fetch response rate of invalid url");
        homePage.setViewResponseLinkValue(viewResponseLink, "/invalid/url");
        homePage.clickViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.verifyHtmlMainContent("/instructorHomeHTMLResponseRateFail.html");

        ______TS("test case: fail to fetch response rate again, check consistency of fail message");
        homePage.clickViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.verifyHtmlMainContent("/instructorHomeHTMLResponseRateFail.html");

        ______TS("test case: pass with valid url after multiple fails");
        viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.setViewResponseLinkValue(viewResponseLink, currentValidUrl);
        homePage.clickViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.verifyHtmlMainContent("/instructorHomeHTMLResponseRatePass.html");
    }

    private void testContent() throws Exception {

        ______TS("content: no courses");

        //this case is implicitly tested when testing for 'delete course' action and
        //new instructor without sample course
        //loginAsInstructor(testData.accounts.get("newInstructorWithSampleCourse").email);
        ______TS("content: new instructor, with status message HINT_FOR_NEW_INSTRUCTOR");

        //already logged in
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/instructorHomeNewInstructorWithoutSampleCourse.html");

        CourseAttributes newCourse = CourseAttributes
                .builder("newIns.wit-demo", "Sample Course 101", "UTC")
                .build();
        BackDoor.createCourse(newCourse);
        @SuppressWarnings("deprecation")
        InstructorAttributes instr = InstructorAttributes
                .builder("CHomeUiT.instructor.tmms.new", "newIns.wit-demo",
                        "Teammates Test New Instructor With Sample", "CHomeUiT.instructor.tmms@gmail.tmt")
                .build();
        BackDoor.createInstructor(instr);

        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/instructorHomeNewInstructorWithSampleCourse.html");

        ______TS("content: multiple courses");

        loginAsCommonInstructor();

        // Should not see private session
        homePage.verifyHtmlMainContent("/instructorHomeHTMLWithHelperView.html");
        updateInstructorToCoownerPrivileges();
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/instructorHomeHTML.html");

        ______TS("content: require sanitization");

        loginAsInstructor("CHomeUiT.idOfInstructor1OfTestingSanitizationCourse");
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/instructorHomeTestingSanitization.html");

    }

    private void updateInstructorToCoownerPrivileges() {
        // update current instructor for CS1101 to have Co-owner privileges
        InstructorAttributes instructor = testData.instructors.get("CHomeUiT.instr.CS1101");
        BackDoor.deleteInstructor(instructor.courseId, instructor.email);
        instructor.privileges.setDefaultPrivilegesForCoowner();
        BackDoor.createInstructor(instructor);
    }

    private void testHelpLink() {

        ______TS("link: help page");

        InstructorHelpPage helpPage = homePage.loadInstructorHelpTab();
        helpPage.closeCurrentWindowAndSwitchToParentWindow();

    }

    private void testCourseLinks() {
        String courseId = testData.courses.get("CHomeUiT.CS1101").getId();
        String instructorId = testData.accounts.get("account").googleId;

        ______TS("link: course enroll");
        InstructorCourseEnrollPage enrollPage = homePage.clickCourseEnrollLink(courseId);
        enrollPage.verifyContains("Enroll Students for CHomeUiT.CS1101");
        String expectedEnrollLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                                        .withCourseId(courseId)
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        assertEquals(expectedEnrollLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);

        ______TS("link: course view");
        InstructorCourseDetailsPage detailsPage = homePage.clickCourseViewLink(courseId);
        detailsPage.verifyContains("Course Details");
        String expectedViewLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                                        .withCourseId(courseId)
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        assertEquals(expectedViewLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);

        ______TS("link: course edit");
        InstructorCourseEditPage editPage = homePage.clickCourseEditLink(courseId);
        editPage.verifyContains("Edit Course Details");
        String expectedEditLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                                        .withCourseId(courseId)
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        assertEquals(expectedEditLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);

        ______TS("link: course add session");
        InstructorFeedbackSessionsPage feedbacksPage = homePage.clickCourseAddEvaluationLink(courseId);
        feedbacksPage.verifyContains("Add New Feedback Session");
        String expectedAddSessionLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                                        .withUserId(instructorId)
                                        .withCourseId(courseId)
                                        .toAbsoluteString();
        assertEquals(expectedAddSessionLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);

    }

    private void testRemindActions() {

        ______TS("remind action: AWAITING feedback session");

        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionAwaiting.getCourseId(),
                                                          feedbackSessionAwaiting.getFeedbackSessionName()));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSessionAwaiting.getCourseId(),
                                                                 feedbackSessionAwaiting.getFeedbackSessionName()));

        ______TS("remind action: OPEN feedback session - outer button");

        homePage.clickAndCancel(homePage.getRemindLink(feedbackSessionOpen.getCourseId(),
                                                       feedbackSessionOpen.getFeedbackSessionName()));
        homePage.clickAndConfirm(homePage.getRemindLink(feedbackSessionOpen.getCourseId(),
                                                        feedbackSessionOpen.getFeedbackSessionName()));

        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);

        ______TS("remind action: OPEN feedback session - inner button");

        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickAndCancel(homePage.getRemindInnerLink(feedbackSessionOpen.getCourseId(),
                                                            feedbackSessionOpen.getFeedbackSessionName()));
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickAndConfirm(homePage.getRemindInnerLink(feedbackSessionOpen.getCourseId(),
                                                             feedbackSessionOpen.getFeedbackSessionName()));
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);

        ______TS("remind particular users action: OPEN feedback session");

        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionOpen.getCourseId(),
                                                feedbackSessionOpen.getFeedbackSessionName());
        homePage.cancelRemindParticularUsersForm();

        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionOpen.getCourseId(),
                                                feedbackSessionOpen.getFeedbackSessionName());
        homePage.waitForAjaxLoaderGifToDisappear();
        homePage.submitRemindParticularUsersForm();
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT);
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionOpen.getCourseId(),
                                                feedbackSessionOpen.getFeedbackSessionName());
        homePage.waitForAjaxLoaderGifToDisappear();
        homePage.fillRemindParticularUsersForm();
        homePage.submitRemindParticularUsersForm();
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);

        ______TS("remind action: CLOSED feedback session - inner button");

        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
        homePage.clickAndCancel(homePage.getRemindInnerLink(feedbackSessionClosed.getCourseId(),
                feedbackSessionOpen.getFeedbackSessionName()));
        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
        homePage.clickAndConfirm(homePage.getRemindInnerLink(feedbackSessionClosed.getCourseId(),
                feedbackSessionClosed.getFeedbackSessionName()));
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN);

        ______TS("remind particular users action: CLOSED feedback session");

        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionClosed.getCourseId(),
                feedbackSessionClosed.getFeedbackSessionName());
        homePage.cancelRemindParticularUsersForm();

        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionClosed.getCourseId(),
                feedbackSessionClosed.getFeedbackSessionName());
        homePage.waitForAjaxLoaderGifToDisappear();
        homePage.submitRemindParticularUsersForm();
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN);

        homePage.clickRemindOptionsLink(feedbackSessionClosed.getCourseId(), feedbackSessionClosed.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionClosed.getCourseId(),
                feedbackSessionClosed.getFeedbackSessionName());
        homePage.waitForAjaxLoaderGifToDisappear();
        homePage.fillRemindParticularUsersForm();
        homePage.submitRemindParticularUsersForm();
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN);

        ______TS("remind action: PUBLISHED feedback session");

        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionPublished.getCourseId(),
                                                          feedbackSessionPublished.getFeedbackSessionName()));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSessionPublished.getCourseId(),
                                                                 feedbackSessionPublished.getFeedbackSessionName()));

    }

    private void testPublishUnpublishActions() {
        ______TS("publish action: AWAITING feedback session");

        homePage.verifyUnclickable(homePage.getSessionResultsOptionsCaretElement(feedbackSessionAwaiting.getCourseId(),
                                                           feedbackSessionAwaiting.getFeedbackSessionName()));

        ______TS("publish action: OPEN feedback session");

        homePage.clickAndCancel(homePage.getPublishLink(feedbackSessionOpen.getCourseId(),
                                                        feedbackSessionOpen.getFeedbackSessionName()));

        ______TS("publish action: CLOSED feedback session");

        homePage.clickAndCancel(homePage.getPublishLink(feedbackSessionClosed.getCourseId(),
                                                        feedbackSessionClosed.getFeedbackSessionName()));

        ______TS("unpublish action: PUBLISHED feedback session");
        homePage.clickFeedbackSessionUnpublishLink(feedbackSessionPublished.getCourseId(),
                                                   feedbackSessionPublished.getFeedbackSessionName());
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
        assertFalse(BackDoor.getFeedbackSession(feedbackSessionPublished.getCourseId(),
                                                feedbackSessionPublished.getFeedbackSessionName()).isPublished());

        ______TS("publish action: PUBLISHED feedback session");
        homePage.clickFeedbackSessionPublishLink(feedbackSessionPublished.getCourseId(),
                                                 feedbackSessionPublished.getFeedbackSessionName());
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
        assertTrue(BackDoor.getFeedbackSession(feedbackSessionPublished.getCourseId(),
                                               feedbackSessionPublished.getFeedbackSessionName()).isPublished());
    }

    private void testArchiveCourseAction() throws Exception {
        String courseIdForCS1101 = testData.courses.get("CHomeUiT.CS1101").getId();

        ______TS("archive course action: click and cancel");

        homePage.clickArchiveCourseLinkAndCancel(courseIdForCS1101);

        InstructorAttributes instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
        InstructorAttributes helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);

        // Both will be false before it is archived for testing
        assertFalse(instructor.isArchived);
        assertFalse(helper.isArchived);

        ______TS("archive course action: click and confirm");

        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);

        instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
        helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);
        assertTrue(instructor.isArchived);
        assertFalse(helper.isArchived);

        homePage.verifyHtmlMainContent("/instructorHomeCourseArchiveSuccessful.html");

        ______TS("archive action failed");

        String courseIdForCS2104 = testData.courses.get("CHomeUiT.CS2104").getId();

        //delete the course, then submit archive request to it
        BackDoor.deleteCourse(courseIdForCS2104);
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS2104);
        assertTrue(browser.driver.getCurrentUrl().contains(Url.addParamToUrl(Const.ViewURIs.UNAUTHORIZED,
                Const.ParamsNames.ERROR_FEEDBACK_URL_REQUESTED, Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE)));
        // recover the deleted course and its related entities
        testData = loadDataBundle("/InstructorHomePageUiTest2.json");
        removeAndRestoreDataBundle(testData);
        loginAsCommonInstructor();
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);
        homePage.loadInstructorHomeTab();
    }

    private void testCopyToFsAction() throws Exception {
        String feedbackSessionName = "First Feedback Session";
        String courseId = testData.courses.get("CHomeUiT.CS2104").getId();

        ______TS("Submit empty course list: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().clickSubmitButton();
        homePage.getFsCopyModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());
        homePage.getFsCopyModal().verifyStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);

        homePage.getFsCopyModal().clickCloseButton();

        ______TS("Copying fails due to fs with same name in course selected: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().fillFormWithAllCoursesSelected(feedbackSessionName);

        homePage.getFsCopyModal().clickSubmitButton();

        String error = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                                     feedbackSessionName, courseId);
        homePage.getFsCopyModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());
        homePage.getFsCopyModal().verifyStatusMessage(error);

        homePage.getFsCopyModal().clickCloseButton();

        ______TS("Copying fails due to fs with invalid name: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        String invalidFeedbackSessionName = "Invalid name | for feedback session";
        homePage.getFsCopyModal().fillFormWithAllCoursesSelected(invalidFeedbackSessionName);

        homePage.getFsCopyModal().clickSubmitButton();
        homePage.getFsCopyModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());

        homePage.getFsCopyModal().verifyStatusMessage(
                getPopulatedErrorMessage(
                    FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidFeedbackSessionName,
                    FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                    FieldValidator.REASON_CONTAINS_INVALID_CHAR));
        homePage.getFsCopyModal().clickCloseButton();

        ______TS("Successful case: Home Page");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().fillFormWithAllCoursesSelected("New name!");

        homePage.getFsCopyModal().clickSubmitButton();

        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);

        homePage.goToPreviousPage(InstructorHomePage.class);

        ______TS("Failure case: Ajax error");

        // Change action link so that ajax will fail
        homePage.changeFsCopyButtonActionLink(courseId, feedbackSessionName, "/page/nonExistentPage?");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        // Wait for modal to appear and show error.
        homePage.getFsCopyModal().waitForModalLoadingError();
        homePage.getFsCopyModal().clickCloseButton();

    }

    private void testDeleteCourseAction() throws Exception {

        ______TS("delete course action");

        String courseId = testData.courses.get("CHomeUiT.CS2104").getId();
        homePage.clickAndCancel(homePage.getDeleteCourseLink(courseId));
        assertNotNull(BackDoor.getCourse(courseId));

        homePage.clickAndConfirm(homePage.getDeleteCourseLink(courseId));
        assertNull(BackDoor.getCourse(courseId));
        homePage.verifyHtmlMainContent("/instructorHomeCourseDeleteSuccessful.html");

        //delete the other course as well
        courseId = testData.courses.get("CHomeUiT.CS1101").getId();
        BackDoor.deleteCourse(courseId);

        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLEmpty.html");

    }

    private void testSearchAction() {
        // Tested in student list page
    }

    private void testSortAction() throws Exception {
        ______TS("sort courses by id");
        homePage.clickSortByIdButton();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortById.html");

        ______TS("sort courses by name");
        homePage.clickSortByNameButton();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortByName.html");

        ______TS("sort courses by date");
        homePage.clickSortByDateButton();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortByDate.html");

        ______TS("sort sessions by session name");
        homePage.sortTablesByName();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortSessionsByName.html");

        ______TS("sort sessions by session start date");
        homePage.sortTablesByStartDate();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortSessionsByStartDate.html");

        ______TS("sort sessions by session end date");
        homePage.sortTablesByEndDate();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLSortSessionsByEndDate.html");
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
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                    .withUserId(googleId);

        homePage = loginAdminToPage(editUrl, InstructorHomePage.class);
    }

    private void loginWithPersistenceProblem() {
        AppUrl homeUrl = ((AppUrl) createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                    .withParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "something"))
                    .withUserId("unreg_user");

        homePage = loginAdminToPage(homeUrl, InstructorHomePage.class);

    }

}
