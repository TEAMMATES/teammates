package teammates.test.cases.common;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

public class FeedbackQuestionAttributesTest extends BaseTestCase {
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
	}
	
	@Test
	public void testValidate() {
		// TODO: follow test sequence similar to evalTest 
	}
	
	@Test
	public void testRemoveIrrelevantVisibilityOptions() {
		
		______TS("test teams->none");
		
		FeedbackQuestionAttributes question = 
				new FeedbackQuestionAttributes();
		List<FeedbackParticipantType> participants =
				new ArrayList<FeedbackParticipantType>();
		
		question.feedbackSessionName = "test session"; 
		question.courseId = "some course";
		question.creatorEmail = "test@case.com";
		question.questionText = new Text("test qn from teams->none.");
		question.questionNumber = 1;
		question.questionType = FeedbackQuestionType.TEXT;
		question.giverType = FeedbackParticipantType.TEAMS;
		question.recipientType = FeedbackParticipantType.NONE;
		question.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
		participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
		participants.add(FeedbackParticipantType.RECEIVER);
		participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
		question.showGiverNameTo = new ArrayList<FeedbackParticipantType>(participants);
		question.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(participants);
		participants.add(FeedbackParticipantType.STUDENTS);
		question.showResponsesTo = new ArrayList<FeedbackParticipantType>(participants);
		
		question.removeIrrelevantVisibilityOptions();
		
		assertTrue(question.showGiverNameTo.isEmpty());
		assertTrue(question.showRecipientNameTo.isEmpty());
		// check that other types are not removed
		assertTrue(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS));
		assertEquals(question.showResponsesTo.size(), 1);
		
		______TS("test students->teams");
		
		question.giverType = FeedbackParticipantType.STUDENTS;
		question.recipientType = FeedbackParticipantType.TEAMS;

		participants.clear();
		participants.add(FeedbackParticipantType.INSTRUCTORS);
		participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
		participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);		
		question.showGiverNameTo = new ArrayList<FeedbackParticipantType>(participants);
		participants.add(FeedbackParticipantType.STUDENTS);
		question.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(participants);
		question.showResponsesTo = new ArrayList<FeedbackParticipantType>(participants);
		
		question.removeIrrelevantVisibilityOptions();
		
		assertEquals(question.showGiverNameTo.size(),2);
		assertEquals(question.showRecipientNameTo.size(),3);
		assertEquals(question.showResponsesTo.size(), 3);
		assertTrue(!question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
		assertTrue(!question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
		assertTrue(!question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
		
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
	}
}
