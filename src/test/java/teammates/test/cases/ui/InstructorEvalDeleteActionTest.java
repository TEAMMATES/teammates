package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.ControllerServlet;

public class InstructorEvalDeleteActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = Common.PAGE_INSTRUCTOR_EVAL_DELETE;
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
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, evaluationInCourse1.courseId,
				Common.PARAM_EVALUATION_NAME, evaluationInCourse1.name 
		};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudents(submissionParams);
		verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
		verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
		
		//recreate the evaluation
		new EvaluationsDb().createEntity(evaluationInCourse1);
		verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
	

}
