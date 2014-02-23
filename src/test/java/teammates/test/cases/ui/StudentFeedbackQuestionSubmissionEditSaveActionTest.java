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
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentFeedbackQuestionSubmissionEditSaveAction;

public class StudentFeedbackQuestionSubmissionEditSaveActionTest extends
		BaseActionTest {

	DataBundle dataBundle;

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}

	@Test
	public void testAccessControl() throws Exception {
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		
		FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
		FeedbackQuestionAttributes q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId(),
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "0"
		};
		
		verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);		
	}

	@Test
	public void testExecuteAndPostProcess() throws Exception {
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		
		FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
		FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
		
		FeedbackResponsesDb frDb = new FeedbackResponsesDb();
		FeedbackResponseAttributes fr = frDb.getFeedbackResponse(fq.getId(), student.email, student.email);
		assertNotNull(fr);
		
		gaeSimulation.loginAsStudent(student.googleId);
		
		______TS("not enough parameters");
		
		verifyAssumptionFailure();
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
		};
		verifyAssumptionFailure(submissionParams);
		
		______TS("edit existing answer");
		
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), student.email, fr.recipientEmail));
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
		};
		
		StudentFeedbackQuestionSubmissionEditSaveAction a = getAction(submissionParams);
		ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
		assertFalse(r.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,	r.getStatusMessage());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), student.email, fr.recipientEmail));
		
		______TS("delete answer");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
		};
		
		a = getAction(submissionParams);
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
		assertFalse(r.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,	r.getStatusMessage());
		assertNull(frDb.getFeedbackResponse(fq.getId(), student.email, fr.recipientEmail));
		
		______TS("skip question");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", ""
		};
		
		a = getAction(submissionParams);
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
		assertFalse(r.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,	r.getStatusMessage());
		assertNull(frDb.getFeedbackResponse(fq.getId(), student.email, fr.recipientEmail));
				
		______TS("new response");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "response"
		};
		
		a = getAction(submissionParams);
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
		assertFalse(r.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,	r.getStatusMessage());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), student.email, fr.recipientEmail));
	}
	
	private StudentFeedbackQuestionSubmissionEditSaveAction getAction(String... params) throws Exception{
		return (StudentFeedbackQuestionSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
	}
}
