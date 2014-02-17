package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentFeedbackSubmissionEditPageAction;

public class StudentFeedbackSubmissionEditPageActionTest extends BaseActionTest {
	
	DataBundle dataBundle;
		
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
		};
		
		this.verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
		gaeSimulation.loginAsStudent(student.googleId);
		
		______TS("not enough parameters");
		
		verifyAssumptionFailure();
		
		______TS("typical success case");
		
		FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
		
		String[] params = new String[]{
				Const.ParamsNames.COURSE_ID, session.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
				Const.ParamsNames.USER_ID, student.googleId
		};
		
		StudentFeedbackSubmissionEditPageAction a = getAction(params);
		ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT
				+ "?error=false"
				+ "&" + Const.ParamsNames.USER_ID + "=" + student.googleId,
				r.getDestinationWithParams());
		assertFalse(r.isError);
		assertEquals("", r.getStatusMessage());
		
		______TS("masquerade mode");
		
		gaeSimulation.loginAsAdmin("admin.user");
		
		a = getAction(params);
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT
				+ "?error=false"
				+ "&" + Const.ParamsNames.USER_ID + "=" + student.googleId,
				r.getDestinationWithParams());
		assertFalse(r.isError);
		assertEquals("", r.getStatusMessage());
		
		______TS("student has not joined course");
		
		gaeSimulation.loginAsStudent(student.googleId);
		
		student.googleId = null;
		new StudentsDb().updateStudent(student.course, student.email,
				student.name, student.team, student.email, student.googleId,
				student.comments);
		
		a = getAction(params);
		RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
				+ "?message=You+are+not+registered+in+the+course+" + session.courseId
				+ "&error=true&user=student1InCourse1",
				rr.getDestinationWithParams());
		assertTrue(rr.isError);
		assertEquals("You are not registered in the course " + session.courseId, rr.getStatusMessage());
	}
	
	private StudentFeedbackSubmissionEditPageAction getAction(String... params) throws Exception{
		return (StudentFeedbackSubmissionEditPageAction) (gaeSimulation.getActionObject(uri, params));
	}
}
