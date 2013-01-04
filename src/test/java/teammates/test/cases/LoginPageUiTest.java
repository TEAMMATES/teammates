package teammates.test.cases;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.StudentData;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class LoginPageUiTest extends BaseTestCase {
	
	private static BrowserInstance bi;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();
		

	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		// Used in testInstructorLogin
		BackDoor.deleteCourse("new.test.course");
		
		// Used in testStudentLogin
		BackDoor.deleteCourse("lput.tsl.course");
		
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testInstructorLogin() {
		// create a fresh course
		CourseData testCourse = new CourseData("new.test.course", "New Test Course101", CourseData.INSTRUCTOR_FIELD_DEPRECATED);
		BackDoor.deleteCourse(testCourse.id);
		String backDoorOperationStatus = BackDoor.createCourse(testCourse);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		// create a fresh instructor in datastore
		InstructorData testInstructor = new InstructorData();
		testInstructor.googleId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
		testInstructor.courseId = testCourse.id;
		BackDoor.deleteInstructor(testInstructor.googleId);
		backDoorOperationStatus = BackDoor.createInstructor(testInstructor);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//try to login
		bi.logout();
		bi.goToUrl(appUrl);
		bi.click(bi.instructorLoginButton);
		assertTrue(bi.isLocalLoginPage()||bi.isGoogleLoginPage());
		String instructorPassword = TestProperties.inst().TEST_INSTRUCTOR_PASSWORD;
		boolean isAdmin = false;
		bi.login(testInstructor.googleId, instructorPassword, isAdmin);
		assertContainsRegex(testInstructor.googleId+"{*}Instructor Home{*}", bi.getCurrentPageSource());
	}
	
	@Test
	public void testStudentLogin(){
		//create a course for the new student
		CourseData testCourse = new CourseData();
		testCourse.id = "lput.tsl.course";
		testCourse.name = "test.course.fornewstudent";
		testCourse.instructor = "test.course.nonexistentinstructor";
		BackDoor.deleteCourse(testCourse.id);
		String backDoorOperationStatus = BackDoor.createCourse(testCourse);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//create a fresh student in datastore
		StudentData testStudent = new StudentData();
		testStudent.id = TestProperties.inst().TEST_STUDENT_ACCOUNT;
		testStudent.name = "Test student";
		testStudent.email = "test@student";
		testStudent.course = testCourse.id;
		BackDoor.deleteStudent(testStudent.course, testStudent.email);
		backDoorOperationStatus = BackDoor.createStudent(testStudent);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//try to login
		bi.logout();
		bi.goToUrl(appUrl);
		bi.click(bi.studentLoginButton);
		assertTrue(bi.isLocalLoginPage()||bi.isGoogleLoginPage());
		String studentPassword = TestProperties.inst().TEST_STUDENT_PASSWORD;
		boolean isAdmin = false;
		bi.login(testStudent.id, studentPassword, isAdmin);
		assertContainsRegex(testStudent.id+"{*}Student Home{*}", bi.getCurrentPageSource());
	}

}
