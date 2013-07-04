package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

public class InstructorEvalSubmissionEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SAVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
				
		String[] submissionParams = {
				Const.ParamsNames.COURSE_ID, eval.courseId,
				Const.ParamsNames.EVALUATION_NAME, eval.name,
				Const.ParamsNames.FROM_EMAIL, student.email
			};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}

	
}
