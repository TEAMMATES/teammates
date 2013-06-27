package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.text.ParseException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackPage;

/**
 * Covers the 'Feedback Session' page for instructors. 
 * SUT is {@link InstructorFeedbackPage}.
 */
public class InstructorFeedbackPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorFeedbackPage feedbackPage;
	private static DataBundle testData;
	/** This contains data for the new feedback session to be created during testing */
	private static FeedbackSessionAttributes newSession;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadTestData("/InstructorFeedbackPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		newSession = new FeedbackSessionAttributes();
		newSession.courseId = "CFeedbackUiT.CS1101";
		newSession.feedbackSessionName = "New Session";
		newSession.startTime = Common.convertToDate("2014-04-01 11:59 PM");
		newSession.endTime = Common.convertToDate("2014-04-30 11:59 PM");
		newSession.creatorEmail = "teammates.test3@gmail.com";
		newSession.createdTime = Common.convertToDate("2014-04-01 11:59 PM");
		newSession.sessionVisibleFromTime = Common.TIME_REPRESENTS_FOLLOW_OPENING;
		newSession.resultsVisibleFromTime = Common.TIME_REPRESENTS_FOLLOW_VISIBLE;
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
		
		testEditLink();
		testViewResultsLink();
		
		testInputValidation();
		
		testAddAction();
		testDeleteAction();
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
			.verifyTablePattern(1,"{*}First Session{*}Second Session{*}Third Session");
		feedbackPage.verifyHtml("/instructorFeedbackByName.html");
		
		feedbackPage.sortByName()
			.verifyTablePattern( 1,"{*}Third Session{*}Second Session{*}First Session");
		
		______TS("sort by course id");
		
		feedbackPage.sortById()
		.verifyTablePattern(0,"{*}CFeedbackUiT.CS1101{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104");
		feedbackPage.verifyHtml("/instructorFeedbackById.html");
		
		feedbackPage.sortById()
			.verifyTablePattern(0,"{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS2104{*}CFeedbackUiT.CS1101");
	
	}

	public void testEditLink(){
		//TODO: implement this (also check for disabling of the link at right times)
	}

	public void testViewResultsLink(){
		//TODO: implement this (also check for disabling of the link at right times)
	}

	public void testInputValidation() throws ParseException {
		
		______TS("client-side input validation");
		
		//TODO: The client-side validation tests below should be covered in JS tests, not as UI tests.
		// They are to be removed after confirming coverage by JS tests.
		
		// Empty closing date
		feedbackPage.fillSessionName("Some value");
		feedbackPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, feedbackPage.getStatus());
		
		// Empty name
		feedbackPage.addFeedbackSession("", newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, feedbackPage.getStatus());
		
		// Empty custom publishTime
		feedbackPage.clickCustomPublishTimeButton();
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, feedbackPage.getStatus());

		// Empty custom visibleTime
		feedbackPage.clickCustomVisibleTimeButton();
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, feedbackPage.getStatus());
		
		// Invalid name
		feedbackPage.clickDefaultVisibleTimeButton();
		feedbackPage.clickDefaultPublishTimeButton();
		feedbackPage.addFeedbackSession("!", newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FEEDBACK_SESSION_NAME_INVALID, feedbackPage.getStatus());
		
	}

	public void testAddAction() throws Exception{
		
		______TS("typical success case");
		
		feedbackPage.addFeedbackSession(newSession.feedbackSessionName, newSession.courseId, newSession.startTime, newSession.endTime,
				newSession.sessionVisibleFromTime, newSession.resultsVisibleFromTime, newSession.instructions,
				newSession.gracePeriod );
		feedbackPage.verifyStatus(Common.MESSAGE_FEEDBACK_SESSION_ADDED);
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
		assertEquals(Common.MESSAGE_FEEDBACK_SESSION_EXISTS, feedbackPage.getStatus());
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

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

	private InstructorFeedbackPage getFeedbackPageForInstructor(String instructorId) {
		Url feedbackPageLink = new Url(Common.PAGE_INSTRUCTOR_FEEDBACK).withUserId(instructorId);
		return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackPage.class);
	}

}