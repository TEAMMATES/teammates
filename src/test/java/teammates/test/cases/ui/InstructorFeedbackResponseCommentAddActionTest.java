package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorFeedbackResponseCommentAddAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackResponseCommentAddActionTest extends
		BaseActionTest {

	DataBundle dataBundle;

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}

	@Test
	public void testAccessControl() throws Exception {
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
				Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
		};
		verifyOnlyInstructorsCanAccess(submissionParams);
	}
	
	@Test
	public void testExcecuteAndPostProcess() throws Exception {
		FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
		FeedbackResponsesDb frDb = new FeedbackResponsesDb();

		FeedbackSessionAttributes fs = dataBundle.feedbackSessions
				.get("session1InCourse1");
		FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion(
				fs.feedbackSessionName, fs.courseId, 1);
		FeedbackResponseAttributes fr = frDb.getFeedbackResponse(fq.getId(),
				"student1InCourse1@gmail.com", "student1InCourse1@gmail.com");
		
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		gaeSimulation.loginAsInstructor(instructor.googleId);
		
		______TS("not enough parameters");
		
		verifyAssumptionFailure();
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response"
		};
		
		verifyAssumptionFailure(submissionParams);
		
		______TS("typical case");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
				Const.ParamsNames.FEEDBACK_RESPONSE_ID, fr.getId(),
				Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
		};
		
		InstructorFeedbackResponseCommentAddAction a = getAction(submissionParams);
		RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE 
				+ "?courseid=idOfTypicalCourse1&fsname=First+feedback+session"
				+ "&user=idOfInstructor1OfCourse1&frsorttype=recipient"
				+ "&message=Your+comment+has+been+saved+successfully&error=false",
				rr.getDestinationWithParams());
		assertFalse(rr.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_ADDED, rr.getStatusMessage());
		
		______TS("empty comment text");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
				Const.ParamsNames.FEEDBACK_RESPONSE_ID, fr.getId(),
				Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
		};
		
		a = getAction(submissionParams);
		rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE 
				+ "?courseid=idOfTypicalCourse1&fsname=First+feedback+session"
				+ "&user=idOfInstructor1OfCourse1&frsorttype=recipient"
				+ "&message=Comment+cannot+be+empty&error=true",
				rr.getDestinationWithParams());
		assertTrue(rr.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, rr.getStatusMessage());
	}
	
	private InstructorFeedbackResponseCommentAddAction getAction(String... params) throws Exception {
		return (InstructorFeedbackResponseCommentAddAction) (gaeSimulation.getActionObject(uri, params));
	}
}
