package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionStats;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.FeedbackSessionPublishedMailAction;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.automated.FeedbackSessionPublishedReminderTest.FeedbackSessionPublishedCallback;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class FeedbackSessionsLogicTest extends BaseComponentUsingTaskQueueTestCase {
	private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
	private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
	private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private DataBundle dataBundle = getTypicalDataBundle();
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackSessionsLogic.class);
		gaeSimulation.setupWithTaskQueueCallbackClass(PublishUnpublishSessionCallback.class);
		gaeSimulation.resetDatastore();
		
	}
	
	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
	}
	
	@SuppressWarnings("serial")
	public static class PublishUnpublishSessionCallback extends BaseTaskQueueCallback {
		
		@Override
		public int execute(URLFetchRequest request) {
			
			HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
			
			EmailAction action = new FeedbackSessionPublishedMailAction(paramMap);
			action.getPreparedEmailsAndPerformSuccessOperations();
			return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
		}
	}
	
	@Test
	public void testGetFeedbackSessionsClosingWithinTimeLimit() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("init : 0 non private sessions closing within time-limit");
		List<FeedbackSessionAttributes> sessionList = fsLogic
				.getFeedbackSessionsClosingWithinTimeLimit();
		
		assertEquals(0, sessionList.size());
		
		______TS("typical case : 1 non private session closing within time limit");
		FeedbackSessionAttributes session = getNewFeedbackSession();
		session.timeZone = 0;
		session.feedbackSessionType = FeedbackSessionType.STANDARD;
		session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		session.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		session.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		fsLogic.createFeedbackSession(session);
		
		sessionList = fsLogic
				.getFeedbackSessionsClosingWithinTimeLimit();
		
		assertEquals(1, sessionList.size());
		assertEquals(session.feedbackSessionName, 
				sessionList.get(0).feedbackSessionName);
		
		______TS("case : 1 private session closing within time limit");
		session.feedbackSessionType = FeedbackSessionType.PRIVATE;
		fsLogic.updateFeedbackSession(session);
		
		sessionList = fsLogic
				.getFeedbackSessionsClosingWithinTimeLimit();
		assertEquals(0, sessionList.size());
		
		//delete the newly added session as restoreTypicalDataInDatastore()
				//wont do it
		fsLogic.deleteFeedbackSessionCascade(session.feedbackSessionName,
				session.courseId);
	}
	
	@Test
	public void testGetFeedbackSessionsWhichNeedOpenMailsToBeSent() throws Exception {
		restoreTypicalDataInDatastore();
		______TS("init : 0 open sessions");
		List<FeedbackSessionAttributes> sessionList = fsLogic
				.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
		
		assertEquals(0, sessionList.size());
		
		______TS("case : 1 open session with mail unsent");
		FeedbackSessionAttributes session = getNewFeedbackSession();
		session.timeZone = 0;
		session.feedbackSessionType = FeedbackSessionType.STANDARD;
		session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		session.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		session.sentOpenEmail = false;
		fsLogic.createFeedbackSession(session);		
		
		sessionList = fsLogic
				.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
		assertEquals(sessionList.size(), 1);
		assertEquals(sessionList.get(0).feedbackSessionName,
				session.feedbackSessionName);
		
		______TS("typical case : 1 open session with mail sent");
		session.sentOpenEmail = true;
		fsLogic.updateFeedbackSession(session);
		
		sessionList = fsLogic
				.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
		
		assertEquals(0, sessionList.size());
		
		______TS("case : 1 closed session with mail unsent");
		session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		fsLogic.updateFeedbackSession(session);
		
		sessionList = fsLogic
				.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
		assertEquals(0, sessionList.size());
		
		//delete the newly added session as restoreTypicalDataInDatastore()
		//wont do it
		fsLogic.deleteFeedbackSessionCascade(session.feedbackSessionName,
				session.courseId);
	}
	
	@Test
	public void testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent() throws Exception {
		
		restoreTypicalDataInDatastore();
		______TS("init : no published sessions");
		unpublishAllSessions();
		List<FeedbackSessionAttributes> sessionList = fsLogic
				.getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
		
		assertEquals(sessionList.size(), 0);
		
		______TS("case : 1 published session with mail unsent");
		FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
		session.timeZone = 0;
		session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		session.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		
		session.sentPublishedEmail = false;
		fsLogic.updateFeedbackSession(session);
		
		sessionList = fsLogic
				.getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
		assertEquals(sessionList.size(), 1);
		assertEquals(sessionList.get(0).feedbackSessionName,
				session.feedbackSessionName);
		
		______TS("case : 1 published session with mail sent");
		session.sentPublishedEmail = true;
		fsLogic.updateFeedbackSession(session);		
		
		sessionList = fsLogic
				.getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
		assertEquals(sessionList.size(), 0);
	}
	
	@Test
	public void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {		
		______TS("test create");
		
		FeedbackSessionAttributes fs = getNewFeedbackSession();
		fsLogic.createFeedbackSession(fs);
		LogicTest.verifyPresentInDatastore(fs);

		______TS("test delete");
		// Create a question under the session to test for cascading during delete.
		FeedbackQuestionAttributes fq = new FeedbackQuestionAttributes();
		fq.feedbackSessionName = fs.feedbackSessionName;
		fq.courseId = fs.courseId;
		fq.questionNumber = 1;
		fq.creatorEmail = fs.creatorEmail;
		fq.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
		fq.giverType = FeedbackParticipantType.STUDENTS;
		fq.recipientType = FeedbackParticipantType.TEAMS;
		fq.questionMetaData = new Text("question to be deleted through cascade");
		fq.questionType = FeedbackQuestionType.TEXT;
		fq.showResponsesTo = new ArrayList<FeedbackParticipantType>();
		fq.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
		fq.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
		
		fqLogic.createFeedbackQuestion(fq);
		
		fsLogic.deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
		LogicTest.verifyAbsentInDatastore(fs);
		LogicTest.verifyAbsentInDatastore(fq);
	}
	
	@Test
	public void testGetFeedbackSessionDetailsForInstructor() throws Exception {
		
		// This file contains a session with a private session + a standard
		// session + a special session with all questions without recipients.
		DataBundle dataBundle = loadDataBundle("/FeedbackSessionDetailsTest.json");
		new BackDoorLogic().persistDataBundle(dataBundle);
		
		Map<String,FeedbackSessionDetailsBundle> detailsMap =
				new HashMap<String,FeedbackSessionDetailsBundle>();
		
		______TS("standard success case");
		
		List<FeedbackSessionDetailsBundle> detailsList = 
				fsLogic.getFeedbackSessionDetailsForInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
		
		List<String> expectedSessions = new ArrayList<String>();
		expectedSessions.add(dataBundle.feedbackSessions.get("standard.session").toString());
		expectedSessions.add(dataBundle.feedbackSessions.get("no.responses.session").toString());
		expectedSessions.add(dataBundle.feedbackSessions.get("no.recipients.session").toString());
		expectedSessions.add(dataBundle.feedbackSessions.get("private.session").toString());
		
		String actualSessions = "";
		for (FeedbackSessionDetailsBundle details : detailsList) {
			actualSessions += details.feedbackSession.toString();
			detailsMap.put(
					details.feedbackSession.feedbackSessionName + "%" +
					details.feedbackSession.courseId,
					details);
		}
		
		assertEquals(detailsList.size(), 4);
		AssertHelper.assertContains(expectedSessions, actualSessions);
		
		/** Standard session **/
		FeedbackSessionStats stats =
				detailsMap.get(dataBundle.feedbackSessions.get("standard.session").feedbackSessionName + "%" +
								dataBundle.feedbackSessions.get("standard.session").courseId).stats;
		
		// 2 instructors, 6 students = 8
		assertEquals(stats.expectedTotal, 8);
		// 1 instructor, 1 student, did not respond => 8-2=6
		assertEquals(stats.submittedTotal, 6);
		
		
		/** No recipients session **/
		stats = detailsMap.get(dataBundle.feedbackSessions.get("no.recipients.session").feedbackSessionName + "%" +
								dataBundle.feedbackSessions.get("no.recipients.session").courseId).stats;
		
		// 2 instructors, 6 students = 8
		assertEquals(stats.expectedTotal, 8);
		// only 1 student responded
		assertEquals(stats.submittedTotal, 1);
		
		/** No responses session **/
		stats = detailsMap.get(dataBundle.feedbackSessions.get("no.responses.session").feedbackSessionName + "%" +
								dataBundle.feedbackSessions.get("no.responses.session").courseId).stats;
		
		// 1 instructors, 1 students = 2
		assertEquals(stats.expectedTotal, 2);
		// no responses
		assertEquals(stats.submittedTotal, 0);
		
		/** Private session **/
		stats = detailsMap.get(dataBundle.feedbackSessions.get("private.session").feedbackSessionName + "%" +
				dataBundle.feedbackSessions.get("private.session").courseId).stats;
		assertEquals(stats.expectedTotal, 1);
		// For private sessions, we mark as completed only when creator has finished all questions.
		assertEquals(stats.submittedTotal, 0);
		
		// Make session non-private
		FeedbackSessionAttributes privateSession = 
				dataBundle.feedbackSessions.get("private.session");
		privateSession.sessionVisibleFromTime = privateSession.startTime;
		privateSession.endTime = TimeHelper.convertToDate("2015-04-01 10:00 PM UTC");
		privateSession.feedbackSessionType = FeedbackSessionType.STANDARD;
		fsLogic.updateFeedbackSession(privateSession);
		
		// Re-read details
		detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
				dataBundle.instructors.get("instructor1OfCourse1").googleId);
		for (FeedbackSessionDetailsBundle details : detailsList) {
			if(details.feedbackSession.feedbackSessionName.equals(
					dataBundle.feedbackSessions.get("private.session").feedbackSessionName)){
				stats = details.stats;
				break;
			}
		}
		// 1 instructor (creator only), 6 students = 8
		assertEquals(stats.expectedTotal, 7);
		// 1 instructor, 1 student responded
		assertEquals(stats.submittedTotal, 2);
		
		______TS("instructor does not exist");
			
		assertTrue(fsLogic.getFeedbackSessionDetailsForInstructor("non-existent.google.id").isEmpty());
					
	}
	
	@Test
	public void testGetFeedbackSessionsForCourse() throws Exception {
		
		restoreTypicalDataInDatastore();
		dataBundle = getTypicalDataBundle();
		
		List<FeedbackSessionAttributes> actualSessions = null;
		
		______TS("Student viewing: 2 visible, 1 awaiting, 1 no questions");
		
		// 2 valid sessions in course 1, 0 in course 2.
		
		actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "student1InCourse1@gmail.com");
		
		// Student can see sessions 1 and 2. Session 3 has no questions. Session 4 is not yet visible for students.
		String expected =
				dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
				dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
				dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
		
		for (FeedbackSessionAttributes session : actualSessions) {
			AssertHelper.assertContains(session.toString(), expected);
		}
		assertTrue(actualSessions.size() == 3);
		
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
				dataBundle.feedbackSessions.get("awaiting.session").toString() + Const.EOL +
				dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
		
		for (FeedbackSessionAttributes session : actualSessions) {
			AssertHelper.assertContains(session.toString(), expected);
		}
		assertTrue(actualSessions.size() == 5);
		
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
	public void testGetFeedbackSessionQuestionsForStudent() throws Exception {
		
		______TS("standard test");

		restoreTypicalDataInDatastore();
		
		FeedbackSessionQuestionsBundle actual =
				fsLogic.getFeedbackSessionQuestionsForStudent(
						"First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.com");
		
		// We just test this once.
		assertEquals(actual.feedbackSession.toString(), 
				dataBundle.feedbackSessions.get("session1InCourse1").toString());
		
		// There should be 2 question for students to do in session 1.
		// The final question is set for SELF (creator) only.
		assertEquals(actual.questionResponseBundle.size(),2);
		
		// Question 1
		FeedbackQuestionAttributes expectedQuestion = 
				getQuestionFromDatastore("qn1InSession1InCourse1");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
		
		String expectedResponsesString = getResponseFromDatastore("response1ForQ1S1C1", dataBundle).toString();
		List<String> actualResponses = new ArrayList<String>();		
		for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
			actualResponses.add(responsesForQn.toString());
		}
		assertEquals(actualResponses.size(), 1);
		AssertHelper.assertContains(actualResponses, expectedResponsesString);
		
		// Question 2
		expectedQuestion = getQuestionFromDatastore("qn2InSession1InCourse1");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
		
		expectedResponsesString = getResponseFromDatastore("response2ForQ2S1C1",dataBundle).toString();	
		actualResponses.clear();		
		for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
			actualResponses.add(responsesForQn.toString());
		}
		assertEquals(actualResponses.size(), 1);
		AssertHelper.assertContains(actualResponses, expectedResponsesString);
		
		______TS("team feedback test");

		// Check that student3 get team member's (student4) feedback response as well (for team question).
		actual = fsLogic.getFeedbackSessionQuestionsForStudent(
						"Second feedback session", "idOfTypicalCourse1", "student3InCourse1@gmail.com");

		assertEquals(actual.questionResponseBundle.size(),2);
		
		// Question 1
		expectedQuestion = getQuestionFromDatastore("team.feedback");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

		expectedResponsesString = getResponseFromDatastore(
				"response1ForQ1S2C1", dataBundle).toString();
		actualResponses.clear();
		for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle
				.get(expectedQuestion)) {
			actualResponses.add(responsesForQn.toString());
		}
		assertEquals(actualResponses.size(), 1);
		AssertHelper.assertContains(actualResponses, expectedResponsesString);
		
		// Question 2, no responses from this student yet
		expectedQuestion = getQuestionFromDatastore("team.members.feedback");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
		assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());
		
		______TS("failure: invalid session");
		
		try {
			fsLogic.getFeedbackSessionQuestionsForStudent(
					"invalid session", "idOfTypicalCourse1", "student3InCourse1@gmail.com");
			signalFailureToDetectException("Did not detect that session does not exist.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(), "Trying to get a feedback session that does not exist.");
		}
		
	}
	
	@Test
	public void testGetFeedbackSessionQuestionsForInstructor() throws Exception {
		______TS("standard test");

		restoreTypicalDataInDatastore();
		
		FeedbackSessionQuestionsBundle actual =
				fsLogic.getFeedbackSessionQuestionsForInstructor(
						"Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.com");
		
		// We just test this once.
		assertEquals(actual.feedbackSession.toString(), 
				dataBundle.feedbackSessions.get("session2InCourse2").toString());
		
		// There should be 2 question for students to do in session 1.
		// The final question is set for SELF (creator) only.
		assertEquals(actual.questionResponseBundle.size(),2);
		
		// Question 1
		FeedbackQuestionAttributes expectedQuestion = 
				getQuestionFromDatastore("qn1InSession2InCourse2");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
		
		String expectedResponsesString = getResponseFromDatastore("response1ForQ1S2C2", dataBundle).toString();
		List<String> actualResponses = new ArrayList<String>();		
		for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
			actualResponses.add(responsesForQn.toString());
		}
		assertEquals(actualResponses.size(), 1);
		AssertHelper.assertContains(actualResponses, expectedResponsesString);
		
		// Question 2
		expectedQuestion = getQuestionFromDatastore("qn2InSession2InCourse2");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
		assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());
		
		______TS("private test: not creator");
		actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
						"Private feedback session", "idOfTypicalCourse2", "instructor2@course2.com");
		assertEquals(actual.questionResponseBundle.size(),0);
		
		______TS("private test: is creator");
		actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
						"Private feedback session", "idOfTypicalCourse2", "instructor1@course2.com");
		assertEquals(actual.questionResponseBundle.size(),1);
		expectedQuestion = getQuestionFromDatastore("qn1InSession1InCourse2");
		assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
		
		______TS("failure: invalid session");
		
		try {
			fsLogic.getFeedbackSessionQuestionsForInstructor(
					"invalid session", "idOfTypicalCourse1", "instructor1@course1.com");
			signalFailureToDetectException("Did not detect that session does not exist.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(), "Trying to get a feedback session that does not exist.");
		}
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
				fsLogic.getFeedbackSessionResultsForStudent(session.feedbackSessionName, 
						session.courseId, student.email);
	
		// We just check for correct session once
		assertEquals(results.feedbackSession.toString(), 
				session.toString());	
		
		// Student can see responses: q1r1, q2r3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r2
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
				"Team 1.1=Team 1.1",
				"Team 1.3=Team 1.3",
				"Team 1.4=Team 1.4",
				"FSRTest.instr1@course1.com=Instructor1 Course1",
				"FSRTest.student1InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
				"FSRTest.student2InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
				"FSRTest.student4InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.2");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.emailNameTable.size(), 10);
		
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
				getResponseId("qn7.resp1",responseBundle)+"={true,true}",
				getResponseId("qn7.resp2",responseBundle)+"={true,true}",
				getResponseId("qn8.resp2",responseBundle)+"={true,true}");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.visibilityTable.size(), 9);
		
		
		/*** Test result bundle for instructor1 ***/
		InstructorAttributes instructor =
				responseBundle.instructors.get("instructor1OfCourse1");		
		results = fsLogic.getFeedbackSessionResultsForInstructor(
				session.feedbackSessionName, 
				session.courseId, instructor.email);
		
		// Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
		assertEquals(results.responses.size(), 10);
		assertEquals(results.questions.size(), 5);
		
		// Test the user email-name maps used for display purposes
		mapString = results.emailNameTable.toString();
		expectedStrings.clear();
		Collections.addAll(expectedStrings,
				"%GENERAL%=%NOBODY%",
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
		assertEquals(results.emailNameTable.size(), 12);
		
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
				getResponseId("qn6.resp1",responseBundle)+"={true,true}");
		AssertHelper.assertContains(expectedStrings, mapString);
		assertEquals(results.visibilityTable.size(), 10);
		
		// TODO: test student2 too.
		
		______TS("private session");

		session = responseBundle.feedbackSessions.get("private.session");
		
		/*** Test result bundle for student1 ***/
		student =  responseBundle.students.get("student1InCourse1");		
		results = fsLogic.getFeedbackSessionResultsForStudent(session.feedbackSessionName, 
						session.courseId, student.email);
		
		assertEquals(results.questions.size(),0);
		assertEquals(results.responses.size(),0);
		assertEquals(results.emailNameTable.size(),0);
		assertEquals(results.visibilityTable.size(),0);
		
		/*** Test result bundle for instructor1 ***/
		
		instructor =
				responseBundle.instructors.get("instructor1OfCourse1");		
		results = fsLogic.getFeedbackSessionResultsForInstructor(
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
			fsLogic.getFeedbackSessionResultsForInstructor("invalid session", 
				session.courseId, instructor.email);
			signalFailureToDetectException("Did not detect that session does not exist.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(), "Trying to view non-existent feedback session.");
		}
		//TODO: check for cases where a person is both a student and an instructor
	}
	
	
	@Test
	public void testGetFeedbackSessionResultsSummaryAsCsv() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("typical case");
	
		FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		
		String export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
				session.feedbackSessionName, session.courseId, instructor.email);
		
		/* This is what export should look like:
		==================================
		Course,idOfTypicalCourse1
		Session Name,First feedback session
		
		
		Question 1,"What is the best selling point of your product?"
		
		Giver,Recipient,Feedback
		"student1 In Course1","student1 In Course1","Student 1 self feedback."
		"student2 In Course1","student2 In Course1","I'm cool'"
		
		
		Question 2,"Rate 5 other students' products",
		Giver,Recipient,Feedback
		"student1 In Course1","student1 In Course1","Response from student 1 to student 2."
		"student2 In Course1","student1 In Course1","Response from student 2 to student 1."
		"student3 In Course1","student2 In Course1","Response from student 3 ""to"" student 2.
		Multiline test."
		
		
		Question 3,"My comments on the class",
		Giver,Recipient,Feedback
		"Instructor1 Course1","-","Good work, keep it up!"
		*/
		
		String[] exportLines = export.split(Const.EOL);
		assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
		assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
		assertEquals(exportLines[2], "");
		assertEquals(exportLines[3], "");
		assertEquals(exportLines[4], "Question 1,\"What is the best selling point of your product?\"");
		assertEquals(exportLines[5], "");
		assertEquals(exportLines[6], "Giver,Recipient,Feedback");
		assertEquals(exportLines[7], "\"student1 In Course1\",\"student1 In Course1\",\"Student 1 self feedback.\"");
		// checking single quotes inside cell
		assertEquals(exportLines[8], "\"student2 In Course1\",\"student2 In Course1\",\"I'm cool'\"");
		assertEquals(exportLines[9], "");
		assertEquals(exportLines[10], "");
		assertEquals(exportLines[11], "Question 2,\"Rate 1 other student's product\"");
		assertEquals(exportLines[12], "");
		assertEquals(exportLines[13], "Giver,Recipient,Feedback");
		assertEquals(exportLines[14], "\"student2 In Course1\",\"student1 In Course1\",\"Response from student 2 to student 1.\"");
		assertEquals(exportLines[15], "\"student1 In Course1\",\"student2 In Course1\",\"Response from student 1 to student 2.\"");
		// checking double quotes inside cell + multiline cell
		assertEquals(exportLines[16].trim(), "\"student3 In Course1\",\"student2 In Course1\",\"Response from student 3 \"\"to\"\" student 2.");
		assertEquals(exportLines[17], "Multiline test.\"");
		assertEquals(exportLines[18], "");
		assertEquals(exportLines[19], "");
		assertEquals(exportLines[20], "Question 3,\"My comments on the class\"");
		assertEquals(exportLines[21], "");
		assertEquals(exportLines[22], "Giver,Recipient,Feedback");
		// checking comma inside cell
		assertEquals(exportLines[23], "\"Instructor1 Course1\",\"-\",\"Good work, keep it up!\"");
		
		______TS("MCQ results");
		
		restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
		dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		session = dataBundle.feedbackSessions.get("mcqSession");
		instructor = dataBundle.instructors.get("instructor1OfCourse1");
		
		export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
				session.feedbackSessionName, session.courseId, instructor.email);
		
		/*This is how the export should look like
		=======================================
		Course,"idOfTypicalCourse1"
		Session Name,"MCQ Session"
		 
		 
		Question 1,"What do you like best about our product?"
		 
		Giver,Recipient,Feedback
		"student1 In Course1","student1 In Course1","It's good"
		"student2 In Course1","student2 In Course1","It's perfect"
		*/
		
		exportLines = export.split(Const.EOL);
		assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
		assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
		assertEquals(exportLines[2], "");
		assertEquals(exportLines[3], "");
		assertEquals(exportLines[4], "Question 1,\"What do you like best about our product?\"");
		assertEquals(exportLines[5], "");
		assertEquals(exportLines[6], "Giver,Recipient,Feedback");
		assertEquals(exportLines[7], "\"student1 In Course1\",\"student1 In Course1\",\"It's good\"");
		assertEquals(exportLines[8], "\"student2 In Course1\",\"student2 In Course1\",\"It's perfect\"");
				
		______TS("Non-existent Course/Session");
		
		try {
			fsLogic.getFeedbackSessionResultsSummaryAsCsv("non.existent", "no course", instructor.email);
			signalFailureToDetectException("Failed to detect non-existent feedback session.");
		} catch (EntityDoesNotExistException e) {
			assertEquals(e.getMessage(), "Trying to view non-existent feedback session.");
		}
	}

	public void testUpdateFeedbackSession() {
		
	}
	
	@Test
	public void testPublishUnpublishFeedbackSession() throws Exception {
		restoreTypicalDataInDatastore();
		PublishUnpublishSessionCallback.resetTaskCount();
		
		______TS("success: publish");
		FeedbackSessionAttributes
			sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse1");
		
		// set as manual publish
		
		sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
		fsLogic.updateFeedbackSession(sessionUnderTest);
		
		fsLogic.publishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
		FeedbackSessionPublishedCallback.waitForTaskQueueExecution(1);
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
					"Did not catch exception signalling that private session can't " +
					"be published.");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Private session can't be published.");
		}
		
		try{
			fsLogic.unpublishFeedbackSession(
				sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
			signalFailureToDetectException(
					"Did not catch exception signalling that private session should " +
					"not be published");
		} catch (InvalidParametersException e) {
			assertEquals(e.getMessage(),
					"Private session can't be published.");
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
	
	private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
		FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
		questionToGet = fqLogic.getFeedbackQuestion(
				questionToGet.feedbackSessionName, 
				questionToGet.courseId,
				questionToGet.questionNumber);
		
		return questionToGet;
	}

	// Extract response id from datastore based on json key.
	private String getResponseId(String jsonId, DataBundle bundle) {
		return getResponseFromDatastore(jsonId, bundle).getId();
	}
	
	private FeedbackResponseAttributes getResponseFromDatastore(String jsonId, DataBundle bundle) {
		FeedbackResponseAttributes response = bundle.feedbackResponses.get(jsonId);
		
		String questionId = null;		
		try {
			int qnNumber = Integer.parseInt(response.feedbackQuestionId);		
			questionId = fqLogic.getFeedbackQuestion(
						response.feedbackSessionName, response.courseId,
						qnNumber).getId();
		} catch (NumberFormatException e) {
			questionId = response.feedbackQuestionId;
		}
		
		return frLogic.getFeedbackResponse(questionId, 
				response.giverEmail, response.recipientEmail);
	}
	
	private void unpublishAllSessions() throws InvalidParametersException, EntityDoesNotExistException {
		for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
			if(fs.isPublished()) {
				fsLogic.unpublishFeedbackSession(fs.feedbackSessionName, fs.courseId);				
			}
		}
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
