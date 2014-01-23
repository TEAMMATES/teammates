package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.InstructorFeedbackSubmissionEditSaveAction;
import teammates.ui.controller.RedirectResult;


public class InstructorFeedbackSubmissionEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = loadDataBundle("/InstructorFeedbackSubmissionEditSaveActionTest.json");
		restoreDatastoreFromJson("/InstructorFeedbackSubmissionEditSaveActionTest.json");
	}
	
	@Test
	public void testAccessControl() throws Exception{
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		//TODO Test error states (catch-blocks and isError == true states)
		
		______TS("edit existing answer");
		
		FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
		FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First Session", "idOfCourse1", 1);
		assertNotNull("Feedback question not found in database", fq);
		
		FeedbackResponsesDb frDb = new FeedbackResponsesDb();
		FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
		fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
		assertNotNull("Feedback response not found in database", fr);
		
		InstructorAttributes instructor1InCourse1 = dataBundle.instructors.get("instructor1InCourse1");
		gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString()				
		};
		
		InstructorFeedbackSubmissionEditSaveAction a = getAction(submissionParams);
		RedirectResult r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("deleted response");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""				
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
				
		______TS("skipped question");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""				
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("new response");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "New " + fr.getResponseDetails().getAnswerString()				
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("edit response, did not specify recipient");
		
		fq = fqDb.getFeedbackQuestion("First Session", "idOfCourse1", 2);
		assertNotNull("Feedback question not found in database", fq);
		
		fr = dataBundle.feedbackResponses.get("response1ForQ2S1C1");
		fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
		assertNotNull("Feedback response not found in database", fr);
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-2-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", "Edited" + fr.getResponseDetails().getAnswerString()				
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("new response, did not specify recipient");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", fr.getResponseDetails().getAnswerString()				
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("private session");
		
		fq = fqDb.getFeedbackQuestion("Private Session", "idOfCourse1", 1);
		assertNotNull("Feedback question not found in database", fq);
		
		fr = dataBundle.feedbackResponses.get("response1ForPrivateSession");
		fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
		assertNotNull("Feedback response not found in database", fr);
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString()				
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=instructor1InCourse1",
						r.getDestinationWithParams());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("mcq");
		
		dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
		
		fq = fqDb.getFeedbackQuestion("MCQ Session", "FSQTT.idOfTypicalCourse1", 2);
		assertNotNull("Feedback question not found in database", fq);
		
		fr = dataBundle.feedbackResponses.get("response1ForQ2S1C1");
		fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
		assertNotNull("Feedback response not found in database", fr);
		
		instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect"			
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=FSQTT.idOfInstructor1OfCourse1",
						r.getDestinationWithParams());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("mcq, question skipped");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString()			
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=FSQTT.idOfInstructor1OfCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("msq");
		
		dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
		
		fq = fqDb.getFeedbackQuestion("MSQ Session", "FSQTT.idOfTypicalCourse1", 2);
		assertNotNull("Feedback question not found in database", fq);
		
		fr = dataBundle.feedbackResponses.get("response1ForQ2S2C1");
		fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
		assertNotNull("Feedback response not found in database", fr);
		
		instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect"			
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=FSQTT.idOfInstructor1OfCourse1",
						r.getDestinationWithParams());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("msq, question skipped");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString()			
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=FSQTT.idOfInstructor1OfCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("numscale");
		
		dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
		
		fq = fqDb.getFeedbackQuestion("NUMSCALE Session", "FSQTT.idOfTypicalCourse1", 2);
		assertNotNull("Feedback question not found in database", fq);
		
		fr = dataBundle.feedbackResponses.get("response1ForQ2S3C1");
		fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
		assertNotNull("Feedback response not found in database", fr);
		
		instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "3.5"			
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=FSQTT.idOfInstructor1OfCourse1",
						r.getDestinationWithParams());
		assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
		
		______TS("numscale, question skipped");
		
		submissionParams = new String[]{
				Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
				Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
		};
		
		a = getAction(submissionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertFalse(r.isError);
		assertEquals("All responses submitted succesfully!", r.getStatusMessage());
		assertEquals("/page/instructorHomePage?message=All+responses+submitted+succesfully%21"
						+ "&error=" + r.isError +"&user=FSQTT.idOfInstructor1OfCourse1",
						r.getDestinationWithParams());
		assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
	}
	
	@Test
	public void testGracePeriodAccessControl() throws Exception{
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("gracePeriodSession");
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
		dataBundle.feedbackSessions.put("gracePeriodSession", fs);
		
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.persistDataBundle(dataBundle);
		
		assertFalse(fs.isOpened());
		assertTrue(fs.isInGracePeriod());
		assertFalse(fs.isClosed());
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testGracePeriodExecuteAndPostProcess() throws Exception{
		FeedbackSessionsDb feedbackSessionDb = new FeedbackSessionsDb();
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Grace Period Session");
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
		gaeSimulation.loginAsInstructor(instructor.googleId);

		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
				};
		
		______TS("opened");
		
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		feedbackSessionDb.updateFeedbackSession(fs);
		
		assertTrue(fs.isOpened());
		assertFalse(fs.isInGracePeriod());

		InstructorFeedbackSubmissionEditSaveAction a = getAction(submissionParams);
		ActionResult r = a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_HOME_PAGE+"?message=All+responses+submitted+succesfully%21&error=false&user=instructor1InCourse1", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				r.getStatusMessage());
		assertFalse(r.isError);
		
		______TS("during grace period");
		
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
		feedbackSessionDb.updateFeedbackSession(fs);
	
		assertFalse(fs.isOpened());
		assertTrue(fs.isInGracePeriod());

		a = getAction(submissionParams);
		r = a.executeAndPostProcess();
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_HOME_PAGE+"?message=All+responses+submitted+succesfully%21&error=false&user=instructor1InCourse1", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				r.getStatusMessage());
		assertFalse(r.isError);
		
		______TS("after grace period");
		
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(-10);
		feedbackSessionDb.updateFeedbackSession(fs);
		
		assertFalse(fs.isOpened());
		assertFalse(fs.isInGracePeriod());
				
		String submissionFailMessage = new String();
		try{
			a = getAction(submissionParams);
			r = a.executeAndPostProcess();
		}
		catch(UnauthorizedAccessException e){
			submissionFailMessage = e.getMessage();
		}
		assertEquals("This feedback session is not open for submission.", submissionFailMessage);
	}
	
	private InstructorFeedbackSubmissionEditSaveAction getAction(String... params) throws Exception{
		return (InstructorFeedbackSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
	}
}
