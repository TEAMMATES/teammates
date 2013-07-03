package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Constants;

public class StudentCourseDetailsPageActionTest extends BaseActionTest {

	DataBundle dataBundle;

	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Constants.ACTION_STUDENT_COURSE_DETAILS;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String iDOfCourseOfStudent = dataBundle.students.get("student1InCourse1").course;
		
		String[] submissionParams = new String[]{
					Constants.PARAM_COURSE_ID, iDOfCourseOfStudent
				};
		
		verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		
	}

	
}
