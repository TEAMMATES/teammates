package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.text.ParseException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Const;
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
		newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
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
		
		testViewResultsLink();
		testEditLink();
		testSubmitLink();		
		testInputValidation();
		
		testAddAction();
		testDeleteAction();
		testPublishAction();
		testUnpublishAction();

	}

	public void testContent() throws Exception{
		
		______TS("no courses");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
		feedbackPage.verifyHtml("/instructorFeedbackEmptyAll.html");
		
		______TS("no sessions");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithoutSessions").googleId);
		feedbackPage.verifyHtml("/instructorFeedbackEmptySession.html");

		______TS("typical view, sort by deadline (default)");
		
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		feedbackPage.verifyHtml("/instructorFeedbackByDeadline.html");

		______TS("sort by name");
		
		feedbackPage.sortByName()
			.verifyTablePattern(1,"{*}First Eval{*}First Session{*}Manual Session{*}Private Session");
		feedbackPage.verifyHtml("/instructorFeedbackByName.html");
		
		feedbackPage.sortByName()
			.verifyTablePattern(1,"{*}Private Session{*}Manual Session{*}First Session{*}First Eval");
		
		______TS("sort by course id");
		
		feedbackPage.sortById()
		.verifyTablePattern(0,"{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104");
		feedbackPage.verifyHtml("/instructorFeedbackById.html");
		
		feedbackPage.sortById()
			.verifyTablePattern(0,"{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS1101");
	
	}

	//TODO: implement these (also check for disabling of the link at right times)
	public void testViewResultsLink(){
		
	}
	
	public void testEditLink(){
		
	}
	
	public void testSubmitLink(){
		
	}

	public void testInputValidation() throws ParseException {
		
		______TS("client-side input validation");
		
		//TODO: The client-side validation tests below should be covered in JS tests, not as UI tests.
		// They are to be removed after confirming coverage by JS tests.
		
		// Empty closing date
		feedbackPage.fillSessionName("Some value");
		feedbackPage.clickSubmitButton();
		assertEquals(Const.StatusMessages.FIELDS_EMPTY, feedbackPage.getStatus());
		
		// Empty name
		feedbackPage.addFeedbackSession("", newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Const.StatusMessages.FIELDS_EMPTY, feedbackPage.getStatus());
		
		// Empty custom publishTime
		feedbackPage.clickCustomPublishTimeButton();
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Const.StatusMessages.FIELDS_EMPTY, feedbackPage.getStatus());

		// Empty custom visibleTime
		feedbackPage.clickCustomVisibleTimeButton();
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Const.StatusMessages.FIELDS_EMPTY, feedbackPage.getStatus());
		
		// Invalid name
		feedbackPage.clickDefaultVisibleTimeButton();
		feedbackPage.clickDefaultPublishTimeButton();
		feedbackPage.addFeedbackSession("!", newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Const.StatusMessages.FEEDBACK_SESSION_NAME_INVALID, feedbackPage.getStatus());
		
	}

	public void testAddAction() throws Exception{
		
		______TS("typical success case");
		
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
		FeedbackSessionAttributes savedSession = BackDoor.getFeedbackSession(newSession.courseId, newSession.feedbackSessionName);
		//Note: This can fail at times because Firefox fails to choose the correct value from the dropdown.
		//  in that case, rerun in Chrome.
		assertEquals(newSession.toString(), savedSession.toString());
		feedbackPage.verifyHtml("/instructorFeedbackAddSuccess.html");

		______TS("duplicate session name");
		
	    // go back as page has redirected
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, feedbackPage.getStatus());
	}

	public void testDeleteAction() throws Exception{
		
		String courseId = newSession.courseId;
	    String sessionName = newSession.feedbackSessionName;
		
	    // refresh page
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		feedbackPage.clickAndCancel(feedbackPage.getDeleteLink(courseId, sessionName));
		assertNotNull(null, BackDoor.getFeedbackSession(courseId, sessionName));
	
		feedbackPage.clickAndConfirm(feedbackPage.getDeleteLink(courseId, sessionName));
		feedbackPage.verifyHtml("/instructorFeedbackDeleteSuccessful.html");
		
	}
	
	public void testPublishAction(){		
	    // refresh page
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		______TS("AUTOMATIC: publish link unclickable");
		
		String courseId = testData.feedbackSessions.get("publishedSession").courseId;
		String sessionName = testData.feedbackSessions.get("publishedSession").feedbackSessionName;
		
		feedbackPage.verifyUnclickable(feedbackPage.getPublishLink(courseId, sessionName));
		feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
		
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
		feedbackPage.verifyHtml("/instructorFeedbackPublishSuccessful.html");
		
		______TS("PUBLISHED: publish link hidden");
		
		feedbackPage.verifyPublishLinkHidden(courseId, sessionName);
	}
	
	public void testUnpublishAction(){
	    // refresh page
		feedbackPage = getFeedbackPageForInstructor(testData.accounts.get("instructorWithSessions").googleId);
		
		______TS("AUTOMATIC: unpublish link hidden");
		
		String courseId = testData.feedbackSessions.get("publishedSession").courseId;
		String sessionName = testData.feedbackSessions.get("publishedSession").feedbackSessionName;
		
		feedbackPage.verifyUnclickable(feedbackPage.getPublishLink(courseId, sessionName));
		feedbackPage.verifyUnpublishLinkHidden(courseId, sessionName);
		
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
		feedbackPage.verifyHtml("/instructorFeedbackUnpublishSuccessful.html");
		
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