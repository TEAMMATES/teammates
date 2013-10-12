package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Calendar;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.StudentsDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.StudentFeedbackSubmissionEditSaveAction;


public class StudentFeedbackSubmissionEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String studentId;
	String otherStudentId;
	String adminUserId;
	FeedbackSessionsDb feedbackSessionDb = new FeedbackSessionsDb();
	FeedbackSessionAttributes fs =  new FeedbackSessionAttributes();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("studentInGraceCourse");
		studentId = student1InCourse1.googleId;
		
		otherStudentId = dataBundle.students.get("student2InCourse1").googleId;
		
		adminUserId = "admin.user";

		fs = dataBundle.feedbackSessions.get("gracePeriod.session");
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
		dataBundle.feedbackSessions.put("gracePeriod.session", fs);

		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		assertEquals(false, fs.isOpened());
		assertEquals(true, fs.isInGracePeriod());
		
		
		FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1GracePeriodFeedback");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fr.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
				Const.ParamsNames.FROM_EMAIL, fr.giverEmail,
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fr.feedbackQuestionId,
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fr.feedbackQuestionType.toString(),
				Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT, fr.recipient,
				Const.ParamsNames.FEEDBACK_RESPONSE_TEXT, fr.answer.toString()
		};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		
		verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		verifyUnaccessibleForDifferentStudentOfTheSameCourses(submissionParams);
		
		verifyUnaccessibleForInstructors(submissionParams);
		verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
		
}

	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		gaeSimulation.loginAsStudent(studentId);

		String submissionFailMessage = new String();
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
				};
		
		______TS("opened");
		
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		feedbackSessionDb.updateFeedbackSession(fs);
		
		assertEquals(true, fs.isOpened());
		assertEquals(false, fs.isInGracePeriod());

		StudentFeedbackSubmissionEditSaveAction a = getAction(submissionParams);
		ActionResult r = a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.STUDENT_HOME_PAGE+"?message=All+responses+submitted+succesfully%21&error=false&user=studentInGraceCourse", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				r.getStatusMessage());
		assertEquals(false, r.isError);
		
		______TS("during grace period");
		
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
		feedbackSessionDb.updateFeedbackSession(fs);
	
		assertEquals(false, fs.isOpened());
		assertEquals(true, fs.isInGracePeriod());

		//refresh the action.
		a = getAction(submissionParams);
		r = a.executeAndPostProcess();
		assertEquals(
				Const.ActionURIs.STUDENT_HOME_PAGE+"?message=All+responses+submitted+succesfully%21&error=false&user=studentInGraceCourse", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				r.getStatusMessage());
		assertEquals(false, r.isError);
		
		______TS("after grace period");
		
		fs.endTime = TimeHelper.getDateOffsetToCurrentTime(-10);
		feedbackSessionDb.updateFeedbackSession(fs);
		
		assertEquals(false, fs.isOpened());
		assertEquals(false, fs.isInGracePeriod());
				
		try{
			r = a.executeAndPostProcess();
		}
		catch(UnauthorizedAccessException e){
			submissionFailMessage= e.getMessage();
		}
		assertEquals("This feedback session is not yet opened.", submissionFailMessage);
		
	}
	private StudentFeedbackSubmissionEditSaveAction getAction(String... params) throws Exception{
		return (StudentFeedbackSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
	}
	
}
