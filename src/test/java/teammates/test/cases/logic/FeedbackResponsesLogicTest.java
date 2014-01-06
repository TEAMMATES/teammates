package teammates.test.cases.logic;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class FeedbackResponsesLogicTest extends BaseComponentTestCase {
	
	private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
	private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private static FeedbackResponsesDb frDb = new FeedbackResponsesDb();
	private DataBundle typicalBundle = getTypicalDataBundle();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackResponsesLogic.class);
	}

	@Test
	public void testUpdateFeedbackResponse() throws Exception {
		
		______TS("success: standard update with carried params ");
		restoreTypicalDataInDatastore();
		
		FeedbackResponseAttributes responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
		
		responseToUpdate.responseMetaData = new Text("Updated Response");
		responseToUpdate.feedbackSessionName = "copy over";
		responseToUpdate.recipientEmail = null;
		
		frLogic.updateFeedbackResponse(responseToUpdate);
		
		responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
		responseToUpdate.responseMetaData = new Text("Updated Response");
		
		assertEquals(frLogic.getFeedbackResponse(
				responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
				responseToUpdate.toString());
		
		______TS("success: recipient changed to something else");
		
		responseToUpdate.recipientEmail = "student5InCourse1@gmail.com";
		
		frLogic.updateFeedbackResponse(responseToUpdate);
		
		assertEquals(frLogic.getFeedbackResponse(
				responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
				responseToUpdate.toString());
		assertNull(frLogic.getFeedbackResponse(
				responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, "student2InCourse1@gmail.com"));
		
		______TS("success: both giver and recipient changed (teammate changed response)");
		
		responseToUpdate = getResponseFromDatastore("response1ForQ1S2C1");
		responseToUpdate.giverEmail = "student5InCourse1@gmail.com";
		responseToUpdate.recipientEmail = "Team 1.1";
		
		assertNotNull(frLogic.getFeedbackResponse(
				responseToUpdate.feedbackQuestionId, "student4InCourse1@gmail.com","Team 1.2"));
		
		frLogic.updateFeedbackResponse(responseToUpdate);
		
		assertEquals(frLogic.getFeedbackResponse(
				responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
				responseToUpdate.toString());
		assertNull(frLogic.getFeedbackResponse(
				responseToUpdate.feedbackQuestionId, "student4InCourse1@gmail.com","Team 1.2"));
		
		______TS("failure: recipient one that is already exists");

		restoreTypicalDataInDatastore();		
		responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
		
		FeedbackResponseAttributes existingResponse = 
				new FeedbackResponseAttributes(
						responseToUpdate.feedbackSessionName, 
						responseToUpdate.courseId, 
						responseToUpdate.feedbackQuestionId, 
						responseToUpdate.feedbackQuestionType, 
						responseToUpdate.giverEmail, 
						"student3InCourse1@gmail.com", 
						responseToUpdate.responseMetaData);
		
		frLogic.createFeedbackResponse(existingResponse);
		
		responseToUpdate.recipientEmail = "student3InCourse1@gmail.com";
		
		try {
			frLogic.updateFeedbackResponse(responseToUpdate);
			signalFailureToDetectException("Should have detected that same giver->recipient response alr exists");
		} catch (EntityAlreadyExistsException e){
			AssertHelper.assertContains(
						"Trying to update recipient for response to one that already exists for this giver.", 
						e.getMessage());
		}
		
		______TS("failure: invalid params");
		
		// Cannot have invalid params as all possible invalid params
		// are copied over from an existing response.
		
		______TS("failure: no such response");
		
		responseToUpdate.setId("invalidId");
		
		try {
			frLogic.updateFeedbackResponse(responseToUpdate);
			signalFailureToDetectException("Should have detected that this response does not exist");
		} catch (EntityDoesNotExistException e){
			AssertHelper.assertContains(
						"Trying to update a feedback response that does not exist.", 
						e.getMessage());
		}
	}
	
	@Test
	public void testUpdateFeedbackResponsesForChangingTeam() throws Exception {
		
		// TODO: test that non team questions are preserved.
		
		______TS("standard update team case");

		restoreTypicalDataInDatastore();
		
		StudentAttributes studentToUpdate = typicalBundle.students.get("student4InCourse1");
		
		// Student 4 has 1 responses to him from team members,
		// 1 response from him a team member, and
		// 1 team response from him to another team.
		FeedbackQuestionAttributes teamQuestion = getQuestionFromDatastore("team.members.feedback");
		assertEquals(frLogic.getFeedbackResponsesForReceiverForQuestion(
				teamQuestion.getId(), studentToUpdate.email).size(),1);
		assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
				teamQuestion.getId(), studentToUpdate.email).size(),1);
		
		teamQuestion = getQuestionFromDatastore("team.feedback");
		assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
				teamQuestion.getId(), studentToUpdate.email).size(),1);
		
		// All these responses should be gone after he changes teams
		
		frLogic.updateFeedbackResponsesForChangingTeam(
				studentToUpdate.course, studentToUpdate.email, studentToUpdate.team, "Team 1.2");
		
		teamQuestion = getQuestionFromDatastore("team.members.feedback");
		assertEquals(frLogic.getFeedbackResponsesForReceiverForQuestion(
				teamQuestion.getId(), studentToUpdate.email).size(),0);
		assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
				teamQuestion.getId(), studentToUpdate.email).size(),0);
		
		teamQuestion = getQuestionFromDatastore("team.feedback");
		assertEquals(frLogic.getFeedbackResponsesForReceiverForQuestion(
				teamQuestion.getId(), studentToUpdate.email).size(),0);
	}
	
	@Test
	public void testUpdateFeedbackResponsesForChangingEmail() throws Exception {
		______TS("standard update email case");
		
		restoreTypicalDataInDatastore();
		
		// Student 1 currently has 3 responses to him and 3 from himself.
		StudentAttributes studentToUpdate = typicalBundle.students.get("student1InCourse1");
		assertEquals(frLogic.getFeedbackResponsesForReceiverForCourse(
				studentToUpdate.course, studentToUpdate.email).size(), 2);
		assertEquals(frLogic.getFeedbackResponsesFromGiverForCourse(
				studentToUpdate.course, studentToUpdate.email).size(), 3);
		
		frLogic.updateFeedbackResponsesForChangingEmail(
				studentToUpdate.course, studentToUpdate.email, "new@email.com");
		
		assertEquals(frLogic.getFeedbackResponsesForReceiverForCourse(
				studentToUpdate.course, studentToUpdate.email).size(), 0);
		assertEquals(frLogic.getFeedbackResponsesFromGiverForCourse(
				studentToUpdate.course, studentToUpdate.email).size(), 0);
		assertEquals(frLogic.getFeedbackResponsesForReceiverForCourse(
				studentToUpdate.course, "new@email.com").size(), 2);
		assertEquals(frLogic.getFeedbackResponsesFromGiverForCourse(
				studentToUpdate.course, "new@email.com").size(), 3);
	}
	
	@Test
	public void testDeleteFeedbackResponsesForStudent() throws Exception {	
		
		______TS("standard delete");
		
		restoreTypicalDataInDatastore();
		
		StudentAttributes studentToDelete = typicalBundle.students.get("student1InCourse1");;
		
		frLogic.deleteFeedbackResponsesForStudent(studentToDelete.course, studentToDelete.email);
		
		List<FeedbackResponseAttributes> remainingResponses = new ArrayList<FeedbackResponseAttributes>();				
		remainingResponses.addAll(frDb.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
		remainingResponses.addAll(frDb.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));		
		assertEquals(remainingResponses.size(), 0);		
		
		______TS("shift team then delete");
		
		restoreTypicalDataInDatastore();
		remainingResponses.clear();
		
		studentToDelete = typicalBundle.students.get("student2InCourse1");;
		
		studentToDelete.team = "Team 1.3";
		StudentsLogic.inst().updateStudentCascade(studentToDelete.email, studentToDelete);

		frLogic.deleteFeedbackResponsesForStudent(studentToDelete.course, studentToDelete.email);
		
		remainingResponses.addAll(frDb.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
		remainingResponses.addAll(frDb.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));		
		assertEquals(remainingResponses.size(), 0);				
		
		______TS("delete last person in team");

		restoreTypicalDataInDatastore();
		remainingResponses.clear();
				
		studentToDelete = typicalBundle.students.get("student5InCourse1");
		
		frLogic.deleteFeedbackResponsesForStudent(studentToDelete.course, studentToDelete.email);
		remainingResponses.addAll(frDb.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
		remainingResponses.addAll(frDb.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
		
		// check that team responses are gone too. already checked giver as it is stored by giver email not team id.
		remainingResponses.addAll(frDb.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, "Team 1.2"));

		assertEquals(remainingResponses.size(),0);	
	}
	
	private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
		FeedbackQuestionAttributes questionToGet = typicalBundle.feedbackQuestions.get(jsonId);
		questionToGet = fqLogic.getFeedbackQuestion(questionToGet.feedbackSessionName, 
													questionToGet.courseId,
													questionToGet.questionNumber);
		
		return questionToGet;
	}
	
	private FeedbackResponseAttributes getResponseFromDatastore(String jsonId){
		FeedbackResponseAttributes response =
				typicalBundle.feedbackResponses.get(jsonId);
		
		int qnNumber = Integer.parseInt(response.feedbackQuestionId);
		
		String qnId = fqLogic.getFeedbackQuestion(
				response.feedbackSessionName, response.courseId, qnNumber).getId();
		
		return frLogic.getFeedbackResponse(
				qnId, response.giverEmail, response.recipientEmail);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(FeedbackResponsesLogic.class);
	}
}
