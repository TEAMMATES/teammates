package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.TimeHelper;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.ControllerServlet;

public class InstructorEvalPublishActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = Common.PAGE_INSTRUCTOR_EVAL_PUBLISH;
		sr.registerServlet(URI, ControllerServlet.class.getName());
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
				Common.PARAM_COURSE_ID, evaluationInCourse1.courseId,
				Common.PARAM_EVALUATION_NAME, evaluationInCourse1.name 
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
