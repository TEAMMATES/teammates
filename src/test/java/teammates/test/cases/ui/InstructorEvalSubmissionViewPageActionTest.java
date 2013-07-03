package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Config;

public class InstructorEvalSubmissionViewPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Config.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		
		String[] submissionParams = new String[]{
				Config.PARAM_COURSE_ID, evaluationInCourse1.courseId,
				Config.PARAM_EVALUATION_NAME, evaluationInCourse1.name,
				Config.PARAM_STUDENT_EMAIL, student1InCourse1.email
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
	

}
