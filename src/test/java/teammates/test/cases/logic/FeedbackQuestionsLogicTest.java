package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.BaseComponentTestCase;

public class FeedbackQuestionsLogicTest extends BaseComponentTestCase {
	
	private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackSessionsLogic.class);
	}
	
	
	@Test
	public void testGetRecipientsForQuestion() throws Exception {
	
		restoreTypicalDataInDatastore();	
		DataBundle typicalBundle = getTypicalDataBundle();
	
		FeedbackQuestionAttributes question;
		String email;
		Map<String, String> recipients;
		
		______TS("response to students, total 5");
		
		question = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1");
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		email = typicalBundle.students.get("student1InCourse1").email;		
		recipients = fqLogic.getRecipientsForQuestion(question, email);		
		assertEquals(recipients.size(), 4); // 5 students minus giver himself
		
		email = typicalBundle.instructors.get("instructor1OfCourse1").email;
		recipients = fqLogic.getRecipientsForQuestion(question, email);
		assertEquals(recipients.size(), 5); // instructor is not student so he can respond to all 5.
		
		______TS("response to instructors, total 3");
		
		question = typicalBundle.feedbackQuestions.get("qn2InSession2InCourse2");
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		email = typicalBundle.instructors.get("instructor1OfCourse2").email;
		recipients = fqLogic.getRecipientsForQuestion(question, email);
		assertEquals(recipients.size(), 2); // 3 - giver = 2
		
		______TS("empty case: response to team members, but alone");

		question = typicalBundle.feedbackQuestions.get("qn2InSession2InCourse1");
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		email = typicalBundle.students.get("student5InCourse1").email;
		recipients = fqLogic.getRecipientsForQuestion(question, email);
		assertEquals(recipients.size(), 0);
						
		______TS("special case: response to other team, instructor is also student");
		question = typicalBundle.feedbackQuestions.get("qn1InSession2InCourse1");
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		email = typicalBundle.students.get("student1InCourse1").email;
		AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
		
		recipients = fqLogic.getRecipientsForQuestion(question, email);	

		assertEquals(recipients.size(), 1);
		
		______TS("to nobody (general feedback)");
		question = typicalBundle.feedbackQuestions.get("qn3InSession1InCourse1");
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		email = typicalBundle.students.get("student1InCourse1").email;
		AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
		
		recipients = fqLogic.getRecipientsForQuestion(question, email);	
		assertEquals(recipients.get(Const.GENERAL_QUESTION), Const.GENERAL_QUESTION);
		assertEquals(recipients.size(), 1);
		
		______TS("to self");
		question = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
		question = fqLogic.getFeedbackQuestion(
				question.feedbackSessionName, question.courseId, question.questionNumber);
		email = typicalBundle.students.get("student1InCourse1").email;
		AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
		
		recipients = fqLogic.getRecipientsForQuestion(question, email);	
		assertEquals(recipients.get(email), typicalBundle.students.get("student1InCourse1").name);
		assertEquals(recipients.size(), 1);

	}
	

	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(FeedbackSessionsLogic.class);
	}
}
