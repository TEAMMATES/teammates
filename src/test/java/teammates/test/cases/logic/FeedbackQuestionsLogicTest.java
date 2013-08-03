package teammates.test.cases.logic;

import static org.testng.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.BaseComponentTestCase;

public class FeedbackQuestionsLogicTest extends BaseComponentTestCase {
	
	private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
	private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private DataBundle typicalBundle = getTypicalDataBundle();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackSessionsLogic.class);
	}
	
	
	@Test
	public void testGetRecipientsForQuestion() throws Exception {
	
		restoreTypicalDataInDatastore();		
	
		FeedbackQuestionAttributes question;
		String email;
		Map<String, String> recipients;
		
		______TS("response to students, total 5");
		
		question = getQuestionFromDatastore("qn2InSession1InCourse1");
		email = typicalBundle.students.get("student1InCourse1").email;		
		recipients = fqLogic.getRecipientsForQuestion(question, email);		
		assertEquals(recipients.size(), 4); // 5 students minus giver himself
		
		email = typicalBundle.instructors.get("instructor1OfCourse1").email;
		recipients = fqLogic.getRecipientsForQuestion(question, email);
		assertEquals(recipients.size(), 5); // instructor is not student so he can respond to all 5.
		
		______TS("response to instructors, total 3");
		
		question = getQuestionFromDatastore("qn2InSession2InCourse2");
		email = typicalBundle.instructors.get("instructor1OfCourse2").email;
		recipients = fqLogic.getRecipientsForQuestion(question, email);
		assertEquals(recipients.size(), 2); // 3 - giver = 2
		
		______TS("empty case: response to team members, but alone");

		question = getQuestionFromDatastore("team.members.feedback");
		email = typicalBundle.students.get("student5InCourse1").email;
		recipients = fqLogic.getRecipientsForQuestion(question, email);
		assertEquals(recipients.size(), 0);
						
		______TS("special case: response to other team, instructor is also student");
		question = getQuestionFromDatastore("team.feedback");
		email = typicalBundle.students.get("student1InCourse1").email;
		AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
		
		recipients = fqLogic.getRecipientsForQuestion(question, email);	

		assertEquals(recipients.size(), 1);
		
		______TS("to nobody (general feedback)");
		question = getQuestionFromDatastore("qn3InSession1InCourse1");
		email = typicalBundle.students.get("student1InCourse1").email;
		AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
		
		recipients = fqLogic.getRecipientsForQuestion(question, email);	
		assertEquals(recipients.get(Const.GENERAL_QUESTION), Const.GENERAL_QUESTION);
		assertEquals(recipients.size(), 1);
		
		______TS("to self");
		question = getQuestionFromDatastore("qn1InSession1InCourse1");
		email = typicalBundle.students.get("student1InCourse1").email;
		AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
		
		recipients = fqLogic.getRecipientsForQuestion(question, email);	
		assertEquals(recipients.get(email), Const.USER_NAME_FOR_SELF);
		assertEquals(recipients.size(), 1);

	}
	
	@Test
	public void testUpdateQuestion() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("standard update, no existing responses, with 'keep existing' policy");
		FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn2InSession2InCourse2");
		questionToUpdate.questionText = new Text("new question text");
		questionToUpdate.questionNumber = 3;
		List<FeedbackParticipantType> newVisibility = 
				new LinkedList<FeedbackParticipantType>();
		newVisibility.add(FeedbackParticipantType.INSTRUCTORS);
		questionToUpdate.showResponsesTo = newVisibility;
		// Check keep existing policy.
		String originalCourseId = questionToUpdate.courseId;
		questionToUpdate.courseId = null;
		
		fqLogic.updateFeedbackQuestion(questionToUpdate);
		
		questionToUpdate.courseId = originalCourseId;

		FeedbackQuestionAttributes updatedQuestion =
				fqLogic.getFeedbackQuestion(questionToUpdate.getId());		
		assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
		
		______TS("cascading update, non-destructive changes, existing responses are preserved");
		questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
		questionToUpdate.questionText = new Text("new question text 2");
		questionToUpdate.numberOfEntitiesToGiveFeedbackTo = 2;
		
		int numberOfResponses =
				frLogic.getFeedbackResponsesForQuestion(
						questionToUpdate.getId()).size();
		
		fqLogic.updateFeedbackQuestion(questionToUpdate);
		updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());
		
		assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
		assertEquals(
				frLogic.getFeedbackResponsesForQuestion(
						questionToUpdate.getId()).size(), numberOfResponses);
		
		______TS("cascading update, destructive changes, delete all existing responses");
		questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
		questionToUpdate.questionText = new Text("new question text 3");
		questionToUpdate.recipientType = FeedbackParticipantType.INSTRUCTORS;
		
		assertTrue(frLogic.getFeedbackResponsesForQuestion(
						questionToUpdate.getId()).isEmpty() == false);
		
		fqLogic.updateFeedbackQuestion(questionToUpdate);
		updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());
		
		assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
		assertEquals(frLogic.getFeedbackResponsesForQuestion(
				questionToUpdate.getId()).size(), 0);

		______TS("failure: question does not exist");
		
		questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
		fqLogic.deleteFeedbackQuestionCascade(questionToUpdate.getId());
		
		try {
			fqLogic.updateFeedbackQuestion(questionToUpdate);
			 signalFailureToDetectException("Expected EntityDoesNotExistException not caught.");
		} catch (EntityDoesNotExistException e){
			assertEquals(e.getMessage(), "Trying to update a feedback question that does not exist.");
		}
		
	}

	private FeedbackQuestionAttributes getQuestionFromDatastore(String questionKey) {
		FeedbackQuestionAttributes question;
		question = typicalBundle.feedbackQuestions.get(questionKey);
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		return question;
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(FeedbackSessionsLogic.class);
	}
}
