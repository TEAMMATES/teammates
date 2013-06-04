package teammates.test.cases.logic;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.FeedbackSessionsLogic;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;
import teammates.test.cases.BaseTestCase;

public class FeedbackSessionsLogicTest extends BaseTestCase {
	
	private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackSessionsLogic.class);
		Datastore.initialize();
	}
	
	@BeforeMethod
	public void caseSetUp() throws ServletException, IOException {
		helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig());
		setHelperTimeZone(helper);
		helper.setUp();
	}
	
	@Test
	public void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {		
		______TS("Standard success case");
		
		FeedbackSessionAttributes fs = getNewFeedbackSession();
		fsLogic.createFeedbackSession(fs);
		LogicTest.verifyPresentInDatastore(fs);
		
		fsLogic.deleteFeedbackSession(fs.feedbackSessionName, fs.courseId);
		LogicTest.verifyAbsentInDatastore(fs);
	}
	
	public void testGetFeedbackSessionDetailsForInstructor() throws Exception {
		______TS("Standard success case");
		
		// TODO: need Responses to test properly.
		
	}
	
	@Test
	public void testGetFeedbackSessionsForCourse() throws Exception {
		
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		
		List<FeedbackSessionAttributes> actualSessions = null;
		
		______TS("Student viewing");
		
		// 2 valid sessions in course 1, 0 in course 2.
		
		actualSessions = fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse1", "student1InCourse1@gmail.com");
		
		// Student can see sessions 1 and 2. Session 3 has no questions.
		String expected =
				dataBundle.feedbackSessions.get("session1InCourse1").toString() + Common.EOL +
				dataBundle.feedbackSessions.get("session2InCourse1").toString() + Common.EOL;
				
		for (FeedbackSessionAttributes session : actualSessions) {
			assertContains(session.toString(), expected);
		}
		assertTrue(actualSessions.size() == 2);
		
		actualSessions = fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse2", "student1InCourse2@gmail.com");
		
		assertTrue(actualSessions.isEmpty());
		
		______TS("Instructor viewing");
		
		// 3 valid sessions in course 1, 1 in course 2.
		
		actualSessions = fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse1", "instructor1@course1.com");
		
		// Instructors should be able to see all sessions for the course
		expected =
				dataBundle.feedbackSessions.get("session1InCourse1").toString() + Common.EOL +
				dataBundle.feedbackSessions.get("session2InCourse1").toString() + Common.EOL +
				dataBundle.feedbackSessions.get("session3InCourse1").toString() + Common.EOL;
		
		for (FeedbackSessionAttributes session : actualSessions) {
			assertContains(session.toString(), expected);
		}
		assertTrue(actualSessions.size() == 3);
		
		// We should only have one session here as session 2 is private and this instructor is not the creator.
		actualSessions = fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse2", "instructor2@course2.com");
		
		assertEquals(actualSessions.get(0).toString(),
				dataBundle.feedbackSessions.get("session2InCourse2").toString());
		assertTrue(actualSessions.size() == 1);

		
		______TS("Private session viewing");
		
		// This is the creator for the private session.
		// We have already tested above that other instructors cannot see it.
		actualSessions = fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse2", "instructor1@course2.com");
		assertContains(dataBundle.feedbackSessions.get("session1InCourse2").toString(),
				actualSessions.toString());

	}
	
	@Test
	public void testGetFeedbackSessionBundleForUser() throws Exception {
		
		______TS("Student submit feedback test");

		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		
		FeedbackSessionQuestionsBundle actual =
				fsLogic.getFeedbackSessionBundleForUser("First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.com");
		
		// There should be 2 question for students to do in session 1.
		// The final question is set for SELF (creator) only.
		assertTrue(actual.questions.size() == 2);
		
		String expected =
				dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").toString() + Common.EOL +
				dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").toString();
		
		assertEquals(actual.feedbackSession.toString(), 
				dataBundle.feedbackSessions.get("session1InCourse1").toString());
		assertContains(actual.questions.get(0).toString(), expected);
		assertContains(actual.questions.get(1).toString(), expected);
		
	}
	
	public void testGetFeedbackSessionResultsForUser() {
		
	}
	
	public void testUpdateFeedbackSession() {
		
	}
	
	private FeedbackSessionAttributes getNewFeedbackSession() {
		FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
		fsa.feedbackSessionType = FeedbackSessionType.STANDARD;
		fsa.feedbackSessionName = "fsTest1";
		fsa.courseId = "testCourse";
		fsa.creatorEmail = "valid@email.com";
		fsa.createdTime = new Date();
		fsa.startTime = new Date();
		fsa.endTime = new Date();
		fsa.sessionVisibleFromTime = new Date();
		fsa.resultsVisibleFromTime = new Date();
		fsa.gracePeriod = 5;
		fsa.sentOpenEmail = true;
		fsa.instructions = new Text("Give feedback.");
		return fsa;
	}
	
	@AfterMethod
	public void caseTearDown() {
		helper.tearDown();
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(FeedbackSessionsLogic.class);
	}
}
