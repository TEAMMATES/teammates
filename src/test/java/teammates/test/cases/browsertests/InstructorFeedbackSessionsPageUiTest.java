package teammates.test.cases.browsertests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;
import teammates.test.driver.StringHelperExtension;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.InstructorFeedbackSessionsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_SESSIONS_PAGE}.
 */
@Priority(-1)
public class InstructorFeedbackSessionsPageUiTest extends BaseUiTestCase {
    private InstructorFeedbackSessionsPage feedbackPage;
    private String idOfInstructorWithSessions;
    private CourseAttributes course;
    /** This contains data for the new feedback session to be created during testing. */
    private FeedbackSessionAttributes newSession;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackSessionsPageUiTest.json");
        course = testData.courses.get("anotherCourse");

        newSession = FeedbackSessionAttributes
                .builder("New Session ##", course.getId(), "teammates.test1@gmail.tmt")
                .withStartTime(TimeHelper.parseInstant("2035-04-01 9:59 PM +0000"))
                .withEndTime(TimeHelper.parseInstant("2035-04-30 8:00 PM +0000"))
                .withCreatedTime(Const.TIME_REPRESENTS_NEVER)
                .withSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                .withGracePeriodMinutes(0)
                .withInstructions(new Text("Please fill in the new feedback session."))
                .withSentOpenEmail(false)
                .withSentPublishedEmail(false)
                .withTimeZone(course.getTimeZone())
                .withClosingEmailEnabled(true)
                .withPublishedEmailEnabled(true)
                .build();

        // the actual test data is refreshed before each test method
    }

    @BeforeMethod
    public void refreshTestData() {
        testData = loadDataBundle("/InstructorFeedbackSessionsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        idOfInstructorWithSessions = testData.accounts.get("instructorWithSessions").googleId;
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }

    @Test
    public void testLinks() {
        testResponseRateLink();
        testViewResultsLink();
        testEditLink();
        testSubmitLink();
    }

    @Test
    public void testButtons() {
        testCopySessionModalButtons();
    }

    @Test
    public void testMiscellaneous() throws Exception {
        testAjaxErrorForLoadingSessionList();
        testValidationReload();
        testJScripts();
    }

    @Test
    public void testCopyActions() throws Exception {
        testCopyFromAction();
        testCopyToAction();
    }

    @Test
    public void testAddDeleteActions() throws Exception {
        testAddAction();
        testMoveToRecycleBinAction();
    }

    @Test
    public void testRemindPublishActions() throws Exception {
        testRemindActions();
        testPublishAndResendLinkAction();
        testUnpublishAction();
    }

    @Test
    public void testRecycleBinActions() throws Exception {
        testRestoreAction();
        testRestoreAllAction();
        testDeleteAction();
        testDeleteAllAction();
    }

    @Test
    public void testContent() throws Exception {

        ______TS("no courses");

        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);

        // This is the full HTML verification for Instructor Feedbacks Page, the rest can all be verifyMainHtml
        feedbackPage.verifyHtml("/instructorFeedbackEmptyAll.html");

        ______TS("no sessions");

        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutSessions").googleId);
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackEmptySession.html");

        ______TS("typical case with helper view");

        String helperId = testData.accounts.get("helperWithSessions").googleId;

        feedbackPage = getFeedbackPageForInstructor(helperId);
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackAllSessionTypesWithHelperView.html");

        ______TS("typical case, sort by name");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.verifyHtmlMainContent("/instructorFeedbackAllSessionTypes.html");

        feedbackPage.sortByName().verifyTablePattern(
                0, 1, "Awaiting Session #{*}First Session #1{*}Manual Session #1{*}Open Session #");
        feedbackPage.sortByName().verifyTablePattern(
                0, 1, "Open Session #{*}Manual Session #1{*}First Session #1{*}Awaiting Session #");

        ______TS("sort by course id");

        feedbackPage.sortById().verifyTablePattern(
                0, 0, "CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104");
        feedbackPage.sortById().verifyTablePattern(
                0, 0, "CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101");

    }

    private void testAddAction() throws Exception {

        // TODO: possibly remove some of the test cases below in the future
        ______TS("ui test case: test two 'change' links for uncommon settings section");

        By uncommonSettingsSection = By.id("uncommonSettingsSection");

        feedbackPage.clickEditUncommonSettingsSendEmailsButton();
        feedbackPage.verifyHtmlPart(uncommonSettingsSection,
                                    "/instructorFeedbackUncommonSettingsSendEmails.html");
        feedbackPage.clickEditUncommonSettingsSessionResponsesVisibleButton();
        feedbackPage.verifyHtmlPart(uncommonSettingsSection,
                                    "/instructorFeedbackUncommonSettings.html");

        feedbackPage.reloadPage();

        feedbackPage.clickEditUncommonSettingsSessionResponsesVisibleButton();
        feedbackPage.verifyHtmlPart(uncommonSettingsSection,
                                    "/instructorFeedbackUncommonSettingsSessionResponsesVisibility.html");
        feedbackPage.clickEditUncommonSettingsSendEmailsButton();
        feedbackPage.verifyHtmlPart(uncommonSettingsSection,
                                    "/instructorFeedbackUncommonSettings.html");

        ______TS("success case: defaults: visible when open, manual publish");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.selectSessionType("session with my own questions");

        feedbackPage.clickEditUncommonSettingsButtons();

        feedbackPage.clickManualPublishTimeButton();

        Text instructions = newSession.getInstructions();

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getEndTimeLocal(), newSession.getStartTimeLocal(), null, null,
                instructions, newSession.getGracePeriodMinutes());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_SESSION_END_TIME_EARLIER_THAN_START_TIME);
        assertEquals(getMockedTinyMceContent(instructions.getValue()), feedbackPage.getInstructions());

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null, null,
                instructions, newSession.getGracePeriodMinutes());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED);

        FeedbackSessionAttributes savedSession =
                BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());

        newSession.setInstructions(new Text(getMockedTinyMceContent(instructions.getValue())));
        assertEquals(newSession.toString(), savedSession.toString());
        newSession.setInstructions(instructions);

        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackAddSuccess.html");

        ______TS("success case: Add a Team Peer Evaluation Session(template session)");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickManualPublishTimeButton();

        feedbackPage.selectSessionType("session using template: team peer evaluation");

        String templateSessionName = "Team Peer Evaluation Session";
        feedbackPage.addFeedbackSession(
                templateSessionName, newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackTeamPeerEvalTemplateAddSuccess.html");
        //TODO: check that the questions created match. Maybe do that in action test.

        //Remove added session to prevent state leaks.
        assertEquals("[BACKDOOR_STATUS_SUCCESS]",
                     BackDoor.deleteFeedbackSession(templateSessionName, newSession.getCourseId()));

        ______TS("success case: Add a session with DST time zone and gap start time");

        course.setTimeZone(ZoneId.of("Pacific/Apia"));
        BackDoor.editCourse(course);

        String dstSessionName = "DST Session";
        LocalDateTime gapStart = TimeHelper.parseDateTimeFromSessionsForm("Fri, 30 Dec, 2011", "7", "0");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.addFeedbackSession(
                dstSessionName, newSession.getCourseId(),
                gapStart, newSession.getEndTimeLocal(), null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        String gapWarning = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_GAP,
                "start time", "Fri, 30 Dec 2011, 07:00 AM", "Sat, 31 Dec 2011, 07:00 AM WSDT (UTC+1400)");
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(gapWarning, Const.StatusMessages.FEEDBACK_SESSION_ADDED);

        assertEquals("[BACKDOOR_STATUS_SUCCESS]",
                BackDoor.deleteFeedbackSession(dstSessionName, newSession.getCourseId()));

        course.setTimeZone(newSession.getTimeZone());
        BackDoor.editCourse(course);

        ______TS("failure case: session exists already");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);

        ______TS("success case: boundary length name, only results email");

        CourseAttributes otherCourse = testData.courses.get("course");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.clickEditUncommonSettingsButtons();

        newSession.setFeedbackSessionName("Session of characters12345678000000000");
        newSession.setCourseId(otherCourse.getId());
        newSession.setTimeZone(otherCourse.getTimeZone());
        newSession.setStartTime(TimeHelper.parseInstant("2035-05-01 6:00 AM +0000"));
        newSession.setEndTime(newSession.getStartTime());
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);

        newSession.setClosingEmailEnabled(false);
        newSession.setPublishedEmailEnabled(true);

        // disable emails for opening and closing
        feedbackPage.toggleSendOpenEmailCheckbox();
        feedbackPage.toggleSendClosingEmailCheckbox();

        // fill in defaults
        newSession.setInstructions(new Text(getMockedTinyMceContent("Please answer all the given questions.")));
        newSession.setGracePeriodMinutes(15);

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        assertEquals(newSession.toString(), savedSession.toString());
        // set back to default course
        newSession.setCourseId(course.getId());
        newSession.setTimeZone(course.getTimeZone());

        ______TS("success case: closed session, custom session visible time, publish follows visible,"
                 + " only open email, empty instructions");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickDefaultPublishTimeButton();

        newSession.setFeedbackSessionName("Allow Early Viewing Session #");

        newSession.setStartTime(TimeHelper.parseInstant("2008-05-01 6:00 AM +0000"));
        newSession.setEndTime(newSession.getStartTime());
        newSession.setGracePeriodMinutes(30);

        newSession.setSessionVisibleFromTime(TimeHelper.parseInstant("2008-03-01 3:00 PM +0000"));
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);

        newSession.setInstructions(new Text(""));

        newSession.setClosingEmailEnabled(false);
        newSession.setPublishedEmailEnabled(false);

        // toggle emails for closing and results
        feedbackPage.toggleSendClosingEmailCheckbox();
        feedbackPage.toggleSendPublishedEmailCheckbox();

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(),
                newSession.getSessionVisibleFromTimeLocal(), null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        assertEquals(newSession.toString(), savedSession.toString());

        ______TS("success case: open session, session visible atopen, responses hidden, open and close emails, "
                + "special char instructions");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickDefaultVisibleTimeButton();
        feedbackPage.clickManualPublishTimeButton();

        instructions = new Text("cannot see responses<script>test</script>$^/\\=?");

        newSession.setFeedbackSessionName("responses cant be seen my students 1 #");
        // start time in past
        newSession.setStartTime(TimeHelper.parseInstant("2012-05-01 2:00 AM +0000"));
        newSession.setEndTime(TimeHelper.parseInstant("2037-12-11 9:59 PM +0000"));
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        newSession.setGracePeriodMinutes(25);
        newSession.setInstructions(new Text(getMockedTinyMceContent(instructions.getValue())));
        newSession.setPublishedEmailEnabled(false);
        newSession.setClosingEmailEnabled(true);

        // enable emails for closing
        feedbackPage.toggleSendPublishedEmailCheckbox();

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        newSession.sanitizeForSaving();

        assertEquals(newSession.toString(), savedSession.toString());

        ______TS("success case: custom publish time, very looong instructions (~ 500 words)");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickDefaultVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();
        newSession.setFeedbackSessionName("Long Instruction Test ##");
        newSession.setStartTime(TimeHelper.parseInstant("2012-05-01 8:00 AM +0000"));
        newSession.setEndTime(TimeHelper.parseInstant("2012-09-01 11:00 PM +0000"));
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        // visible from time is in future, hence the year.
        newSession.setResultsVisibleFromTime(TimeHelper.parseInstant("2035-09-01 11:00 PM +0000"));
        newSession.setGracePeriodMinutes(5);

        newSession.setInstructions(new Text(StringHelperExtension.generateStringOfLength(3000)));
        newSession.setPublishedEmailEnabled(true);
        newSession.setClosingEmailEnabled(true);

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(),
                null, newSession.getResultsVisibleFromTimeLocal(),
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        newSession.sanitizeForSaving();
        newSession.setInstructions(new Text(getMockedTinyMceContent(newSession.getInstructionsString())));
        assertEquals(newSession.toString(), savedSession.toString());

        ______TS("failure case: invalid input: (end < start < visible) and (publish < visible)");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();

        newSession.setFeedbackSessionName("invalid publish time #");
        newSession.setStartTime(TimeHelper.parseInstant("2012-05-01 12:00 PM +0000"));
        newSession.setEndTime(TimeHelper.parseInstant("2012-05-01 8:00 AM +0000"));

        newSession.setSessionVisibleFromTime(TimeHelper.parseInstant("2012-05-01 2:00 PM +0000"));
        newSession.setResultsVisibleFromTime(TimeHelper.parseInstant("2012-04-30 11:00 PM +0000"));
        newSession.setGracePeriodMinutes(30);
        newSession.setInstructions(new Text("Test instructions"));

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(),
                newSession.getSessionVisibleFromTimeLocal(), newSession.getResultsVisibleFromTimeLocal(),
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        List<String> expectedStatusStrings = new ArrayList<>();
        expectedStatusStrings.add(String.format(
                FieldValidator.TIME_FRAME_ERROR_MESSAGE,
                FieldValidator.RESULTS_VISIBLE_TIME_FIELD_NAME,
                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME));

        expectedStatusStrings.add(String.format(
                FieldValidator.TIME_FRAME_ERROR_MESSAGE,
                FieldValidator.SESSION_START_TIME_FIELD_NAME,
                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME));

        expectedStatusStrings.add(String.format(
                FieldValidator.TIME_FRAME_ERROR_MESSAGE,
                FieldValidator.SESSION_END_TIME_FIELD_NAME,
                FieldValidator.SESSION_START_TIME_FIELD_NAME));

        AssertHelper.assertContains(expectedStatusStrings, feedbackPage.getTextsForAllStatusMessagesToUser().get(0));

        ______TS("failure case: invalid input (session name)");

        //feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        newSession.setFeedbackSessionName("bad name %% #");
        newSession.setEndTime(Const.TIME_REPRESENTS_LATER);
        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, "bad name %% #",
                        FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR));

    }

    /**
     * Returns the mocked HTML content set by TinyMCE. Note: This is a basic mock that only adds paragraph tags to the
     * content, which simulates TinyMCE's behavior for plain strings. TinyMCE's more complicated behavior for other scenarios
     * is not mocked.
     */
    private String getMockedTinyMceContent(String content) {
        return "<p>" + content + "</p>";
    }

    private void testCopyFromAction() throws Exception {

        ______TS("Success case: copy successfully a previous session");
        feedbackPage.copyFeedbackSession("New Session ## (Copied)", newSession.getCourseId());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackCopySuccess.html");

        ______TS("Success case: copy successfully a previous session with trimmed name");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.copyFeedbackSession(" New Session ## Trimmed (Copied) ", newSession.getCourseId());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackCopyTrimmedSuccess.html");

        ______TS("Failure case: copy fail since the feedback session name is the same with existing one");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.copyFeedbackSession("New Session ## (Copied)", newSession.getCourseId());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                "A feedback session by this name already exists under this course");

        feedbackPage.reloadPage();

        ______TS("Failure case: copy fail since the feedback session name is blank");

        feedbackPage.copyFeedbackSession("", newSession.getCourseId());
        assertTrue(feedbackPage.isElementVisible("copyModalForm"));
        assertTrue(feedbackPage.isElementHasClass("modalCopiedSessionName", "text-box-error"));

        ______TS("Failure case: copy fail since the feedback session name starts with (");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.copyFeedbackSession("(New Session ##)", newSession.getCourseId());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                "\"(New Session ##)\" is not acceptable to TEAMMATES as a/an feedback session name because "
                + "it starts with a non-alphanumeric character. "
                + "A/An feedback session name must start with an alphanumeric character, "
                + "and cannot contain any vertical bar (|) or percent sign (%).");

        feedbackPage.goToPreviousPage(InstructorFeedbackSessionsPage.class);
    }

    private void testCopyToAction() {
        String feedbackSessionName = "Open Session #";
        String courseId = newSession.getCourseId();

        ______TS("Submit empty course list: Feedbacks Page");

        feedbackPage.clickFsCopyButton(courseId, feedbackSessionName);
        feedbackPage.getFsCopyToModal().waitForModalToLoad();
        feedbackPage.getFsCopyToModal().clickSubmitButton();
        feedbackPage.getFsCopyToModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(feedbackPage.getFsCopyToModal().isFormSubmissionStatusMessageVisible());
        feedbackPage.getFsCopyToModal().verifyStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);

        // Go back to previous page because 'copy feedback session' redirects to the 'FeedbackEdit' page.
        feedbackPage.goToPreviousPage(InstructorFeedbackSessionsPage.class);

        ______TS("Copying fails due to fs with same name in course selected: Feedbacks Page");

        feedbackPage.clickFsCopyButton(courseId, feedbackSessionName);
        feedbackPage.getFsCopyToModal().waitForModalToLoad();
        feedbackPage.getFsCopyToModal().fillFormWithAllCoursesSelected(feedbackSessionName);

        feedbackPage.getFsCopyToModal().clickSubmitButton();

        String error = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                                     feedbackSessionName, courseId);

        feedbackPage.getFsCopyToModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(feedbackPage.getFsCopyToModal().isFormSubmissionStatusMessageVisible());
        feedbackPage.getFsCopyToModal().verifyStatusMessage(error);

        feedbackPage.getFsCopyToModal().clickCloseButton();

        ______TS("Copying fails due to fs with invalid name: Feedbacks Page");

        feedbackPage.clickFsCopyButton(courseId, feedbackSessionName);
        feedbackPage.getFsCopyToModal().waitForModalToLoad();
        feedbackPage.getFsCopyToModal().fillFormWithAllCoursesSelected("Invalid name | for feedback session");

        feedbackPage.getFsCopyToModal().clickSubmitButton();

        feedbackPage.getFsCopyToModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(feedbackPage.getFsCopyToModal().isFormSubmissionStatusMessageVisible());
        feedbackPage.getFsCopyToModal().verifyStatusMessage(
                "\"Invalid name | for feedback session\" is not acceptable to TEAMMATES as a/an "
                + "feedback session name because it contains invalid characters. "
                + "A/An feedback session name must start with an alphanumeric character, "
                + "and cannot contain any vertical bar (|) or percent sign (%).");

        feedbackPage.getFsCopyToModal().clickCloseButton();

        ______TS("Successful case: Feedbacks Page");

        feedbackPage.clickFsCopyButton(courseId, feedbackSessionName);
        feedbackPage.getFsCopyToModal().waitForModalToLoad();
        feedbackPage.getFsCopyToModal().fillFormWithAllCoursesSelected("New name!");

        feedbackPage.getFsCopyToModal().clickSubmitButton();

        feedbackPage.waitForPageToLoad();
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED);

        feedbackPage.goToPreviousPage(InstructorFeedbackSessionsPage.class);
    }

    private void testMoveToRecycleBinAction() throws Exception {

        String courseId = newSession.getCourseId();
        String sessionName = "Long Instruction Test ##";

        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions)
                .moveSessionToRecycleBin(courseId, sessionName);
        newSession.setDeletedTime();

        assertTrue(feedbackPage.getTextsForAllStatusMessagesToUser()
                .contains(Const.StatusMessages.FEEDBACK_SESSION_MOVED_TO_RECYCLE_BIN));
        assertNotNull("session should not have been deleted",
                      BackDoor.getFeedbackSession(courseId, sessionName));
        assertTrue(newSession.isSessionDeleted());
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackMoveToRecycleBinSuccessful.html");

    }

    private void testRestoreAction() throws Exception {

        String instructorId = testData.accounts.get("instructorWithCS2105").googleId;
        feedbackPage = getFeedbackPageForInstructor(instructorId);
        feedbackPage.verifyHtmlMainContent("/instructorOfCS2105WithMultipleSoftDeletedSessions.html");
        FeedbackSessionAttributes session1OfCS2105 = testData.feedbackSessions.get("session1OfCS2105");
        assertTrue(session1OfCS2105.isSessionDeleted());

        feedbackPage = getFeedbackPageForInstructor(instructorId);
        feedbackPage.restoreSession(session1OfCS2105.getCourseId(), session1OfCS2105.getFeedbackSessionName());
        session1OfCS2105.resetDeletedTime();

        assertTrue(feedbackPage.getTextsForAllStatusMessagesToUser()
                .contains(Const.StatusMessages.FEEDBACK_SESSION_RESTORED));
        assertNotNull(BackDoor.getFeedbackSession(session1OfCS2105.getCourseId(), session1OfCS2105.getSessionName()));
        assertFalse(session1OfCS2105.isSessionDeleted());
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackRestoreSuccessful.html");

    }

    private void testRestoreAllAction() throws Exception {

        FeedbackSessionAttributes session2OfCS2105 = testData.feedbackSessions.get("session2OfCS2105");
        FeedbackSessionAttributes session3OfCS2105 = testData.feedbackSessions.get("session3OfCS2105");
        assertTrue(session2OfCS2105.isSessionDeleted());
        assertTrue(session3OfCS2105.isSessionDeleted());

        feedbackPage.restoreAllSessions();
        session2OfCS2105.resetDeletedTime();
        session3OfCS2105.resetDeletedTime();

        assertTrue(feedbackPage.getTextsForAllStatusMessagesToUser()
                .contains(Const.StatusMessages.FEEDBACK_SESSION_ALL_RESTORED));
        assertNotNull(BackDoor.getFeedbackSession(session2OfCS2105.getCourseId(),
                session2OfCS2105.getFeedbackSessionName()));
        assertNotNull(BackDoor.getFeedbackSession(session3OfCS2105.getCourseId(),
                session3OfCS2105.getFeedbackSessionName()));
        assertFalse(session2OfCS2105.isSessionDeleted());
        assertFalse(session3OfCS2105.isSessionDeleted());
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackRestoreAllSuccessful.html");

    }

    private void testDeleteAction() throws Exception {

        String instructorId = testData.accounts.get("instructorWithCS2106").googleId;
        feedbackPage = getFeedbackPageForInstructor(instructorId);
        feedbackPage.verifyHtmlMainContent("/instructorOfCS2106WithMultipleSoftDeletedSessions.html");
        FeedbackSessionAttributes session1OfCS2106 = testData.feedbackSessions.get("session1OfCS2106");
        assertTrue(session1OfCS2106.isSessionDeleted());

        // Delete and cancel
        feedbackPage.deleteSessionAndCancel(session1OfCS2106.getCourseId(), session1OfCS2106.getFeedbackSessionName());

        assertNotNull(BackDoor.getFeedbackSession(session1OfCS2106.getCourseId(),
                session1OfCS2106.getFeedbackSessionName()));

        // Delete and confirm
        feedbackPage = getFeedbackPageForInstructor(instructorId);
        feedbackPage.deleteSessionAndConfirm(session1OfCS2106.getCourseId(), session1OfCS2106.getFeedbackSessionName());

        assertNull(BackDoor.getFeedbackSession(session1OfCS2106.getCourseId(), session1OfCS2106.getFeedbackSessionName()));
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackDeleteSuccessful.html");

    }

    private void testDeleteAllAction() throws Exception {

        String instructorId = testData.accounts.get("instructorWithCS2106").googleId;
        FeedbackSessionAttributes session2OfCS2106 = testData.feedbackSessions.get("session2OfCS2106");
        FeedbackSessionAttributes session3OfCS2106 = testData.feedbackSessions.get("session3OfCS2106");
        assertTrue(session2OfCS2106.isSessionDeleted());
        assertTrue(session3OfCS2106.isSessionDeleted());

        // Delete all and cancel
        feedbackPage.deleteAllSessionsAndCancel();

        assertNotNull(BackDoor.getFeedbackSession(session2OfCS2106.getCourseId(),
                session2OfCS2106.getFeedbackSessionName()));
        assertNotNull(BackDoor.getFeedbackSession(session3OfCS2106.getCourseId(),
                session3OfCS2106.getFeedbackSessionName()));

        // Delete all and confirm
        feedbackPage = getFeedbackPageForInstructor(instructorId);
        feedbackPage.deleteAllSessionsAndConfirm();

        assertNull(BackDoor.getFeedbackSession(session2OfCS2106.getCourseId(), session2OfCS2106.getFeedbackSessionName()));
        assertNull(BackDoor.getFeedbackSession(session3OfCS2106.getCourseId(), session3OfCS2106.getFeedbackSessionName()));
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackDeleteAllSuccessful.html");

    }

    private void testRemindActions() {
        //TODO implement this
    }

    private void testPublishAndResendLinkAction() throws Exception {
        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        ______TS("MANUAL: publish link clickable");

        String courseId = testData.feedbackSessions.get("manualSession").getCourseId();
        String sessionName = testData.feedbackSessions.get("manualSession").getFeedbackSessionName();

        feedbackPage.clickAndCancel(feedbackPage.getPublishLink(courseId, sessionName));
        assertFalse(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());

        // Test that the resend published link button doesn't exist
        assertFalse(feedbackPage.checkIfResendPublishedEmailButtonExists(courseId, sessionName));

        feedbackPage.clickAndConfirm(feedbackPage.getPublishLink(courseId, sessionName));
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
        assertTrue(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackPublishSuccessful.html");

        ______TS("PUBLISHED: publish link hidden");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);

        ______TS("resend link action: PUBLISHED feedback session");
        // Test that the resend published link button exists
        assertTrue(feedbackPage.checkIfResendPublishedEmailButtonExists(courseId, sessionName));

        // Test that the resend published link button can be clicked and the form can be cancelled
        feedbackPage.clickResendPublishedEmailLink(courseId, sessionName);
        feedbackPage.cancelResendPublishedEmailForm();

        // Test the status message when the form is submitted with empty recipient list
        feedbackPage.clickResendPublishedEmailLink(courseId, sessionName);
        feedbackPage.waitForAjaxLoaderGifToDisappear();
        feedbackPage.submitResendPublishedEmailForm();
        feedbackPage.waitForPageToLoad();
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_EMPTY_RECIPIENT);

        // Test the status message when the form is submitted with a recipient list
        feedbackPage.clickResendPublishedEmailLink(courseId, sessionName);
        feedbackPage.waitForAjaxLoaderGifToDisappear();
        feedbackPage.fillResendPublishedEmailForm();
        feedbackPage.submitResendPublishedEmailForm();
        feedbackPage.waitForPageToLoad();
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_EMPTY_RECIPIENT);
    }

    private void testUnpublishAction() throws Exception {
        // refresh page

        String courseId = testData.feedbackSessions.get("publishedSession").getCourseId();
        String sessionName = testData.feedbackSessions.get("publishedSession").getFeedbackSessionName();
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);

        ______TS("MANUAL: unpublish link clickable");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        courseId = testData.feedbackSessions.get("manualSession").getCourseId();
        sessionName = testData.feedbackSessions.get("manualSession").getFeedbackSessionName();

        feedbackPage.clickAndCancel(feedbackPage.getUnpublishLink(courseId, sessionName));
        assertTrue(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());

        feedbackPage.clickAndConfirm(feedbackPage.getUnpublishLink(courseId, sessionName));
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
        assertFalse(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackUnpublishSuccessful.html");

        ______TS("PUBLISHED: unpublish link hidden");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);

    }

    private void testAjaxErrorForLoadingSessionList() {
        feedbackPage.changeUserIdInAjaxForSessionsForm("InvalidUserId");
        feedbackPage.reloadSessionsList();
        feedbackPage.waitForAjaxLoaderGifToDisappear();
        assertTrue(feedbackPage.getTextsForAllStatusMessagesToUser().get(0).contains("Failed to load sessions."));
    }

    private void testJScripts() {
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
        LocalDate defaultStartDate = LocalDate.now();

        testSessionViewableTable();
        testDatePickerScripts(defaultStartDate);
    }

    private void testSessionViewableTable() {

        ______TS("all 4 datetime elements enabled when custom is selected");
        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickCustomPublishTimeButton();
        feedbackPage.clickCustomVisibleTimeButton();

        assertTrue(feedbackPage.isEnabled(By.id("visibledate")));
        assertTrue(feedbackPage.isEnabled(By.id("visibletime")));
        assertTrue(feedbackPage.isEnabled(By.id("publishdate")));
        assertTrue(feedbackPage.isEnabled(By.id("publishtime")));

        ______TS("all 4 datetime elements disabled when custom is deselected");

        feedbackPage.clickDefaultPublishTimeButton();
        feedbackPage.clickDefaultVisibleTimeButton();

        assertTrue(feedbackPage.isDisabled(By.id("visibledate")));
        assertTrue(feedbackPage.isDisabled(By.id("visibletime")));
        assertTrue(feedbackPage.isDisabled(By.id("publishdate")));
        assertTrue(feedbackPage.isDisabled(By.id("publishtime")));
    }

    private void testDatePickerScripts(LocalDate defaultStartDate) {

        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy");

        ______TS("validate visible date range before editing start date");

        String maxValueOfVisibleDate = feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);

        assertEquals(formatter.format(defaultStartDate), maxValueOfVisibleDate);

        ______TS("changing visible date to start date does not affect visible time if start time is not earlier");

        feedbackPage.setStartTime(24);
        feedbackPage.setVisibleTime(1);
        String lateStartTime = feedbackPage.getStartTime();
        String earlyVisibleTime = feedbackPage.getVisibleTime();
        assertTrue(Integer.parseInt(lateStartTime) >= Integer.parseInt(earlyVisibleTime));

        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, defaultStartDate);
        assertTrue(Integer.parseInt(feedbackPage.getStartTime()) >= Integer.parseInt(feedbackPage.getVisibleTime()));

        ______TS("changing visible date to start date affects visible time if start time is earlier");

        feedbackPage.setStartTime(1);
        feedbackPage.setVisibleTime(24);
        String earlyStartTime = feedbackPage.getStartTime();
        String lateVisibleTime = feedbackPage.getVisibleTime();
        assertTrue(Integer.parseInt(earlyStartTime) < Integer.parseInt(lateVisibleTime));

        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, defaultStartDate);
        assertEquals(feedbackPage.getStartTime(), feedbackPage.getVisibleTime());

        // setup various dates for later test cases
        LocalDate initialDateTime = LocalDate.of(2014, Month.APRIL, 16);

        // fill in default values for later test cases
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, initialDateTime);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, initialDateTime);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, initialDateTime);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, initialDateTime);

        ______TS("increasing start date does not affect end date value");

        LocalDate increasedStartDate = initialDateTime.plusMonths(1);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, increasedStartDate);

        String valueOfEndDate = feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE);
        assertEquals(formatter.format(initialDateTime), valueOfEndDate);

        ______TS("decreasing start date affects visible time, end date range and publish date range");

        LocalDate decreasedStartDate = initialDateTime.minusDays(5);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, decreasedStartDate);

        String valueOfVisibleDate = feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(formatter.format(decreasedStartDate), valueOfVisibleDate);

        maxValueOfVisibleDate = feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(formatter.format(decreasedStartDate), maxValueOfVisibleDate);

        String minValueOfPublishDate = feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE);
        assertEquals(formatter.format(decreasedStartDate), minValueOfPublishDate);

        ______TS("decreasing end date does not affect start time or visible time");
        LocalDate decreasedEndDate = initialDateTime.minusMonths(2);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, decreasedEndDate);

        String valueOfStartDate = feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE);
        assertEquals(formatter.format(decreasedStartDate), valueOfStartDate);

        valueOfVisibleDate = feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(formatter.format(decreasedStartDate), valueOfVisibleDate);

        maxValueOfVisibleDate = feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(formatter.format(decreasedStartDate), maxValueOfVisibleDate);

        minValueOfPublishDate = feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE);
        assertEquals(formatter.format(decreasedStartDate), minValueOfPublishDate);

        ______TS("changing visible date affects publish date range");

        LocalDate changedVisibleDate = LocalDate.of(2014, Month.FEBRUARY, 10);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, changedVisibleDate);

        String valueOfPublishDate = feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE);
        assertEquals(formatter.format(changedVisibleDate), valueOfPublishDate);

        ______TS("changing publish date affects visible date range publishTime < startTime");

        LocalDate changedPublishDate = LocalDate.of(2014, Month.FEBRUARY, 19);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, changedPublishDate);

        valueOfVisibleDate = feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(formatter.format(changedPublishDate), valueOfVisibleDate);

        ______TS("changing publish date does not affect visible date range publishTime > startTime");

        decreasedStartDate = LocalDate.of(2014, Month.JANUARY, 19);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, decreasedStartDate);

        changedPublishDate = LocalDate.of(2014, Month.FEBRUARY, 21);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, changedPublishDate);

        //check if maxDate is start time and not publish time
        maxValueOfVisibleDate = feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(formatter.format(decreasedStartDate), maxValueOfVisibleDate);
    }

    private void testResponseRateLink() {
        ______TS("test response rate link clickable");

        feedbackPage.clickViewResponseLink("CFeedbackUiT.CS2104", "First Session #1");
        feedbackPage.verifyResponseValue("0 / 0", "CFeedbackUiT.CS2104", "First Session #1");
    }

    private void testViewResultsLink() {
        InstructorFeedbackResultsPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;

        ______TS("view results clickable not creator, open session");

        fsa = testData.feedbackSessions.get("openSession");

        feedbackResultsPage = feedbackPage.loadViewResultsLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        ______TS("view results clickable creator, closed session");

        fsa = testData.feedbackSessions.get("manualSession");

        feedbackResultsPage = feedbackPage.loadViewResultsLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }

    private void testEditLink() {
        InstructorFeedbackEditPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;

        ______TS("edit link clickable when creator");

        fsa = testData.feedbackSessions.get("manualSession");

        feedbackResultsPage = feedbackPage.loadEditLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }

    private void testSubmitLink() {

        FeedbackSubmitPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;

        ______TS("submit link clickable when visible");

        fsa = testData.feedbackSessions.get("awaitingSession");

        feedbackResultsPage = feedbackPage.loadSubmitLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }

    private void testCopySessionModalButtons() {
        feedbackPage.copyFeedbackSessionTestButtons("Session 1", newSession.getCourseId());

        assertFalse(feedbackPage.isCopySubmitButtonEnabled());

        ______TS("click on a row");

        feedbackPage.clickCopyTableAtRow(0);
        assertTrue(feedbackPage.isRowSelected(0));
        assertTrue(feedbackPage.isRadioButtonChecked(0));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());

        // row -> row
        ______TS("click on another row");

        feedbackPage.clickCopyTableAtRow(3);
        assertTrue(feedbackPage.isRowSelected(3));
        assertTrue(feedbackPage.isRadioButtonChecked(3));
        assertFalse(feedbackPage.isRowSelected(0));
        assertFalse(feedbackPage.isRadioButtonChecked(0));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());

        // row -> radio
        ______TS("click on a radio button");

        feedbackPage.clickCopyTableRadioButtonAtRow(2);
        assertTrue(feedbackPage.isRowSelected(2));
        assertTrue(feedbackPage.isRadioButtonChecked(2));
        assertFalse(feedbackPage.isRowSelected(3));
        assertFalse(feedbackPage.isRadioButtonChecked(3));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());

        // radio -> radio
        ______TS("click on another radio button");

        feedbackPage.clickCopyTableRadioButtonAtRow(1);
        assertTrue(feedbackPage.isRowSelected(1));
        assertTrue(feedbackPage.isRadioButtonChecked(1));
        assertFalse(feedbackPage.isRowSelected(2));
        assertFalse(feedbackPage.isRadioButtonChecked(2));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());

        // radio -> row
        ______TS("click on a row");

        feedbackPage.clickCopyTableAtRow(3);
        assertTrue(feedbackPage.isRowSelected(3));
        assertTrue(feedbackPage.isRadioButtonChecked(3));
        assertFalse(feedbackPage.isRowSelected(1));
        assertFalse(feedbackPage.isRadioButtonChecked(1));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());

        // row -> radio (same row)
        ______TS("click on a radio button of the same row");

        feedbackPage.clickCopyTableRadioButtonAtRow(3);
        assertTrue(feedbackPage.isRowSelected(3));
        assertTrue(feedbackPage.isRadioButtonChecked(3));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());

        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.copyFeedbackSessionTestButtons("Session 1", newSession.getCourseId());

        assertFalse(feedbackPage.isCopySubmitButtonEnabled());

        ______TS("click on a radio button after page refresh");

        feedbackPage.clickCopyTableRadioButtonAtRow(3);
        assertTrue(feedbackPage.isRowSelected(3));
        assertTrue(feedbackPage.isRadioButtonChecked(3));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());
    }

    private void testValidationReload() throws Exception {

        ______TS("form fields do not reset on form validation failure when session type is STANDARD");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.selectSessionType("session with my own questions");
        String templateSessionName = "!Invalid name";
        feedbackPage.addFeedbackSession(
                templateSessionName, newSession.getCourseId(),
                TimeHelper.parseDateTimeFromSessionsForm("Sun, 01 Apr, 2035", "22", "00"),
                TimeHelper.parseDateTimeFromSessionsForm("Mon, 30 Apr, 2035", "22", "00"),
                null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        assertEquals("STANDARD", feedbackPage.getSessionType());
        assertEquals("22", feedbackPage.getStartTime());
        assertEquals("22", feedbackPage.getEndTime());

        ______TS("form fields do not reset on form validation failure when session type is TEAMEVALUATION");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.selectSessionType("session using template: team peer evaluation");
        templateSessionName = "!Invalid name";
        feedbackPage.addFeedbackSession(
                templateSessionName, newSession.getCourseId(),
                TimeHelper.parseDateTimeFromSessionsForm("Sun, 01 Apr, 2035", "10", "00"),
                TimeHelper.parseDateTimeFromSessionsForm("Mon, 30 Apr, 2035", "22", "00"),
                null, null,
                newSession.getInstructions(), newSession.getGracePeriodMinutes());

        assertEquals("TEAMEVALUATION", feedbackPage.getSessionType());
        assertEquals("10", feedbackPage.getStartTime());
        assertEquals("22", feedbackPage.getEndTime());

        ______TS("failure case: test that advanced options are still available after input is rejected");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        newSession.setFeedbackSessionName("");
        newSession.setEndTime(Const.TIME_REPRESENTS_LATER);
        feedbackPage.clickEditUncommonSettingsButtons();
        feedbackPage.clickCustomPublishTimeButton();
        newSession.setResultsVisibleFromTime(TimeHelper.parseInstant("2035-09-01 11:00 PM +0000"));
        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTimeLocal(), newSession.getEndTimeLocal(), null,
                newSession.getResultsVisibleFromTimeLocal(),
                newSession.getInstructions(),
                newSession.getGracePeriodMinutes());
        feedbackPage.waitForTextsForAllStatusMessagesToUserEquals(
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING_FOR_SESSION_NAME,
                        FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                        FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH));
        assertTrue(feedbackPage.isVisible(By.id("timeFramePanel")));
        assertTrue(feedbackPage.isVisible(By.id("responsesVisibleFromColumn")));
        assertTrue(feedbackPage.isVisible(By.id("instructionsRow")));
    }

    private InstructorFeedbackSessionsPage getFeedbackPageForInstructor(String instructorId) {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE).withUserId(instructorId);
        InstructorFeedbackSessionsPage page = loginAdminToPage(feedbackPageLink, InstructorFeedbackSessionsPage.class);
        page.waitForElementPresence(By.id("table-sessions"));
        return page;
    }

}
