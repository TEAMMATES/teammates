package teammates.test.cases;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
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
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testCoordLogin(){
		//create a fresh coordinator in datastore
		CoordData testCoord = new CoordData();
		testCoord.id = TestProperties.inst().TEST_COORD_ACCOUNT;
		testCoord.name = "Test Coord";
		testCoord.email = "test@coord";
		BackDoor.deleteCoord(testCoord.id);
		String backDoorOperationStatus = BackDoor.createCoord(testCoord);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//try to login
		bi.logout();
		bi.goToUrl(appUrl);
		bi.click(bi.coordLoginButton);
		assertTrue(bi.isLocalLoginPage()||bi.isGoogleLoginPage());
		String coordPassword = TestProperties.inst().TEST_COORD_PASSWORD;
		boolean isAdmin = false;
		bi.login(testCoord.id, coordPassword, isAdmin);
		assertContainsRegex(testCoord.id+"{*}Coordinator Home{*}", bi.getCurrentPageSource());
	}
	
	@Test
	public void testStudentLogin(){
		//create a course for the new student
		CourseData testCourse = new CourseData();
		testCourse.id = "lput.tsl.course";
		testCourse.name = "test.course.fornewstudent";
		testCourse.coord = "test.course.nonexistentcoord";
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
