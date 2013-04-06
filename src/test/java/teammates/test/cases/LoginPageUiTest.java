package teammates.test.cases;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
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
		
		// Delete accounts used for testing
		BackDoor.deleteAccount(TestProperties.inst().TEST_STUDENT_ACCOUNT);
		BackDoor.deleteAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
		
		
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testInstructorLogin() {
		// Create an account for the instructor
		AccountData testInstructor = new AccountData(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT, "Test Course Creator", true, "instructor@testCourse.com", "National University of Singapore");
		String backDoorOperationStatus = BackDoor.createAccount(testInstructor);
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
		
		//recreate instructor account 
		BackDoor.deleteAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
		AccountData testCourseCreator = new AccountData(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT, "Test Course Creator", true, "instructor@testCourse.com", "National University of Singapore");
		String backDoorOperationStatus = BackDoor.createAccount(testCourseCreator);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		
		//recreate the student account 
		BackDoor.deleteAccount(TestProperties.inst().TEST_STUDENT_ACCOUNT);
		AccountData testStudentAccount = new AccountData(TestProperties.inst().TEST_STUDENT_ACCOUNT, "Emily Tmms", false, "emily.tmms@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//create a course for the new student
		CourseData testCourse = new CourseData();
		testCourse.id = "lput.tsl.course";
		testCourse.name = "test.course.fornewstudent";
		BackDoor.deleteCourse(testCourse.id);
		backDoorOperationStatus = BackDoor.createCourse(testCourse);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//enroll the student in the course
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
