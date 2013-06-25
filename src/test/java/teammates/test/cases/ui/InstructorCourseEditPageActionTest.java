package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.ui.controller.ControllerServlet;

public class InstructorCourseEditPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorCourseEdit";
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		InstructorAttributes instructorOfOtherCourse = dataBundle.instructors.get("instructor1OfCourse2");
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		String studentId = student1InCourse1.googleId;
		
		String adminUserId = "admin.user";
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, instructor1OfCourse1.courseId
		};
		
		______TS("not-logged-in users cannot access");
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("non-registered users cannot access");
		
		loginUser(unregUserId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("students cannot access");
		
		loginAsStudent(studentId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("instructor of the course can access");
		
		loginAsInstructor(instructor1OfCourse1.googleId);
		verifyCanAccess(submissionParams);
		
		______TS("instructor of others courses cannot access");
		
		verifyCannotMasquerade(addUserIdToParams(instructorOfOtherCourse.googleId,submissionParams));
		
		String[] submissionParamsForOtherCourse = new String[]{
				Common.PARAM_COURSE_ID, instructorOfOtherCourse.courseId
		};
		verifyCannotAccess(submissionParamsForOtherCourse);
		
		______TS("admin can masquerade");
		
		loginAsAdmin(adminUserId);
		//not checking for non-masquerade mode because admin may not be an instructor
		verifyCanMasquerade(addUserIdToParams(instructorOfOtherCourse.googleId,submissionParams));
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}

}
