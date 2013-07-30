package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;

public class FeedbackSessionsLogicTest extends BaseComponentTestCase {
	
	private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
	private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private DataBundle dataBundle = getTypicalDataBundle();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackSessionsLogic.class);
	}
	
	
	@Test
	public void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {		
		______TS("Standard success case");
		
		FeedbackSessionAttributes fs = getNewFeedbackSession();
		fsLogic.createFeedbackSession(fs);
		LogicTest.verifyPresentInDatastore(fs);
		
		fsLogic.deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
		LogicTest.verifyAbsentInDatastore(fs);
	}
	
	public void testGetFeedbackSessionDetailsForInstructor() throws Exception {
		______TS("Standard success case");
		
		// TODO: need Responses to test properly.
		
	}
	
	@Test
	public void testGetFeedbackSessionsForCourse() throws Exception {
		
		restoreTypicalDataInDatastore();
		
		List<FeedbackSessionAttributes> actualSessions = null;
		
		______TS("Student viewing: 2 visible, 1 awaiting, 1 no questions");
		
		// 2 valid sessions in course 1, 0 in course 2.
		
		actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "student1InCourse1@gmail.com");
		
		// Student can see sessions 1 and 2. Session 3 has no questions. Session 4 is not yet visible for students.
		String expected =
				dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
				dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL;
				
		for (FeedbackSessionAttributes session : actualSessions) {
			AssertHelper.assertContains(session.toString(), expected);
		}
		assertTrue(actualSessions.size() == 2);
		
		// Course 2 only has an instructor session and a private session.
		actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "student1InCourse2@gmail.com");		
		assertTrue(actualSessions.isEmpty());
				
		______TS("Instructor viewing");
		
		// 3 valid sessions in course 1, 1 in course 2.
		
		actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "instructor1@course1.com");
		
		// Instructors should be able to see all sessions for the course
		expected =
				dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
				dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
				dataBundle.feedbackSessions.get("empty.session").toString() + Const.EOL + 
				dataBundle.feedbackSessions.get("awaiting.session").toString() + Const.EOL;
		
		for (FeedbackSessionAttributes session : actualSessions) {
			AssertHelper.assertContains(session.toString(), expected);
		}
		assertTrue(actualSessions.size() == 4);
		
		// We should only have one session here as session 2 is private and this instructor is not the creator.
		actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor2@course2.com");
		
		assertEquals(actualSessions.get(0).toString(),
				dataBundle.feedbackSessions.get("session2InCourse2").toString());
		assertTrue(actualSessions.size() == 1);

		
		______TS("Private session viewing");
		
		// This is the creator for the private session.
		// We have already tested above that other instructors cannot see it.
		actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor1@course2.com");
		AssertHelper.assertContains(dataBundle.feedbackSessions.get("session1InCourse2").toString(),
				actualSessions.toString());

	}
	
	@Test
	public void testGetFeedbackSessionBundleForUser() throws Exception {
		
		______TS("Student submit feedback test");

		restoreTypicalDataInDatastore();
		
		FeedbackSessionQuestionsBundle actual =
				fsLogic.getFeedbackSessionQuestionsForUser("First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.com");
		
		// There should be 2 question for students to do in session 1.
		// The final question is set for SELF (creator) only.
		assertTrue(actual.questionResponseBundle.size() == 2);
		
		String expected =
				dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").toString() + Const.EOL +
				dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").toString();
		
		assertEquals(actual.feedbackSession.toString(), 
				dataBundle.feedbackSessions.get("session1InCourse1").toString());
		
		for (FeedbackQuestionAttributes key : actual.questionResponseBundle.keySet()) {
		    AssertHelper.assertContains(key.toString(), expected);
		}
		
		// TODO: test responses (valueSet)
	}
	
	@Test
	public void testGetFeedbackSessionResultsForUser() throws Exception {
		
		// This file contains a session with a private session + a standard
		// session which needs to have enough qn/response combinations to cover as much
		// of the SUT as possible
		DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
		new BackDoorLogic().persistDataBundle(responseBundle);
		
		______TS("standard session with varied visibilities");		
		
		FeedbackSessionAttributes session =
				responseBundle.feedbackSessions.get("standard.session");
		
		/*** Test result bundle for student1 ***/
		StudentAttributes student = 
				responseBundle.students.get("student1InCourse1");		
		FeedbackSessionResultsBundle results =
				fsLogic.getFeedbackSessionResultsForUser(session.feedbackSessionName, 
						session.courseId, student.email);
	
		// We just check for correct session once
		assertEquals(results.feedbackSession.toString(), 
				session.toString());	
		
		// Student can see responses: q1r1, q2r3, q3r1, qr4r2-3, q5r1, q6r1, q7r1-2
		// We don't check the actual IDs as this is also implicitly tested
		// later when checking the visibility table.
		assertEquals(results.responses.size(), 9);
		assertEquals(results.questions.size(), 7);
		
		// Test the user email-name maps used for display purposes
		String mapString = results.emailNameTable.toString();
		List<String> expectedStrings = new ArrayList<String>();
		Collections.addAll(expectedStrings,
				"FSRTest.student1InCourse1@gmail.com=student1 In Course1",
				"FSRTest.student2InCourse1@gmail.com=student2 In Course1",
				"FSRTest.student4InCourse1@gmail.com=student4 In Course1",
				"Team 1.3=Team 1.3",
				"Team 1.4=Team 1.4",
				"FSRTest.instr1@course1.com=Instructor1 Course1",
				"FSRTest.student1InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
				"FSRTest.student2InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.emailNameTable.size(), 8);
		
		// Test the generated response visibilityTable for userNames.		
		mapString = tableToString(results.visibilityTable);
		expectedStrings.clear();
		Collections.addAll(expectedStrings,
				getResponseId("qn1.resp1",responseBundle)+"={true,true}",
				getResponseId("qn2.resp3",responseBundle)+"={true,true}",
				getResponseId("qn3.resp1",responseBundle)+"={false,false}",
				getResponseId("qn4.resp2",responseBundle)+"={false,true}",
				getResponseId("qn4.resp3",responseBundle)+"={false,true}",
				getResponseId("qn5.resp1",responseBundle)+"={true,false}",
				getResponseId("qn6.resp1",responseBundle)+"={false,false}",
				getResponseId("qn7.resp1",responseBundle)+"={true,true}",
				getResponseId("qn7.resp2",responseBundle)+"={true,true}");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.visibilityTable.size(), 9);
		
		
		/*** Test result bundle for instructor1 ***/
		InstructorAttributes instructor =
				responseBundle.instructors.get("instructor1OfCourse1");		
		results = fsLogic.getFeedbackSessionResultsForUser(
				session.feedbackSessionName, 
				session.courseId, instructor.email);
		
		// Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
		assertEquals(results.responses.size(), 10);
		assertEquals(results.questions.size(), 5);
		
		// Test the user email-name maps used for display purposes
		mapString = results.emailNameTable.toString();
		expectedStrings.clear();
		Collections.addAll(expectedStrings,
				"FSRTest.student1InCourse1@gmail.com=student1 In Course1",
				"FSRTest.student2InCourse1@gmail.com=student2 In Course1",
				"FSRTest.student3InCourse1@gmail.com=student3 In Course1",
				"FSRTest.student4InCourse1@gmail.com=student4 In Course1",
				"FSRTest.student5InCourse1@gmail.com=student5 In Course1",
				"FSRTest.student6InCourse1@gmail.com=student6 In Course1",
				"Team 1.2=Team 1.2",
				"Team 1.3=Team 1.3",
				"Team 1.4=Team 1.4",
				"FSRTest.instr1@course1.com=Instructor1 Course1",
				"FSRTest.instr2@course1.com=Instructor2 Course1");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.emailNameTable.size(), 11);
		
		// Test the generated response visibilityTable for userNames.		
		mapString = tableToString(results.visibilityTable);
		expectedStrings.clear();
		Collections.addAll(expectedStrings,
				getResponseId("qn2.resp1",responseBundle)+"={false,false}",
				getResponseId("qn2.resp2",responseBundle)+"={false,false}",
				getResponseId("qn2.resp3",responseBundle)+"={false,false}",
				getResponseId("qn3.resp1",responseBundle)+"={true,false}",
				getResponseId("qn3.resp2",responseBundle)+"={false,false}",
				getResponseId("qn4.resp1",responseBundle)+"={true,true}",
				getResponseId("qn4.resp2",responseBundle)+"={true,true}",
				getResponseId("qn4.resp3",responseBundle)+"={true,true}",
				getResponseId("qn5.resp1",responseBundle)+"={false,true}",
				getResponseId("qn6.resp1",responseBundle)+"={false,false}");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.visibilityTable.size(), 10);
		
		// TODO: test student2 too.
		
		______TS("private session");

		session = responseBundle.feedbackSessions.get("private.session");
		
		/*** Test result bundle for student1 ***/
		student =  responseBundle.students.get("student1InCourse1");		
		results = fsLogic.getFeedbackSessionResultsForUser(session.feedbackSessionName, 
						session.courseId, student.email);
		
		assertEquals(results.questions.size(),0);
		assertEquals(results.responses.size(),0);
		assertEquals(results.emailNameTable.size(),0);
		assertEquals(results.visibilityTable.size(),0);
		
		/*** Test result bundle for instructor1 ***/
		
		instructor =
				responseBundle.instructors.get("instructor1OfCourse1");		
		results = fsLogic.getFeedbackSessionResultsForUser(
				session.feedbackSessionName, 
				session.courseId, instructor.email);
		
		// Can see all responses regardless of visibility settings.
		assertEquals(results.questions.size(),2);
		assertEquals(results.responses.size(),2);
		
		// Test the user email-name maps used for display purposes
		mapString = results.emailNameTable.toString();
		expectedStrings.clear();
		Collections.addAll(expectedStrings,
				"FSRTest.student1InCourse1@gmail.com=student1 In Course1",
				"FSRTest.instr1@course1.com=Instructor1 Course1",
				"Team 1.2=Team 1.2");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.emailNameTable.size(), 3);
		
		// Test that name visibility is adhered to even when
		// it is a private session. (to protect anonymity during session type conversion)"
		mapString = tableToString(results.visibilityTable);
		expectedStrings.clear();
		Collections.addAll(expectedStrings,
				getResponseId("p.qn1.resp1",responseBundle)+"={false,false}",
				getResponseId("p.qn2.resp1",responseBundle)+"={true,false}");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.visibilityTable.size(), 2);
		
		______TS("failure: no session");
				
		try {
			fsLogic.getFeedbackSessionResultsForUser("invalid session", 
				session.courseId, instructor.email);
			signalFailureToDetectException("Did not detect that session does not exist.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(), "Trying to view non-existent feedback session.");
		}
	}

	public void testUpdateFeedbackSession() {
		
	}
	
	@Test
	public void testPublishUnpublishFeedbackSession() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("failure: not manual type");
		
		FeedbackSessionAttributes
			sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse1");
		
		try{
			fsLogic.publishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session should " +
					"be published automatically.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Session should be published automatically.");
		}
		
		try{
			fsLogic.unpublishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session should " +
					"be published automatically.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Session should be not be unpublished in this manner.");
		}
		
		______TS("success: publish");
		
		// set as manual publish
		sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
		fsLogic.updateFeedbackSession(sessionUnderTest);
		
		fsLogic.publishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
		
		sessionUnderTest.sentPublishedEmail = true;
		sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;
		
		assertEquals(
				fsLogic.getFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId).toString(),
				sessionUnderTest.toString());
		
		
		______TS("failure: already published");
		
		try{
			fsLogic.publishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session is already published.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Session is already published.");
		}
		
		______TS("success: publish");
		
		fsLogic.unpublishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
		
		sessionUnderTest.sentPublishedEmail = false;
		sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
		
		assertEquals(
				fsLogic.getFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId).toString(),
				sessionUnderTest.toString());
		
		______TS("failure: not published");
		
		try{
			fsLogic.unpublishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session is not published.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Session is not currently published.");
		}
		
		______TS("failure: private session");
		
		sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse2");

		try{
			fsLogic.publishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session should " +
					"be published automatically.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Session should be published automatically.");
		}
		
		try{
			fsLogic.unpublishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session should " +
					"be published automatically.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Session should be not be unpublished in this manner.");
		}
				
		______TS("failure: session does not exist");

		sessionUnderTest.feedbackSessionName = "non-existant session";
		
		try{
			fsLogic.publishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session does not exist.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(),
					"Trying to publish a non-existant session.");
		}
		
		try{
			fsLogic.unpublishFeedbackSession(
					sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that session does not exist.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(),
					"Trying to publish a non-existant session.");
		}
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

	// Extract response id from datastore based on json key.
	private String getResponseId(String jsonId, DataBundle bundle) {
		FeedbackResponseAttributes response = bundle.feedbackResponses.get(jsonId);
		String responseId = frLogic.getFeedbackResponse(response.feedbackQuestionId, 
				response.giverEmail, response.recipient).getId();		
		return responseId;
	}

	// Stringifies the visibility table for easy testing/comparison.
	private String tableToString(Map<String, boolean[]> table){
		String tableString = "";
		for(Map.Entry<String, boolean[]> entry : table.entrySet()) {
			tableString += "{";
			tableString += entry.getKey().toString();
			tableString += "={";
			tableString += String.valueOf(entry.getValue()[0]);
			tableString += ",";
			tableString += String.valueOf(entry.getValue()[1]);
			tableString += "}},";
		}
		if(!tableString.isEmpty()) {
			tableString = tableString.substring(0, tableString.length()-1);
		}
		return tableString;
	}
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(FeedbackSessionsLogic.class);
	}
}
