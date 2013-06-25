package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.EvaluationsLogic;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.common.EvaluationAttributesTest;
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseDetailsPageAction;
import teammates.ui.controller.StudentHomePageAction;
import teammates.ui.controller.StudentHomePageData;

public class StudentCourseDetailsPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String studentId;
	String studentInOtherCourse;
	String adminUserId;

	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/studentCourseDetails";
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		studentId = student1InCourse1.googleId;
		
		studentInOtherCourse = dataBundle.students.get("student1InCourse2").googleId;
		
		adminUserId = "admin.user";
		
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String iDOfCourseOfStudent = dataBundle.students.get("student1InCourse1").course;
		
		String[] submissionParams = new String[]{Common.PARAM_COURSE_ID, iDOfCourseOfStudent};
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		loginUser(unregUserId);
		verifyRedirectTo(Common.PAGE_STUDENT_HOME, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		loginAsStudent(studentId);
		verifyCanAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentInOtherCourse,submissionParams));
		
		loginAsStudent(studentInOtherCourse);
		verifyRedirectTo(Common.PAGE_STUDENT_HOME, submissionParams);
		
		loginAsInstructor(instructorId);
		verifyRedirectTo(Common.PAGE_STUDENT_HOME, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		loginAsAdmin(adminUserId);
		//admin cannot access this page directly because admin is not a student
		verifyCanMasquerade(addUserIdToParams(studentId,submissionParams));
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		
	}

	private StudentCourseDetailsPageAction getAction(String... params) throws Exception{
			return (StudentCourseDetailsPageAction) (super.getActionObject(params));
	}
	
}
