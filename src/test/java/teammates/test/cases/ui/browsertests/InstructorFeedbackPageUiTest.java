package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbacksPage;

/**
 * Covers the 'Feedback Session' page for instructors. 
 * SUT is {@link InstructorFeedbacksPage}.
 */
public class InstructorFeedbackPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorFeedbacksPage feedbackPage;
	private static DataBundle testData;
	/** This contains data for the new feedback session to be created during testing */
	private static FeedbackSessionAttributes newSession;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorFeedbackPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		newSession = new FeedbackSessionAttributes();
		newSession.courseId = "CFeedbackUiT.CS1101";
		newSession.feedbackSessionName = "New Session";
		newSession.startTime = TimeHelper.convertToDate("2014-04-01 11:59 PM UTC");
		newSession.endTime = TimeHelper.convertToDate("2014-04-30 10:00 PM UTC");
		newSession.creatorEmail = "teammates.test1@gmail.com";
		newSession.createdTime = TimeHelper.convertToDate("2014-04-01 11:59 PM UTC");
		newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
		newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
		newSession.gracePeriod = 10;
		newSession.instructions = new Text("Please fill in the new feedback session.");
		newSession.sentOpenEmail = false;
		newSession.sentPublishedEmail = false;
		newSession.timeZone = 8;
		newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
			
		browser = BrowserPool.getBrowser();
		
	}
	
	@Test
	public void allTests() throws Exception{
		testContent();
		
		testAddAction();
		testDeleteAction();
		testPublishAction();
		testUnpublishAction();
		
		//testing response rate links due to page source problems encountered after testContent()
		testViewResultsLink();
		testEditLink();
		testSubmitLink();

	}

	public void testContent() throws Exception{
		
		______TS("no courses");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
		feedbackPage.verifyHtml("/instructorFeedbackEmptyAll.html");
		
		______TS("no sessions");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutSessions").googleId);
		feedbackPage.verifyHtml("/instructorFeedbackEmptySession.html");

		______TS("sort by name");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);

		feedbackPage.sortByName()
			.verifyTablePattern(1,"{*}First Eval{*}First Session{*}Manual Session{*}Private Session");
		feedbackPage.sortByName()
			.verifyTablePattern(1,"{*}Private Session{*}Manual Session{*}First Session{*}First Eval");
		
		______TS("sort by course id");
		
		feedbackPage.sortById()
			.verifyTablePattern(0,"{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104");		
		feedbackPage.sortById()
			.verifyTablePattern(0,"{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101");
	
	}

	//TODO: implement these
	public void testViewResultsLink(){
		______TS("link always clickable");		
	}
	
	public void testEditLink(){
		______TS("creator: clickable");
		
		______TS("other instructor in course: not clickable");
	}
	
	public void testSubmitLink(){
		______TS("link always clickable");
	}

	public void testAddAction() throws Exception{
		
		______TS("success case 1: defaults: visible when open, manual publish");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);

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
		feedbackPage.verifyHtml("/instructorFeedbackAddSuccess.html");


		______TS("failure case 1: session exists already");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				newSession.startTime, newSession.endTime,
				null, null,
				newSession.instructions,
				newSession.gracePeriod );
		assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, feedbackPage.getStatus());
		
		______TS("success case 2: private session");

		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		feedbackPage.clickNeverVisibleTimeButton();
		
		newSession.feedbackSessionName = "Private Session";
		newSession.endTime = null;
		newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
		newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;		
		newSession.feedbackSessionType = FeedbackSessionType.PRIVATE; 
		// defaults are saved
		newSession.instructions = new Text("Please answer all the given questions.");
		newSession.gracePeriod = 15;
		
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				null, null,
				null, null,
				null,
				-1 );
		
		savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
		newSession.startTime = savedSession.startTime;
		
		assertEquals(newSession.toString(), savedSession.toString());
		
		______TS("success case 3: custom session visible time, publish follows visible");

		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		feedbackPage.clickCustomVisibleTimeButton();
		feedbackPage.clickDefaultPublishTimeButton();
		newSession.feedbackSessionName = "Allow Early Viewing Session";
		// start time = end time, in future.
		newSession.startTime = TimeHelper.convertToDate("2014-05-01 8:00 AM UTC");
		newSession.endTime = newSession.startTime;
		newSession.sessionVisibleFromTime = TimeHelper.convertToDate("2014-03-01 5:00 PM UTC");
		newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
		newSession.feedbackSessionType = FeedbackSessionType.STANDARD; 
		newSession.gracePeriod = 0;
		
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, null,
				newSession.instructions,
				newSession.gracePeriod );
		
		savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
		assertEquals(newSession.toString(), savedSession.toString());
		
		______TS("success case 4: custom session visible time, responses always hidden");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		feedbackPage.clickCustomVisibleTimeButton();
		feedbackPage.clickNeverPublishTimeButton();
		newSession.feedbackSessionName = "responses cant be seen my students 1";
		// start time in past
		newSession.startTime = TimeHelper.convertToDate("2012-05-01 4:00 AM UTC");
		newSession.endTime = TimeHelper.convertToDate("2013-31-12 11:59 PM UTC");
		newSession.sessionVisibleFromTime = TimeHelper.convertToDate("2012-05-01 2:00 AM UTC");
		newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
		newSession.gracePeriod = 30;
		newSession.instructions = new Text("cannot \r\n see responses<script>test</script>");
		
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, null,
				newSession.instructions,
				newSession.gracePeriod );
		
		savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
		assertEquals(newSession.toString(), savedSession.toString());
		
		______TS("success case 5: visible when open, custom publish time");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		feedbackPage.clickDefaultVisibleTimeButton();
		feedbackPage.clickCustomPublishTimeButton();
		newSession.feedbackSessionName = "boundary number of characters123456789";
		newSession.startTime = TimeHelper.convertToDate("2012-05-01 8:00 AM UTC");
		newSession.endTime = TimeHelper.convertToDate("2012-09-01 11:00 PM UTC");
		newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
		newSession.resultsVisibleFromTime = TimeHelper.convertToDate("2014-02-02 1:00 AM UTC");
		newSession.gracePeriod = 5;
		newSession.instructions = new Text("");
		
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				newSession.startTime, newSession.endTime,
				null, newSession.resultsVisibleFromTime,
				newSession.instructions,
				newSession.gracePeriod );
		
		savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
		assertEquals(newSession.toString(), savedSession.toString());
		
		______TS("failure case 2: invalid input: publish time before visible (visible follows open)");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		feedbackPage.clickDefaultVisibleTimeButton();
		feedbackPage.clickCustomPublishTimeButton();
		newSession.feedbackSessionName = "invalid publish time";
		newSession.startTime = TimeHelper.convertToDate("2012-05-01 3:00 AM UTC");
		newSession.endTime = TimeHelper.convertToDate("2012-09-01 4:00 PM UTC");
		newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
		newSession.resultsVisibleFromTime = TimeHelper.convertToDate("2011-02-02 1:00 AM UTC");
		newSession.gracePeriod = 30;
		newSession.instructions = new Text("");
		
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				newSession.startTime, newSession.endTime,
				null, newSession.resultsVisibleFromTime,
				newSession.instructions,
				newSession.gracePeriod );
		
		assertEquals(String.format(
				FieldValidator.TIME_FRAME_ERROR_MESSAGE,
				FieldValidator.RESULTS_VISIBLE_TIME_FIELD_NAME,
				FieldValidator.FEEDBACK_SESSION_NAME,
				FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME), feedbackPage.getStatus());

		
		______TS("failure case 3: invalid input (session name)");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		newSession.feedbackSessionName = "bad name!";
		newSession.endTime = TimeHelper.convertToDate("2014-09-01 0:00 AM UTC");
		feedbackPage.addFeedbackSession(
				newSession.feedbackSessionName, newSession.courseId,
				newSession.startTime, newSession.endTime,
				null, null,
				newSession.instructions,
				newSession.gracePeriod );
		assertEquals(String.format(
				FieldValidator.ALPHANUMERIC_STRING_ERROR_MESSAGE,
				"bad name!",
				FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
				FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME), feedbackPage.getStatus());

	}

	public void testDeleteAction() throws Exception{
		
		String courseId = newSession.courseId;
	    String sessionName = "New Session";
		
	    // refresh page
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		feedbackPage.clickAndCancel(feedbackPage.getDeleteLink(courseId, sessionName));
		assertNotNull("session should not have been deleted", BackDoor.getFeedbackSession(courseId, sessionName));
	
		feedbackPage.clickAndConfirm(feedbackPage.getDeleteLink(courseId, sessionName));
		feedbackPage.verifyHtmlAjax("/instructorFeedbackDeleteSuccessful.html");
		
	}
	
	public void testPublishAction(){		
	    // refresh page
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
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
		feedbackPage.verifyHtmlAjax("/instructorFeedbackPublishSuccessful.html");
		
		______TS("PUBLISHED: publish link hidden");
		
		feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
	}
	
	public void testUnpublishAction(){
	    // refresh page
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		______TS("PUBLISHED: unpublish link unclickable for other instructor");
		
		String courseId = testData.feedbackSessions.get("publishedSession").courseId;
		String sessionName = testData.feedbackSessions.get("publishedSession").feedbackSessionName;
		
		feedbackPage.verifyUnclickable(feedbackPage.getUnpublishLink(courseId, sessionName));
		feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
		
		______TS("PRIVATE: unpublish link unclickable");
		
		courseId = testData.feedbackSessions.get("privateSession").courseId;
		sessionName = testData.feedbackSessions.get("privateSession").feedbackSessionName;

		feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
		feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
		
		______TS("MANUAL: unpublish link clickable");
		
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

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

	private InstructorFeedbacksPage getFeedbackPageForInstructor(String instructorId) {
		Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE).withUserId(instructorId);
		return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbacksPage.class);
	}

}