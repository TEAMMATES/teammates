package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
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
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        idOfInstructorWithSessions = testData.accounts.get("instructorWithSessions").googleId;
        
        newSession = new FeedbackSessionAttributes();
        newSession.courseId = "CFeedbackUiT.CS1101";
        newSession.feedbackSessionName = "New Session";
        // start time is in future, hence the year.
        newSession.startTime = TimeHelper.convertToDate("2035-04-01 11:59 PM UTC");
        newSession.endTime = TimeHelper.convertToDate("2035-04-30 10:00 PM UTC");
        newSession.creatorEmail = "teammates.test1@gmail.com";
        newSession.createdTime = Const.TIME_REPRESENTS_NEVER;
        newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
        newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        newSession.gracePeriod = 0;
        newSession.instructions = new Text("Please fill in the new feedback session.");
        newSession.sentOpenEmail = false;
        newSession.sentPublishedEmail = false;
        newSession.timeZone = 8.0;
        newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
        newSession.isOpeningEmailEnabled = true;
        newSession.isClosingEmailEnabled = true;
        newSession.isPublishedEmailEnabled = true;
            
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void allTests() throws Exception{
        testCopyAction();
        testContent();
        testAddAction();
        testDeleteAction();
        testPublishAction();
        testUnpublishAction();
        
        testResponseRateLink();
        testViewResultsLink();
        testEditLink();
        testSubmitLink();
        
        testJScripts();
    }
    
    public void testContent() throws Exception{
        
        ______TS("no courses");
        
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
        feedbackPage.verifyHtml("/instructorFeedbackEmptyAll.html");
        
        ______TS("no sessions");
        
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutSessions").googleId);
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackEmptySession.html");

        ______TS("typical case with helper view");
        
        String helperId = testData.accounts.get("helperWithSessions").googleId;
        
        feedbackPage = getFeedbackPageForInstructor(helperId);
        feedbackPage.verifyHtmlAjax("/instructorFeedbacksAllSessionTypesWithHelperView.html");
        
        ______TS("typical case, sort by name");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.verifyHtmlAjax("/instructorFeedbackAllSessionTypes.html");

        feedbackPage.sortByName()
            .verifyTablePattern(1,"Awaiting Session{*}Copied Session{*}First Eval{*}First Session{*}Manual Session{*}Open Session{*}Private Session");
        feedbackPage.sortByName()
            .verifyTablePattern(1,"Private Session{*}Open Session{*}Manual Session{*}First Session{*}First Eval{*}Copied Session{*}Awaiting Session");
        
        ______TS("sort by course id");
        
        feedbackPage.sortById()
            .verifyTablePattern(0,"CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101"
                    + "{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104");
        feedbackPage.sortById()
            .verifyTablePattern(0,"CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104"
                    + "{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101");
    
    }
    
    public void testAddAction() throws Exception{
        
        // TODO: possibly remove some of the test cases below in the future
        
        ______TS("success case: defaults: visible when open, manual publish");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.selectSessionType("Session with your own questions");
        
        feedbackPage.clickEditUncommonSettingsButton();
        
        feedbackPage.clickManualPublishTimeButton();
        
        feedbackPage.addFeedbackSession(
                newSession.feedbackSessionName, newSession.courseId, 
                newSession.startTime, newSession.endTime,
                null, null,
                newSession.instructions, newSession.gracePeriod );
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
        FeedbackSessionAttributes savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
        //Note: This can fail at times because Firefox fails to choose the correct value from the dropdown.
        //  in that case, rerun in Chrome.
        assertEquals(newSession.toString(), savedSession.toString());
        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackAddSuccess.html");
        
        
        ______TS("success case: Add a Team Peer Evaluation Session(template session)");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);

        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickManualPublishTimeButton();
        
        feedbackPage.selectSessionType("Team peer evaluation session");
        
        String templateSessionName = "Team Peer Evaluation Session";
        feedbackPage.addFeedbackSession(
                templateSessionName , newSession.courseId, 
                newSession.startTime, newSession.endTime,
                null, null,
                newSession.instructions, newSession.gracePeriod );
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackTeamPeerEvalTemplateAddSuccess.html");
        //TODO: check that the questions created match. Maybe do that in action test.

        //Remove added session to prevent state leaks.
        assertEquals("[BACKDOOR_STATUS_SUCCESS]",BackDoor.deleteFeedbackSession(templateSessionName, newSession.courseId));
        
        ______TS("failure case: session exists already");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.addFeedbackSession(
                newSession.feedbackSessionName, newSession.courseId,
                newSession.startTime, newSession.endTime,
                null, null,
                newSession.instructions, 
                newSession.gracePeriod );
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, feedbackPage.getStatus());
        
        ______TS("success case: private session, boundary length name, timezone = 5.75, only results email");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickNeverVisibleTimeButton();
        
        //verify that timeFrameTable, instructions and ResponseVisTable are all hidden
        feedbackPage.verifyHidden(By.id("timeFramePanel"));
        feedbackPage.verifyHidden(By.id("responsesVisibleFromColumn"));
        feedbackPage.verifyHidden(By.id("instructionsRow"));
        
        
        newSession.feedbackSessionName = "private session of characters123456789";
        newSession.courseId = "CFeedbackUiT.CS2104";
        newSession.timeZone = 5.75;
        newSession.endTime = null;
        newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
        newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
        
        newSession.isOpeningEmailEnabled = false;
        newSession.isClosingEmailEnabled = false;
        newSession.isPublishedEmailEnabled = true;
        
        // disable emails for opening and closing
        feedbackPage.toggleSendOpenEmailCheckbox();
        feedbackPage.toggleSendClosingEmailCheckbox();
        
        // fill in defaults
        newSession.instructions = new Text("Please answer all the given questions.");
        newSession.gracePeriod = 15;
        
        newSession.feedbackSessionType = FeedbackSessionType.PRIVATE;
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.feedbackSessionName, newSession.courseId,
                null, null,
                null, null,
                null,
                -1, newSession.timeZone );
        
        savedSession = BackDoor.getFeedbackSession( newSession.courseId, newSession.feedbackSessionName);
        newSession.startTime = savedSession.startTime;
        
        assertEquals(newSession.toString(), savedSession.toString());
        
        ______TS("success case: closed session, custom session visible time, publish follows visible, timezone -4.5, only open email, empty instructions");

        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickDefaultPublishTimeButton();
        
        newSession.feedbackSessionName = "Allow Early Viewing Session";
        newSession.courseId = "CFeedbackUiT.CS1101";
        newSession.timeZone = -4.5;
        
        newSession.startTime = TimeHelper.convertToDate("2004-05-01 8:00 AM UTC");
        newSession.endTime = newSession.startTime;
        newSession.gracePeriod = 30;
        
        newSession.sessionVisibleFromTime = TimeHelper.convertToDate("2004-03-01 5:00 PM UTC");
        newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
        newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
        
        newSession.instructions = new Text("");
        newSession.isOpeningEmailEnabled = true;
        newSession.isClosingEmailEnabled = false;
        newSession.isPublishedEmailEnabled = false;
        
        // toggle emails for closing and results
        feedbackPage.toggleSendClosingEmailCheckbox();
        feedbackPage.toggleSendPublishedEmailCheckbox();
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.feedbackSessionName, newSession.courseId,
                newSession.startTime, newSession.endTime,
                newSession.sessionVisibleFromTime, null,
                newSession.instructions,
                newSession.gracePeriod, newSession.timeZone );
        
        savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
        assertEquals(newSession.toString(), savedSession.toString());
        
        ______TS("success case: open session, session visible atopen, responses hidden, timezone -2, open and close emails, special char instructions");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickDefaultVisibleTimeButton();
        feedbackPage.clickNeverPublishTimeButton();
        
        newSession.feedbackSessionName = "responses cant be seen my students 1";
        // start time in past
        newSession.startTime = TimeHelper.convertToDate("2012-05-01 4:00 AM UTC");
        newSession.endTime = TimeHelper.convertToDate("2017-31-12 11:59 PM UTC");
        newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
        newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
        newSession.gracePeriod = 25;
        newSession.instructions = new Text("cannot \r\n see responses<script>test</script> $^/\\=?");
        newSession.timeZone = -2;
        newSession.isPublishedEmailEnabled = false;
        newSession.isOpeningEmailEnabled = true;
        newSession.isClosingEmailEnabled = true;
        
        // enable emails for closing
        feedbackPage.toggleSendPublishedEmailCheckbox();
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.feedbackSessionName, newSession.courseId,
                newSession.startTime, newSession.endTime,
                null, null,
                newSession.instructions,
                newSession.gracePeriod, newSession.timeZone);
        
        savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
        newSession.sanitizeForSaving();
        assertEquals(newSession.toString(), savedSession.toString());
        
        ______TS("success case: timezone 0, custom publish time, very looong instructions (~ 500 words)");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickDefaultVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();
        newSession.feedbackSessionName = "Long Instruction Test";
        newSession.timeZone = 0;
        newSession.startTime = TimeHelper.convertToDate("2012-05-01 8:00 AM UTC");
        newSession.endTime = TimeHelper.convertToDate("2012-09-01 11:00 PM UTC");
        newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
        // visible from time is in future, hence the year.
        newSession.resultsVisibleFromTime = TimeHelper.convertToDate("2035-09-01 11:00 PM UTC");
        newSession.gracePeriod = 5;
        
        newSession.instructions = new Text(StringHelper.generateStringOfLength(3000));
        newSession.isPublishedEmailEnabled = true;
        newSession.isOpeningEmailEnabled = true;
        newSession.isClosingEmailEnabled = true;
        
        feedbackPage.addFeedbackSessionWithTimeZone(
                newSession.feedbackSessionName, newSession.courseId,
                newSession.startTime, newSession.endTime,
                null, newSession.resultsVisibleFromTime,
                newSession.instructions,
                newSession.gracePeriod, newSession.timeZone );
        
        savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
        newSession.sanitizeForSaving();
        assertEquals(newSession.toString(), savedSession.toString());
        
        ______TS("failure case: invalid input: (end < start < visible) and (publish < visible)");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.clickEditUncommonSettingsButton();
        feedbackPage.clickCustomVisibleTimeButton();
        feedbackPage.clickCustomPublishTimeButton();
        
        newSession.feedbackSessionName = "invalid publish time";
        newSession.startTime = TimeHelper.convertToDate("2012-05-01 8:00 PM UTC");
        newSession.endTime = TimeHelper.convertToDate("2012-05-01 4:00 PM UTC");
        
        newSession.sessionVisibleFromTime = TimeHelper.convertToDate("2012-05-01 10:00 PM UTC");
        newSession.resultsVisibleFromTime = TimeHelper.convertToDate("2012-05-01 7:00 AM UTC");
        newSession.gracePeriod = 30;
        newSession.instructions = new Text("Test instructions");
        
        feedbackPage.addFeedbackSession(
                newSession.feedbackSessionName, newSession.courseId,
                newSession.startTime, newSession.endTime,
                newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime,
                newSession.instructions,
                newSession.gracePeriod );
        
        List<String> expectedStatusStrings = new ArrayList<String>();
        expectedStatusStrings.add(String.format(
                FieldValidator.TIME_FRAME_ERROR_MESSAGE,
                FieldValidator.RESULTS_VISIBLE_TIME_FIELD_NAME,
                FieldValidator.FEEDBACK_SESSION_NAME,
                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME));
        
        expectedStatusStrings.add(String.format(
                FieldValidator.TIME_FRAME_ERROR_MESSAGE,
                FieldValidator.START_TIME_FIELD_NAME,
                FieldValidator.FEEDBACK_SESSION_NAME,
                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME));

        expectedStatusStrings.add(String.format(
                FieldValidator.TIME_FRAME_ERROR_MESSAGE,
                FieldValidator.END_TIME_FIELD_NAME,
                FieldValidator.FEEDBACK_SESSION_NAME,
                FieldValidator.START_TIME_FIELD_NAME));
        
        AssertHelper.assertContains(expectedStatusStrings, 
                    feedbackPage.getStatus());

        ______TS("failure case: invalid input (session name)");
        
        //feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        newSession.feedbackSessionName = "bad name %%";
        newSession.endTime = Const.TIME_REPRESENTS_LATER;
        feedbackPage.addFeedbackSession(
                newSession.feedbackSessionName, newSession.courseId,
                newSession.startTime, newSession.endTime,
                null, null,
                newSession.instructions,
                newSession.gracePeriod );
        assertEquals(String.format(
                        FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        "bad name %%",
                        FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                        FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                        FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME),
                    feedbackPage.getStatus());

    }
    
    public void testCopyAction() throws Exception{
        
        ______TS("Success case: copy successfully a previous session");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.copyFeedbackSession("Copied Session", newSession.courseId);
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        // Check that we are redirected to the edit page.
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackCopySuccess.html");
        
        ______TS("Failure case: copy fail since the feedback session name is the same with existing one");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.copyFeedbackSession("Copied Session", newSession.courseId);
        feedbackPage.verifyStatus("A feedback session by this name already exists under this course");
       
        ______TS("Failure case: copy fail since the feedback session name is invalid");
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        feedbackPage.copyFeedbackSession("", newSession.courseId);
        feedbackPage.verifyStatus("Feedback session name must not be empty.");
        
    }

    public void testDeleteAction() throws Exception{
        
        String courseId = newSession.courseId;
        String sessionName = "Long Instruction Test";
        
        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        feedbackPage.clickAndCancel (feedbackPage.getDeleteLink(courseId, sessionName));
        assertNotNull ("session should not have been deleted", BackDoor.getFeedbackSession(courseId, sessionName));
    
        feedbackPage.clickAndConfirm (feedbackPage.getDeleteLink(courseId, sessionName));
        feedbackPage.verifyHtmlAjax ("/instructorFeedbackDeleteSuccessful.html");
        
    }
    
    public void testPublishAction() throws Exception {        
        // refresh page
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        String courseId = testData.feedbackSessions.get("publishedSession").courseId;
        String sessionName = testData.feedbackSessions.get("publishedSession").feedbackSessionName;

        ______TS("PRIVATE: publish link unclickable");
        
        courseId = testData.feedbackSessions.get("privateSession").courseId;
        sessionName = testData.feedbackSessions.get("privateSession").feedbackSessionName;

        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
        feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
        
        ______TS("MANUAL: publish link clickable");
        
        courseId = testData.feedbackSessions.get("manualSession").courseId;
        sessionName = testData.feedbackSessions.get("manualSession").feedbackSessionName;
        
        feedbackPage.clickAndCancel(feedbackPage.getPublishLink(courseId, sessionName));
        assertEquals(false, BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        
        feedbackPage.clickAndConfirm(feedbackPage.getPublishLink(courseId, sessionName));
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
        assertEquals(true, BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        feedbackPage.verifyHtmlAjax ("/instructorFeedbackPublishSuccessful.html");
        
        ______TS("PUBLISHED: publish link hidden");
        
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
    }
    
    public void testUnpublishAction() throws Exception {
        // refresh page
        
        String courseId = testData.feedbackSessions.get("publishedSession").courseId;
        String sessionName = testData.feedbackSessions.get("publishedSession").feedbackSessionName;
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
        
        ______TS("PRIVATE: unpublish link unclickable");
        
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions2").googleId);
        
        courseId = testData.feedbackSessions.get("privateSession").courseId;
        sessionName = testData.feedbackSessions.get("privateSession").feedbackSessionName;
        feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
        feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
        
        ______TS("MANUAL: unpublish link clickable");
        
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        courseId = testData.feedbackSessions.get("manualSession").courseId;
        sessionName = testData.feedbackSessions.get("manualSession").feedbackSessionName;
        
        feedbackPage.clickAndCancel(feedbackPage.getUnpublishLink(courseId, sessionName));
        assertEquals(true, BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        
        feedbackPage.clickAndConfirm(feedbackPage.getUnpublishLink(courseId, sessionName));
        feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
        assertEquals(false, BackDoor.getFeedbackSession(courseId, sessionName).isPublished());
        feedbackPage.verifyHtmlAjax("/instructorFeedbackUnpublishSuccessful.html");
        
        ______TS("PUBLISHED: unpublish link hidden");
        
        feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
        
    }
    
    public void testJScripts() throws ParseException{
        feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
        testSessionViewableTable();
        
        // TODO: check this method. it gives random failure!!!
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
    
    public void testDatePickerScripts() throws ParseException {
        
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
        

        ______TS("increasing start date affects end date value");
        cal.add(Calendar.DATE, 30);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE));
        
        ______TS("decreasing start date affects  visible time, end date range and publish date range");
        
        cal.add(Calendar.DATE, -35);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE));
        
        
        
        ______TS("decreasing end date affects start time, visible time");
        cal.add(Calendar.DATE, -50);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE));
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getValueOfDate(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE));
        
        
        
        ______TS("changing visible date affects publish date range");
        cal.add(Calendar.DATE, -10);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMinDateOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE));
        
        
        
        ______TS("changing publish date affects visible date range publishTime < startTime");
        
        cal.add(Calendar.DATE, 9);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, cal);
        
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        
        ______TS("changing publish date does not affect visible date range publishTime > startTime");
        
        cal.add(Calendar.DATE, 30);
        feedbackPage.fillTimeValueForDatePickerTest(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, cal);
        
        //set the value back to start time
        cal.add(Calendar.DATE, -29);
        //check if maxDate is start time and not publish time
        assertEquals(sdf.format(cal.getTime()), feedbackPage.getMaxDateOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));
        
        
    }
    
    public void testResponseRateLink(){
        ______TS("test response rate link clickable");
        
        feedbackPage.clickViewResponseLink("CFeedbackUiT.CS2104", "Private Session");
        feedbackPage.verifyResponseValue("0 / 0", "CFeedbackUiT.CS2104","Private Session");
        
        ______TS("test response rate");
        //Already displayed
        assertEquals("0 / 2", feedbackPage.getResponseValue("CFeedbackUiT.CS1101","First Eval"));
        
        // Failure case tested in HomePageUiTest
        
    }

    public void testViewResultsLink() {
        InstructorFeedbackResultsPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;
        
        ______TS("view results clickable not creator, open session");
        
        fsa = testData.feedbackSessions.get("openSession");
        assertNull(feedbackPage.getViewResultsLink(fsa.courseId, fsa.feedbackSessionName).getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadViewResultsLink(fsa.courseId, fsa.feedbackSessionName);
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.courseId, fsa.feedbackSessionName));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        ______TS("view results clickable creator, closed session");
        
        fsa = testData.feedbackSessions.get("manualSession");
        assertNull(feedbackPage.getViewResultsLink(fsa.courseId, fsa.feedbackSessionName).getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadViewResultsLink(fsa.courseId, fsa.feedbackSessionName);
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.courseId, fsa.feedbackSessionName));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }
    
    public void testEditLink() {
        InstructorFeedbackEditPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;
        
        ______TS("edit link clickable when creator");
        
        fsa = testData.feedbackSessions.get("privateSession");
        assertNull(feedbackPage.getEditLink(fsa.courseId, fsa.feedbackSessionName).getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadEditLink(fsa.courseId, fsa.feedbackSessionName);
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.courseId, fsa.feedbackSessionName));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }
    
    public void testSubmitLink () {
        
        FeedbackSubmitPage feedbackResultsPage;
        FeedbackSessionAttributes fsa;
        
        ______TS("submit link clickable when visible");
        
        fsa = testData.feedbackSessions.get("awaitingSession");
        assertNull(feedbackPage.getSubmitLink(fsa.courseId, fsa.feedbackSessionName).getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadSubmitLink(fsa.courseId, fsa.feedbackSessionName);
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.courseId, fsa.feedbackSessionName));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
        
        ______TS("submit link clickable when private (never visible)");
        
        fsa = testData.feedbackSessions.get("privateSession");
        assertNull(feedbackPage.getSubmitLink(fsa.courseId, fsa.feedbackSessionName).getAttribute("onclick"));
        
        feedbackResultsPage = feedbackPage.loadSubmitLink(fsa.courseId, fsa.feedbackSessionName);
        assertTrue(feedbackResultsPage.isCorrectPage(fsa.courseId, fsa.feedbackSessionName));
        feedbackPage = getFeedbackPageForInstructor(idOfInstructorWithSessions);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private InstructorFeedbacksPage getFeedbackPageForInstructor(String instructorId) {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE).withUserId(instructorId);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbacksPage.class);
    }

}