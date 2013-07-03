package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Config;

public class InstructorCourseEnrollSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Config.PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{
				Config.PARAM_COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId,
				Config.PARAM_STUDENTS_ENROLLMENT_INFO, ""
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}

}
