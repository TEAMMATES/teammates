package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.EvaluationsDb;

public class InstructorEvalPublishActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_EVAL_PUBLISH;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
		
		makeEvaluationClosed(evaluationInCourse1);
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
				Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
		};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudents(submissionParams);
		verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
		verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
		
		makeEvaluationClosed(evaluationInCourse1); //we have revert to the closed state
		
		verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		
		//TODO: ensure cannot publish in when not publishable
	}

	private void makeEvaluationClosed(EvaluationAttributes eval) throws Exception {
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		eval.activated = true;
		eval.published = false;
		assertEquals(EvalStatus.CLOSED, eval.getStatus());
		new EvaluationsDb().updateEvaluation(eval);
		
	}
	

}
