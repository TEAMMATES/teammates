package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.BaseTest2;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordCourseAddTest extends BaseTest2 {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	private static String COURSE_ID = "Test1001";
	private static String COURSE_NAME = "Random Course Name";
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseAddTest");
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(COURSE_ID);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordCourseAddTest ==========//");
	}
	
	@Test
	public void testCoordAddCourse() {
		testCoordAddCourseSuccessful();
		testCoordAddCourseWithDuplicateIDFailed();
		testCoordAddCourseWithDuplicateNameSuccessful();
		testCoordAddCourseWithInvalidInputsFailed();
		
//		testCreateCourseAllowedSymbolsIDSuccess();
//		testCreateCourseNonAllowedSymbolsIDFail();
	}
	
	/**
	* Successfully create a course with course ID containing permitted symbols
	*/
	public static void testCreateCourseAllowedSymbolsIDSuccess(){
		System.out.println("TestCoordCourse: TestCreatingCourseWithAllowedSymbolsInID");
	 		
		// Try adding course with ID containing '.'
		bi.addCourse(scn.course.courseId.replace('-', '.'), scn.course.courseName + " (.)");
		bi.gotoCourses();
		bi.verifyAddedCourse(scn.course.courseId.replace('-', '.'), scn.course.courseName + " (.)");
	
		// Try adding course with ID containing '_'
		bi.clickAndConfirmCourseDelete(scn.course.courseName + " (.)");
		bi.addCourse(scn.course.courseId.replace('-', '_'), scn.course.courseName + " (_)");
		bi.gotoCourses();
		bi.verifyAddedCourse(scn.course.courseId.replace('-', '_'), scn.course.courseName + " (_)");
		
		// Try adding course with ID containing '$'
		bi.clickAndConfirmCourseDelete(scn.course.courseName + " (_)");
		bi.addCourse(scn.course.courseId.replace('-', '$'), scn.course.courseName + " ($)");
		bi.gotoCourses();
		bi.verifyAddedCourse(scn.course.courseId.replace('-', '$'), scn.course.courseName + " ($)");
	}
		
	/**
	 * Fail to create a course with course ID containing non-permitted symbols
	 */
	public static void testCreateCourseNonAllowedSymbolsIDFail(){
		System.out.println("TestCoordCourse: TestCreatingCourseWithNonPermittedSymbolsInID");
		
		bi.addCourse(scn.course.courseId.replace("-", "!*}"), scn.course.courseName + " (!*})");
		assertEquals(true,bi.isElementPresent(bi.courseMessage));
	}
	
	/**
	 * Test: coordinator Add Course successfully
	 * Condition: 
	 * Action: enter courseID and course name
	 * Expectation: new course added in the course list, show successful message
	 * */
	public static void testCoordAddCourseSuccessful() {
		System.out.println("testCoordAddCourseSuccessful");
		bi.gotoCourses();//include verify Add Course page
		bi.addCourse(scn.course.courseId, scn.course.courseName);
		
		bi.clickCourseTab();
		bi.verifyAddedCourse(scn.course.courseId, scn.course.courseName);
	}
	
	/**
	 * Testing: duplicate course
	 * Condition: a course has been added
	 * Action: create another course with same ID/name
	 * Expectation: course not added. show error message
	 * 
	 * Relationship: 
	 * coordinator (1) - (m) course (1) - (m) evaluation
	 * 
	 * Scenario: 
	 * 1 duplicate course under same coordinator
	 * TODO: 2 duplicate course under different coordinators
	 **/
	public static void testCoordAddCourseWithDuplicateIDFailed() {
		System.out.println("testCoordAddCourseWithDuplicateIDFailed");
		
		bi.addCourse(scn.course.courseId, COURSE_NAME);
		assertEquals(bi.getElementText(bi.statusMessage), bi.MESSAGE_COURSE_EXISTS);
		
		//check course not added
		bi.clickCourseTab();
		bi.waitForElementPresent(bi.getCourseID(scn.course.courseId));
		assertFalse(bi.getElementText(bi.getCourseName(scn.course.courseId)).equals(COURSE_NAME));
	}
	
	public static void testCoordAddCourseWithDuplicateNameSuccessful() {
		System.out.println("testCoordAddCourseWithDuplicateNameSuccessful");

		bi.addCourse(COURSE_ID, scn.course.courseName);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_ADDED);
		assertEquals(bi.getElementText(bi.statusMessage), bi.MESSAGE_COURSE_ADDED);
		
		//check course added
		bi.clickCourseTab();
		bi.verifyAddedCourse(COURSE_ID, scn.course.courseName);
	}
	
	/**
	 * Test: CoordAddCourseWithInvalidInputsFailed
	 * Condition:
	 * Action: enter invalid courseID or course name
	 * Expectation: course not added, show error message
	 * */
	public void testCoordAddCourseWithInvalidInputsFailed() {
		String longCourseName = "HELLO WORLD HELLO WORLD HELLO WORLD HELLO WORLD ";//48 CHAR
		
		System.out.println("TestCoordCourse: Creating course with missing info.");
		// Trying adding course without ID
		bi.addCourse("", scn.course.courseName);
		assertEquals(bi.getElementText(bi.statusMessage), bi.ERROR_COURSE_MISSING_FIELD);
		// Adding course without name
		bi.addCourse(scn.course.courseId, "");
		assertEquals(bi.getElementText(bi.statusMessage), bi.ERROR_COURSE_MISSING_FIELD);
		

		System.out.println("TestCoordCourse: Creating course with invalid info.");
		//invalid ID
		
		//invalid name
		bi.addCourse(scn.course.courseId, longCourseName);
		assertEquals(bi.getElementText(bi.statusMessage), bi.ERROR_COURSE_LONG_COURSE_NAME);
		
	}
	
	//TODO: testCoordAddCourseWithRandomNameSuccessful()
	

}
