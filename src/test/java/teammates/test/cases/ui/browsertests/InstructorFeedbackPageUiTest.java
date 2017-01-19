package teammates.test.cases.ui.browsertests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

/**
 * Covers the 'Feedback Session' page for instructors.
 * SUT is {@link InstructorFeedbacksPage}.
 */
@Priority(-1)
public class InstructorFeedbackPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorFeedbacksPage feedbackPage;
    private static DataBundle testData;
    private static String idOfInstructorWithSessions;
    /** This contains data for the new feedback session to be created during testing */
    private static FeedbackSessionAttributes newSession;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        
        newSession = new FeedbackSessionAttributes();
        newSession.setCourseId("CFeedbackUiT.CS1101");
        newSession.setFeedbackSessionName("New Session ##");
        // start time is in future, hence the year.
        newSession.setStartTime(TimeHelper.convertToDate("2035-04-01 11:59 PM UTC"));
        newSession.setEndTime(TimeHelper.convertToDate("2035-04-30 10:00 PM UTC"));
        newSession.setCreatorEmail("teammates.test1@gmail.tmt");
        newSession.setCreatedTime(Const.TIME_REPRESENTS_NEVER);
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        newSession.setGracePeriod(0);
        newSession.setInstructions(new Text("Please fill in the new feedback session."));
        newSession.setSentOpenEmail(false);
        newSession.setSentPublishedEmail(false);
        newSession.setTimeZone(8.0);
        newSession.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        newSession.setClosingEmailEnabled(true);
        newSession.setPublishedEmailEnabled(true);
            
        browser = BrowserPool.getBrowser();
    }
    
    @BeforeMethod
    public void refreshTestData() {
        testData = loadDataBundle("/InstructorFeedbackPageUiTest.json");
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
        testDeleteAction();
    }

    @Test
    public void testRemindPublishActions() throws Exception {
        testRemindActions();
        testPublishAction();
        testUnpublishAction();
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
                0, 1, "Awaiting Session #{*}First Session #1{*}Manual Session #1{*}Open Session #{*}Private Session #");
        feedbackPage.sortByName().verifyTablePattern(
                0, 1, "Private Session #{*}Open Session #{*}Manual Session #1{*}First Session #1{*}Awaiting Session #");
        
        
        ______TS("sort by course id");
        
        feedbackPage.sortById().verifyTablePattern(
                0, 0, "CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS2104"
                      + "{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104");
        feedbackPage.sortById().verifyTablePattern(
                0, 0, "CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104"
                      + "{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101");
    
    }
    
    public void testAddAction() throws Exception {
        
        // TODO: possibly remove some of the test cases below in the future
        
        ______TS("success case: defaults: visible when open, manual publish");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.selectSessionType("Session with your own questions");
        
        feedbackPage.clickEditUncommonSettingsButton();
        
        feedbackPage.clickManualPublishTimeButton();
        
        Text instructions = newSession.getInstructions();

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getEndTime(), newSession.getStartTime(), null, null,
                instructions, newSession.getGracePeriod());
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_END_TIME_EARLIER_THAN_START_TIME);
        assertEquals("<p>" + instructions.getValue() + "</p>", feedbackPage.getInstructions());

        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, null,
                instructions, newSession.getGracePeriod());
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_ADDED);

        FeedbackSessionAttributes savedSession =
                BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());

        // TinyMCE wraps text with <p> tag
        newSession.setInstructions(new Text("<p>" + instructions.getValue() + "</p>"));
        assertEquals(newSession.toString(), savedSession.toString());
        newSession.setInstructions(instructions);

        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackAddSuccess.html");
        
        
        ______TS("success case: Add a Team Peer Evaluation Session(template session)");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickManualPublishTimeButton();
        
        feedbackPage.selectSessionType("Team peer evaluation session");
        
        String templateSessionName = "Team Peer Evaluation Session";
        feedbackPage.addFeedbackSession(
                templateSessionName, newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, null,
                newSession.getInstructions(), newSession.getGracePeriod());
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackTeamPeerEvalTemplateAddSuccess.html");
        //TODO: check that the questions created match. Maybe do that in action test.

        //Remove added session to prevent state leaks.
        assertEquals("[BACKDOOR_STATUS_SUCCESS]",
                     BackDoor.deleteFeedbackSession(templateSessionName, newSession.getCourseId()));
        
        
        ______TS("failure case: session exists already");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, null,
                newSession.getInstructions(), newSession.getGracePeriod());
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
        
        ______TS("success case: private session, boundary length name, timezone = 5.75, only results email");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickNeverVisibleTimeButton();
        
        //verify that timeFrameTable, instructions and ResponseVisTable are all hidden
        feedbackPage.verifyHidden(By.id("timeFramePanel"));
        feedbackPage.verifyHidden(By.id("responsesVisibleFromColumn"));
        feedbackPage.verifyHidden(By.id("instructionsRow"));
        
        newSession.setFeedbackSessionName("private session of characters1234567 #");
        newSession.setCourseId("CFeedbackUiT.CS2104");
        newSession.setTimeZone(5.75);
        newSession.setEndTime(null);
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
        
        newSession.setClosingEmailEnabled(false);
        newSession.setPublishedEmailEnabled(true);
        
        // disable emails for opening and closing
        feedbackPage.toggleSendOpenEmailCheckbox();
        feedbackPage.toggleSendClosingEmailCheckbox();
        
        // fill in defaults
        newSession.setInstructions(new Text("<p>Please answer all the given questions.</p>"));
        newSession.setGracePeriod(15);
        
        newSession.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                null, null, null, null,
                null, -1, newSession.getTimeZone());
        
        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        newSession.setStartTime(savedSession.getStartTime());
        
        assertEquals(newSession.toString(), savedSession.toString());
        
        
        ______TS("success case: closed session, custom session visible time, publish follows visible,"
                 + " timezone -4.5, only open email, empty instructions");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickDefaultPublishTimeButton();
        
        newSession.setFeedbackSessionName("Allow Early Viewing Session #");
        newSession.setCourseId("CFeedbackUiT.CS1101");
        newSession.setTimeZone(-4.5);
        
        newSession.setStartTime(TimeHelper.convertToDate("2004-05-01 8:00 AM UTC"));
        newSession.setEndTime(newSession.getStartTime());
        newSession.setGracePeriod(30);
        
        newSession.setSessionVisibleFromTime(TimeHelper.convertToDate("2004-03-01 5:00 PM UTC"));
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
        newSession.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        
        newSession.setInstructions(new Text(""));

        newSession.setClosingEmailEnabled(false);
        newSession.setPublishedEmailEnabled(false);
        
        // toggle emails for closing and results
        feedbackPage.toggleSendClosingEmailCheckbox();
        feedbackPage.toggleSendPublishedEmailCheckbox();
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), newSession.getSessionVisibleFromTime(), null,
                newSession.getInstructions(), newSession.getGracePeriod(), newSession.getTimeZone());
        
        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        assertEquals(newSession.toString(), savedSession.toString());
        
        
        ______TS("success case: open session, session visible atopen, responses hidden, timezone -2,"
                 + " open and close emails, special char instructions");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickDefaultVisibleTimeButton();
        feedbackPage.clickNeverPublishTimeButton();

        instructions = new Text("cannot see responses<script>test</script>$^/\\=?");

        newSession.setFeedbackSessionName("responses cant be seen my students 1 #");
        // start time in past
        newSession.setStartTime(TimeHelper.convertToDate("2012-05-01 4:00 AM UTC"));
        newSession.setEndTime(TimeHelper.convertToDate("2017-31-12 11:59 PM UTC"));
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
        newSession.setGracePeriod(25);
        newSession.setInstructions(instructions);
        newSession.setTimeZone(-2);
        newSession.setPublishedEmailEnabled(false);
        newSession.setClosingEmailEnabled(true);
        
        // enable emails for closing
        feedbackPage.toggleSendPublishedEmailCheckbox();
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, null,
                newSession.getInstructions(), newSession.getGracePeriod(), newSession.getTimeZone());

        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        newSession.sanitizeForSaving();

        newSession.setInstructions(new Text("<p>cannot see responses$^/&#61;?</p>"));

        assertEquals(newSession.toString(), savedSession.toString());

        ______TS("success case: timezone 0, custom publish time, very looong instructions (~ 500 words)");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickDefaultVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();
        newSession.setFeedbackSessionName("Long Instruction Test ##");
        newSession.setTimeZone(0);
        newSession.setStartTime(TimeHelper.convertToDate("2012-05-01 8:00 AM UTC"));
        newSession.setEndTime(TimeHelper.convertToDate("2012-09-01 11:00 PM UTC"));
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        // visible from time is in future, hence the year.
        newSession.setResultsVisibleFromTime(TimeHelper.convertToDate("2035-09-01 11:00 PM UTC"));
        newSession.setGracePeriod(5);
        
        newSession.setInstructions(new Text(StringHelper.generateStringOfLength(3000)));
        newSession.setPublishedEmailEnabled(true);
        newSession.setClosingEmailEnabled(true);
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, newSession.getResultsVisibleFromTime(),
                newSession.getInstructions(), newSession.getGracePeriod(), newSession.getTimeZone());
        
        savedSession = BackDoor.getFeedbackSession(newSession.getCourseId(), newSession.getFeedbackSessionName());
        newSession.sanitizeForSaving();
        newSession.setInstructions(new Text("<p>" + newSession.getInstructionsString() + "</p>"));
        assertEquals(newSession.toString(), savedSession.toString());
        
        
        ______TS("failure case: invalid input: (end < start < visible) and (publish < visible)");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();
        
        newSession.setFeedbackSessionName("invalid publish time #");
        newSession.setStartTime(TimeHelper.convertToDate("2012-05-01 8:00 PM UTC"));
        newSession.setEndTime(TimeHelper.convertToDate("2012-05-01 4:00 PM UTC"));
        
        newSession.setSessionVisibleFromTime(TimeHelper.convertToDate("2012-05-01 10:00 PM UTC"));
        newSession.setResultsVisibleFromTime(TimeHelper.convertToDate("2012-05-01 7:00 AM UTC"));
        newSession.setGracePeriod(30);
        newSession.setInstructions(new Text("Test instructions"));
        
        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(),
                newSession.getSessionVisibleFromTime(), newSession.getResultsVisibleFromTime(),
                newSession.getInstructions(), newSession.getGracePeriod());
        
        List<String> expectedStatusStrings = new ArrayList<String>();
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
        
        AssertHelper.assertContains(expectedStatusStrings, feedbackPage.getStatus());

        
        ______TS("failure case: invalid input (session name)");
        
        //feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        newSession.setFeedbackSessionName("bad name %% #");
        newSession.setEndTime(Const.TIME_REPRESENTS_LATER);
        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, null,
                newSession.getInstructions(), newSession.getGracePeriod());
        feedbackPage.verifyStatus(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, "bad name %% #",
                                                           FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                                                           FieldValidator.REASON_CONTAINS_INVALID_CHAR));
        
    }
    
    public void testCopyFromAction() throws Exception {
        
        ______TS("Success case: copy successfully a previous session");
        feedbackPage.copyFeedbackSession("New Session ## (Copied)", newSession.getCourseId());
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackCopySuccess.html");
        
        ______TS("Success case: copy successfully a previous session with trimmed name");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.copyFeedbackSession(" New Session ## Trimmed (Copied) ", newSession.getCourseId());
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackCopyTrimmedSuccess.html");
        
        ______TS("Failure case: copy fail since the feedback session name is the same with existing one");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.copyFeedbackSession("New Session ## (Copied)", newSession.getCourseId());
        feedbackPage.verifyStatus("A feedback session by this name already exists under this course");
       
        feedbackPage.reloadPage();
        
        ______TS("Failure case: copy fail since the feedback session name is blank");
        
        feedbackPage.copyFeedbackSession("", newSession.getCourseId());
        assertTrue(feedbackPage.isElementVisible("copyModalForm"));
        assertTrue(feedbackPage.isElementHasClass("modalCopiedSessionName", "text-box-error"));
        
        ______TS("Failure case: copy fail since the feedback session name starts with (");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.copyFeedbackSession("(New Session ##)", newSession.getCourseId());
        feedbackPage.verifyStatus(
                "\"(New Session ##)\" is not acceptable to TEAMMATES as a/an feedback session name because "
                + "it starts with a non-alphanumeric character. "
                + "All feedback session name must start with an alphanumeric character, "
                + "and cannot contain any vertical bar (|) or percent sign (%).");
        
        feedbackPage.goToPreviousPage(InstructorFeedbacksPage.class);
    }
    
    public void testCopyToAction() {
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
        feedbackPage.goToPreviousPage(InstructorFeedbacksPage.class);
        
        
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
                + "All feedback session name must start with an alphanumeric character, "
                + "and cannot contain any vertical bar (|) or percent sign (%).");
        
        feedbackPage.getFsCopyToModal().clickCloseButton();
        
        ______TS("Successful case: Feedbacks Page");
        
        feedbackPage.clickFsCopyButton(courseId, feedbackSessionName);
        feedbackPage.getFsCopyToModal().waitForModalToLoad();
        feedbackPage.getFsCopyToModal().fillFormWithAllCoursesSelected("New name!");
        
        feedbackPage.getFsCopyToModal().clickSubmitButton();
   
        feedbackPage.waitForPageToLoad();
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        
        feedbackPage.goToPreviousPage(InstructorFeedbacksPage.class);
    }

    public void testDeleteAction() throws Exception {
        
        String courseId = newSession.getCourseId();
        String sessionName = "Long Instruction Test ##";
        
        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.clickAndCancel(feedbackPage.getDeleteLink(courseId, sessionName));
        assertNotNull("session should not have been deleted",
                      BackDoor.getFeedbackSession(courseId, sessionName));
    
        feedbackPage.clickAndConfirm(feedbackPage.getDeleteLink(courseId, sessionName));
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackDeleteSuccessful.html");
        
    }

    public void testRemindActions() {
        //TODO implement this
    }
    
    public void testPublishAction() throws Exception {
        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        String courseId = testData.feedbackSessions.get("publishedSession").getCourseId();
        String sessionName = testData.feedbackSessions.get("publishedSession").getFeedbackSessionName();

        ______TS("PRIVATE: publish link unclickable");
        
        courseId = testData.feedbackSessions.get("privateSession").getCourseId();
        sessionName = testData.feedbackSessions.get("privateSession").getFeedbackSessionName();

        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
        feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
        
        
        ______TS("MANUAL: publish link clickable");
        
        courseId = testData.feedbackSessions.get("manualSession").getCourseId();
        sessionName = testData.feedbackSessions.get("manualSession").getFeedbackSessionName();
        
        feedbackPage.clickAndCancel(feedbackPage.getPublishLink(courseId, sessionName));
        assertFalse(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        
        feedbackPage.clickAndConfirm(feedbackPage.getPublishLink(courseId, sessionName));
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
        assertTrue(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackPublishSuccessful.html");
        
        
        ______TS("PUBLISHED: publish link hidden");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
    }
    
    public void testUnpublishAction() throws Exception {
        // refresh page
        
        String courseId = testData.feedbackSessions.get("publishedSession").getCourseId();
        String sessionName = testData.feedbackSessions.get("publishedSession").getFeedbackSessionName();
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
        
        ______TS("PRIVATE: unpublish link unclickable");
        
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions2").googleId);
        
        courseId = testData.feedbackSessions.get("privateSession").getCourseId();
        sessionName = testData.feedbackSessions.get("privateSession").getFeedbackSessionName();
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
        feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
        
        
        ______TS("MANUAL: unpublish link clickable");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        courseId = testData.feedbackSessions.get("manualSession").getCourseId();
        sessionName = testData.feedbackSessions.get("manualSession").getFeedbackSessionName();
        
        feedbackPage.clickAndCancel(feedbackPage.getUnpublishLink(courseId, sessionName));
        assertTrue(BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        
        feedbackPage.clickAndConfirm(feedbackPage.getUnpublishLink(courseId, sessionName));
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
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
        assertTrue(feedbackPage.getStatus().contains("Failed to load sessions."));
    }
    
    public void testJScripts() {
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
        testDefaultTimeZone();
        testSessionViewableTable();
        testDatePickerScripts();
    }
    
    public void testSessionViewableTable() {
        
        ______TS("all 4 datetime elements enabled when custom is selected");
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickCustomPublishTimeButton();
        feedbackPage.clickCustomVisibleTimeButton();
        
        feedbackPage.verifyEnabled(By.id("visibledate"));
        feedbackPage.verifyEnabled(By.id("visibletime"));
        feedbackPage.verifyEnabled(By.id("publishdate"));
        feedbackPage.verifyEnabled(By.id("publishtime"));
        
        
        ______TS("all 4 datetime elements disabled when custom is deselected");
        
        feedbackPage.clickDefaultPublishTimeButton();
        feedbackPage.clickDefaultVisibleTimeButton();
        
        feedbackPage.verifyDisabled(By.id("visibledate"));
        feedbackPage.verifyDisabled(By.id("visibletime"));
        feedbackPage.verifyDisabled(By.id("publishdate"));
        feedbackPage.verifyDisabled(By.id("publishtime"));
    }
    
    public void testDatePickerScripts() {
        
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();
        
        // setup various dates
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(2014, 3, 16, 0, 0, 0);
        
        // fill in defaut values
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, cal);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, cal);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, cal);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, cal);
        

        ______TS("increasing start date does not affect end date value");
        Calendar initialCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        initialCal.setTime(cal.getTime());
        
        cal.add(Calendar.DATE, 30);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, cal);
        
        assertEquals(sdf.format(initialCal.getTime()),
                     feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE));
        
        
        ______TS("decreasing start date affects visible time, end date range and publish date range");
        
        cal.add(Calendar.DATE, -35);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()),
                     feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        assertEquals(sdf.format(cal.getTime()),
                     feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        assertEquals(sdf.format(cal.getTime()),
                     feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE));
        
        
        ______TS("decreasing end date does not affects start time or visible time");
        initialCal.setTime(cal.getTime());
        cal.add(Calendar.DATE, -50);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, cal);
        
        String valueOfStartDate = feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE);
        assertEquals(sdf.format(initialCal.getTime()),
                     valueOfStartDate);
        
        String valueOfVisibleDate = feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(sdf.format(initialCal.getTime()),
                     valueOfVisibleDate);
        
        String maxValueOfVisibleDate = feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        assertEquals(sdf.format(initialCal.getTime()),
                     maxValueOfVisibleDate);
        
        String minValueOfPublishDate = feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE);
        assertEquals(sdf.format(initialCal.getTime()),
                     minValueOfPublishDate);
        
        
        ______TS("changing visible date affects publish date range");

        cal.add(Calendar.DATE, -10);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()),
                     feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE));
        
        
        ______TS("changing publish date affects visible date range publishTime < startTime");
        
        cal.add(Calendar.DATE, 9);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()),
                     feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        
        
        ______TS("changing publish date does not affect visible date range publishTime > startTime");
        
        cal.set(2014, 1, 19);
        initialCal.setTime(cal.getTime());
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, cal);
        
        
        cal.add(Calendar.DATE, 30);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, cal);
        
        //check if maxDate is start time and not publish time
        assertEquals(sdf.format(initialCal.getTime()),
                     feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        
    }
    
    public void testDefaultTimeZone() {
        // Uses JavaScript to get client timezone as it is not affected by VM arguments.
        // This test is accurate only if the client is not in the default selected timezone.
        // If the client is, this test will always pass.
        assertEquals(feedbackPage.getClientTimeZone(), feedbackPage.getTimeZone());
    }
    
    public void testResponseRateLink() {
        ______TS("test response rate link clickable");
        
        feedbackPage.clickViewResponseLink("CFeedbackUiT.CS2104", "Private Session #");
        feedbackPage.verifyResponseValue("0 / 0", "CFeedbackUiT.CS2104", "Private Session #");
        
        
        ______TS("test response rate already displayed");
        assertEquals("0 / 0", feedbackPage.getResponseValue("CFeedbackUiT.CS1101", "Open Session #"));
    }

    public void testViewResultsLink() {
        InstructorFeedbackResultsPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;
        
        ______TS("view results clickable not creator, open session");
        
        fsa = testData.feedbackSessions.get("openSession");
        assertNull(feedbackPage.getViewResultsLink(fsa.getCourseId(), fsa.getFeedbackSessionName())
                               .getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadViewResultsLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        
        ______TS("view results clickable creator, closed session");
        
        fsa = testData.feedbackSessions.get("manualSession");
        assertNull(feedbackPage.getViewResultsLink(fsa.getCourseId(), fsa.getFeedbackSessionName())
                               .getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadViewResultsLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }
    
    public void testEditLink() {
        InstructorFeedbackEditPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;
        
        ______TS("edit link clickable when creator");
        
        fsa = testData.feedbackSessions.get("privateSession");
        assertNull(feedbackPage.getEditLink(fsa.getCourseId(), fsa.getFeedbackSessionName())
                               .getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadEditLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }
    
    public void testSubmitLink() {
        
        FeedbackSubmitPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;
        
        ______TS("submit link clickable when visible");
        
        fsa = testData.feedbackSessions.get("awaitingSession");
        assertNull(feedbackPage.getSubmitLink(fsa.getCourseId(), fsa.getFeedbackSessionName())
                               .getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadSubmitLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        
        ______TS("submit link clickable when private (never visible)");
        
        fsa = testData.feedbackSessions.get("privateSession");
        assertNull(feedbackPage.getSubmitLink(fsa.getCourseId(), fsa.getFeedbackSessionName())
                               .getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadSubmitLink(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }
    
    public void testCopySessionModalButtons() {
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
        
        feedbackPage.clickCopyTableRadioButtonAtRow(4);
        assertTrue(feedbackPage.isRowSelected(4));
        assertTrue(feedbackPage.isRadioButtonChecked(4));
        assertTrue(feedbackPage.isCopySubmitButtonEnabled());
    }
    
    public void testValidationReload() throws Exception {
        
        ______TS("form fields do not reset on form validation failure when session type is STANDARD");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.selectSessionType("Session with your own questions");
        String templateSessionName = "!Invalid name";
        feedbackPage.addFeedbackSession(
                templateSessionName, newSession.getCourseId(),
                TimeHelper.convertToDate("2035-04-01 10:00 PM UTC"),
                TimeHelper.convertToDate("2035-04-30 10:00 PM UTC"),
                null, null,
                newSession.getInstructions(), newSession.getGracePeriod());
        
        assertEquals("STANDARD", feedbackPage.getSessionType());
        assertEquals("22", feedbackPage.getStartTime());
        assertEquals("22", feedbackPage.getEndTime());
        assertEquals("8", feedbackPage.getTimeZone());
        
        
        ______TS("form fields do not reset on form validation failure when session type is TEAMEVALUATION, "
                 + "timezone is changed");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.selectSessionType("Team peer evaluation session");
        templateSessionName = "!Invalid name";
        feedbackPage.addFeedbackSessionWithTimeZone(
                templateSessionName, newSession.getCourseId(),
                TimeHelper.convertToDate("2035-04-01 10:00 AM UTC"),
                TimeHelper.convertToDate("2035-04-30 10:00 PM UTC"),
                null, null,
                newSession.getInstructions(), newSession.getGracePeriod(), -2.0);
        
        assertEquals("TEAMEVALUATION", feedbackPage.getSessionType());
        assertEquals("10", feedbackPage.getStartTime());
        assertEquals("22", feedbackPage.getEndTime());
        assertEquals("-2", feedbackPage.getTimeZone());
        
        
        ______TS("failure case: test that advanced options are still available after input is rejected");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        newSession.setFeedbackSessionName("");
        newSession.setEndTime(Const.TIME_REPRESENTS_LATER);
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickNeverPublishTimeButton();
        feedbackPage.addFeedbackSession(
                newSession.getFeedbackSessionName(), newSession.getCourseId(),
                newSession.getStartTime(), newSession.getEndTime(), null, null,
                newSession.getInstructions(),
                newSession.getGracePeriod());
        feedbackPage.verifyStatus(getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                                                           "", FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                                                           FieldValidator.REASON_EMPTY,
                                                           FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH));
        assertTrue(feedbackPage.verifyVisible(By.id("timeFramePanel")));
        assertTrue(feedbackPage.verifyVisible(By.id("responsesVisibleFromColumn")));
        assertTrue(feedbackPage.verifyVisible(By.id("instructionsRow")));
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

    private static InstructorFeedbacksPage getFeedbackPageForInstructor(String instructorId) {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE).withUserId(instructorId);
        InstructorFeedbacksPage page = loginAdminToPage(browser, feedbackPageLink, InstructorFeedbacksPage.class);
        page.waitForElementPresence(By.id("table-sessions"));
        return page;
    }

}
