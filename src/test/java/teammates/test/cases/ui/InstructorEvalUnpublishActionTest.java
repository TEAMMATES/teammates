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

public class InstructorEvalUnpublishActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_EVAL_UNPUBLISH;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
		makeEvaluationPublished(evaluationInCourse1);
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
				Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
		};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudents(submissionParams);
		verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
		verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
		
		makeEvaluationPublished(evaluationInCourse1); //we have revert to the PUBLISHED state
		
		verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
		
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		
		//TODO: ensure cannot unpublish if not published already
	}

	private void makeEvaluationPublished(EvaluationAttributes eval) throws Exception {
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		eval.activated = true;
		eval.published = true;
		assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
		new EvaluationsDb().updateEvaluation(eval);
		
	}
	

}
