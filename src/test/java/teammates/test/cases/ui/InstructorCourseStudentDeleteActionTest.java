package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.CoursesLogic;
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.InstructorCourseDeleteAction;
import teammates.ui.controller.InstructorCoursePageAction;
import teammates.ui.controller.InstructorCoursePageData;
import teammates.ui.controller.InstructorCourseStudentDeleteAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseStudentDeleteActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorCourseStudentDelete";
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String	unregUserId = "unreg.user";
		String 	adminUserId = "admin.user";
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, instructor1OfCourse1.courseId,
				Common.PARAM_STUDENT_EMAIL, student1InCourse1.email 
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
		
		loginAsStudent(student1InCourse1.googleId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("instructor of the course can access");
		
		loginAsInstructor(instructor1OfCourse1.googleId);
		verifyCanAccess(submissionParams);
		
		______TS("instructor of others courses cannot access");
		
		InstructorAttributes instructorOfOtherCourse = dataBundle.instructors.get("instructor1OfCourse2");
		verifyCannotMasquerade(addUserIdToParams(instructorOfOtherCourse.googleId,submissionParams));
		
		//cannot delete students in another course
		StudentAttributes studentInOtherCourse = dataBundle.students.get("student1InCourse2");
		String[] submissionParamsForOtherCourse = new String[]{
				Common.PARAM_COURSE_ID, instructorOfOtherCourse.courseId,
				Common.PARAM_STUDENT_EMAIL, studentInOtherCourse.email 
		};
		verifyCannotAccess(submissionParamsForOtherCourse);
		
		______TS("admin can masquerade");
		
		loginAsAdmin(adminUserId);
		//not checking for non-masquerade mode because admin may not be an instructor
		verifyCanMasquerade(addUserIdToParams(instructorOfOtherCourse.googleId,submissionParamsForOtherCourse));
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
	

}
