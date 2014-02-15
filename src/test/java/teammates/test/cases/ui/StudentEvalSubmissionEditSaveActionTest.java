package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.StudentEvalSubmissionEditSaveAction;

public class StudentEvalSubmissionEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String studentId;
	String otherStudentId;
	String adminUserId;
	EvaluationsDb evaluationsDb = new EvaluationsDb();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_SAVE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		studentId = student1InCourse1.googleId;
		
		otherStudentId = dataBundle.students.get("student2InCourse1").googleId;
		
		adminUserId = "admin.user";
		
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		assertEquals(EvalStatus.OPEN, eval.getStatus());
		SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, eval.courseId,
				Const.ParamsNames.EVALUATION_NAME, eval.name,
				Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
				Const.ParamsNames.TEAM_NAME, sub.team,
				Const.ParamsNames.TO_EMAIL, sub.reviewee,
				Const.ParamsNames.POINTS, sub.points+"",
				Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
				Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
			};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		
		verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		verifyUnaccessibleForDifferentStudentOfTheSameCourses(submissionParams);
		
		verifyUnaccessibleForInstructors(submissionParams);
		verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
	}

	@Test
	public void testValidation() throws Exception {
		gaeSimulation.loginAsStudent(studentId);
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		
		______TS("typical success case");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, eval.courseId,
				Const.ParamsNames.EVALUATION_NAME, eval.name,
				Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
				Const.ParamsNames.TEAM_NAME, sub.team,
				Const.ParamsNames.TO_EMAIL, sub.reviewee,
				Const.ParamsNames.POINTS, Integer.toString(sub.points),
				Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
				Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
		};
		StudentEvalSubmissionEditSaveAction a = getAction(submissionParams);
		ActionResult r = a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.STUDENT_HOME_PAGE+"?message=Your+submission+for+evaluation1+In+Course1+"
				+"in+course+"+eval.courseId +"+has+been+saved+successfully&error=false&user="+ 
						student1InCourse1.googleId,r.getDestinationWithParams());
		
		assertFalse(r.isError);
		
		______TS("empty point field");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, eval.courseId,
				Const.ParamsNames.EVALUATION_NAME, eval.name,
				Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
				Const.ParamsNames.TEAM_NAME, sub.team,
				Const.ParamsNames.TO_EMAIL, sub.reviewee,
				Const.ParamsNames.POINTS, "",
				Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
				Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
		};
		a = getAction(submissionParams);
		r = a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE
						+ "?courseid=" + eval.courseId
						+ "&evaluationname=evaluation1+In+Course1"
						+ "&user=" + student1InCourse1.googleId
						+ "&message=Please+give+contribution+scale+to+everyone&error=true",
				r.getDestinationWithParams());
		
		assertTrue(r.isError);
		
		______TS("multiple empty point field");
		
		SubmissionAttributes sub2 = dataBundle.submissions.get("submissionFromS1C1ToS1C1");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, eval.courseId,
				Const.ParamsNames.EVALUATION_NAME, eval.name,
				Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
				Const.ParamsNames.TEAM_NAME, sub.team,
				Const.ParamsNames.TO_EMAIL, sub.reviewee,
				Const.ParamsNames.TO_EMAIL, sub2.reviewee,
				Const.ParamsNames.POINTS, "",
				Const.ParamsNames.POINTS, "",
				Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
				Const.ParamsNames.JUSTIFICATION, sub2.justification.toString(),
				Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString(),
				Const.ParamsNames.COMMENTS, sub2.p2pFeedback.toString()
		};
		a = getAction(submissionParams);
		r = a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE
						+ "?courseid=" + eval.courseId
						+ "&evaluationname=evaluation1+In+Course1"
						+ "&user=" + student1InCourse1.googleId
						+ "&message=Please+give+contribution+scale+to+everyone&error=true",
				r.getDestinationWithParams());
		
		assertTrue(r.isError);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		//TODO: ensure uneditable if not OPEN
		gaeSimulation.loginAsStudent(studentId);
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		
		String submissionFailMessage = new String();
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, eval.courseId,
				Const.ParamsNames.EVALUATION_NAME, eval.name,
				Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
				Const.ParamsNames.TEAM_NAME, sub.team,
				Const.ParamsNames.TO_EMAIL, sub.reviewee,
				Const.ParamsNames.POINTS, sub.points+"",
				Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
				Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
				
				};
		StudentEvalSubmissionEditSaveAction a = getAction(submissionParams);
		______TS("opened");
		
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		evaluationsDb.updateEvaluation(eval);
		
		assertEquals(EvalStatus.OPEN, eval.getStatus());

		ActionResult r = a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.STUDENT_HOME_PAGE+"?message=Your+submission+for+evaluation1+In+Course1+"
				+"in+course+"+eval.courseId +"+has+been+saved+successfully&error=false&user="+ 
						student1InCourse1.googleId,r.getDestinationWithParams());
		
		assertFalse(r.isError);
		
		______TS("closed");
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-10);
		
		evaluationsDb.updateEvaluation(eval);
		assertEquals(EvalStatus.CLOSED, eval.getStatus());
			
		try{
			r = a.executeAndPostProcess();
		}
		catch(UnauthorizedAccessException e){
			submissionFailMessage = e.getMessage();
		}
		assertEquals(Const.Tooltips.EVALUATION_STATUS_CLOSED, submissionFailMessage);
		
	
		______TS("published");
	
		eval.published = true;
		assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		try{
			r = a.executeAndPostProcess();
		}
		catch(UnauthorizedAccessException e){
			submissionFailMessage = e.getMessage();
		}
		assertEquals(Const.Tooltips.EVALUATION_STATUS_PUBLISHED, submissionFailMessage);
	
		______TS("awaiting");
	
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
		eval.setDerivedAttributes();
		assertEquals(EvalStatus.AWAITING, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		try{
			r = a.executeAndPostProcess();
		}
		catch(UnauthorizedAccessException e){
			submissionFailMessage = e.getMessage();
		}
		assertEquals(Const.Tooltips.EVALUATION_STATUS_AWAITING, submissionFailMessage);
	}
	
	private StudentEvalSubmissionEditSaveAction getAction(String... params) throws Exception{
		return (StudentEvalSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
	}
	
}
