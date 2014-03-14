package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorFeedbackResponseCommentDeleteAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackResponseCommentDeleteActionTest extends
		BaseActionTest {

	DataBundle dataBundle;

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE;
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
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "1",
				Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
		};
		verifyOnlyInstructorsCanAccess(submissionParams);
	}
	
	@Test
	public void testExcecuteAndPostProcess() throws Exception {
		FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
		FeedbackResponsesDb frDb = new FeedbackResponsesDb();
		FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

		FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion(
				"First feedback session", "idOfTypicalCourse1", 1);
		FeedbackResponseAttributes fr = frDb.getFeedbackResponse(fq.getId(),
				"student1InCourse1@gmail.com", "student1InCourse1@gmail.com");
		FeedbackResponseCommentAttributes frc = dataBundle.feedbackResponseComments
				.get("comment1FromT1C1ToR1Q1S1C1");
		frc = frcDb.getFeedbackResponseComment(fr.getId(),
				frc.giverEmail, frc.createdAt);
		assertNotNull("response comment not found", frc);
		
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		gaeSimulation.loginAsInstructor(instructor.googleId);
		
		______TS("not enough parameters");
		
		verifyAssumptionFailure();
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, frc.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, frc.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
				Const.ParamsNames.USER_ID, instructor.googleId
		};
		
		verifyAssumptionFailure(submissionParams);
		
		______TS("typical case");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, frc.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, frc.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, frc.commentText + " (Edited)",
				Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
		};
		
		InstructorFeedbackResponseCommentDeleteAction a = getAction(submissionParams);
		RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE 
				+ "?courseid=idOfTypicalCourse1&fsname=First+feedback+session"
				+ "&user=idOfInstructor1OfCourse1&frsorttype=recipient"
				+ "&message=Your+comment+has+been+deleted+successfully&error=false",
				rr.getDestinationWithParams());
		assertFalse(rr.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_DELETED, rr.getStatusMessage());
		assertNull(frcDb.getFeedbackResponseComment(frc.feedbackResponseId,
				frc.giverEmail, frc.createdAt));
	}
	
	private InstructorFeedbackResponseCommentDeleteAction getAction(String... params) throws Exception {
		return (InstructorFeedbackResponseCommentDeleteAction) (gaeSimulation.getActionObject(uri, params));
	}
}
