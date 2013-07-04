package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.EvaluationsDb;

public class StudentEvalSubmissionEditPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	EvaluationsDb evaluationsDb = new EvaluationsDb();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		______TS("OPEN evaluation");
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		assertEquals(EvalStatus.OPEN, eval.getStatus());
		checkAccessControlForEval(eval, true);
		
		______TS("CLOSED evaluation");
		
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.CLOSED, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		checkAccessControlForEval(eval, true);
		
		______TS("PUBLISHED evaluation");
		
		eval.published = true;
		assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		checkAccessControlForEval(eval, true);
		
		______TS("AWAITING evaluation");
		
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
		eval.setDerivedAttributes();
		assertEquals(EvalStatus.AWAITING, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		//We allow accessing it in AWAITING state because it is hard for students to do if 
		//  they don't know the evaluation name. In any case there's no harm if they did it.
		checkAccessControlForEval(eval, true);
		
	}

	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		
	}

	private void checkAccessControlForEval(EvaluationAttributes eval, boolean isEditableForStudent)
			throws Exception {

		String courseId = eval.courseId;
		String evalName = eval.name;
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		String studentId = student1InCourse1.googleId;
		
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.EVALUATION_NAME, evalName};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		
		gaeSimulation.loginUser("unreg.user");
		//if the user is not a student of the course, we redirect to home page.
		verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		if(isEditableForStudent){
			verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		}else {
			verifyUnaccessibleForStudents(submissionParams);
		}
		
		
		gaeSimulation.loginAsInstructor(instructorId);
		//if the user is not a student of the course, we redirect to home page.
		verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
	}
	
}
