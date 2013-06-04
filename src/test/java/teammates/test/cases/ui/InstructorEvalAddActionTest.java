package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.InstructorEvalAddAction;

public class InstructorEvalAddActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String otherInstructorId;
	String studentId;
	String adminUserId;

	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorEvalAdd";
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		
		InstructorAttributes instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
		otherInstructorId = instructor1OfCourse2.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		studentId = student1InCourse1.googleId;
		
		adminUserId = "admin.user";
		
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		String[] submissionParams = 
				createParamsForTypicalEval(instructor1ofCourse1.courseId, "ieaat tca eval");
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructorId,submissionParams));
		
		loginUser(unregUserId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructorId,submissionParams));
		
		loginAsStudent(studentId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructorId,submissionParams));
		
		loginAsInstructor(instructorId);
		verifyCanAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(otherInstructorId,submissionParams));
		submissionParams = 
			createParamsForTypicalEval("idOfTypicalCourse2", "ieaat tca eval");
		verifyCannotAccess(submissionParams); //trying to create evaluation for someone else's course
		
		loginAsAdmin(adminUserId);
		//not checking for non-masquerade mode because admin may not be an instructor
		submissionParams = 
				createParamsForTypicalEval(instructor1ofCourse1.courseId, "ieaat tca eval2");
		verifyCanMasquerade(addUserIdToParams(instructorId,submissionParams));
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
	
	
	private InstructorEvalAddAction getAction(String... params) throws Exception{
			return (InstructorEvalAddAction) (super.getActionObject(params));
	}
	
	private String[] createParamsForTypicalEval(String courseId, String evalName){
		
		return new String[]{
				Common.PARAM_COURSE_ID, courseId,
				Common.PARAM_EVALUATION_NAME, evalName,
				Common.PARAM_EVALUATION_COMMENTSENABLED, "true",
				Common.PARAM_EVALUATION_START, "01/01/2015",
				Common.PARAM_EVALUATION_STARTTIME, "0",
				Common.PARAM_EVALUATION_DEADLINE, "01/01/2015",
				Common.PARAM_EVALUATION_DEADLINETIME, "0",
				Common.PARAM_EVALUATION_TIMEZONE, "0",
				Common.PARAM_EVALUATION_GRACEPERIOD, "0",
				Common.PARAM_EVALUATION_INSTRUCTIONS, "ins"
		};
	}
	
}
