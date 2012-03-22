package teammates.testing.concurrent;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * 
 * @author zds1989
 *
 */
public class CoordCourseAddUITest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	private static String COURSE_ID = "Test1001";
	private static String COURSE_NAME = "Random Course Name";
	private static int COURSE_NAME_MAX_LENGTH = 38;
	private static int COURSE_ID_MAX_LENGTH = 21;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseAddTest");
		bi = BrowserInstancePool.request();

		TMAPI.createCourse(scn.course, scn.coordinator.username);
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
		
		testCoordAddCourseWithInvalidInputsFailed();
		testMaxLengthOfInputFields();
		
		testCoordAddCourseWithDuplicateIDFailed();
		testCoordAddCourseWithDuplicateNameSuccessful();
		
	}

	
	/**
	 * Test: coordinator Add Course successfully
	 * Condition: 
	 * Action: enter courseID and course name
	 * Expectation: new course added in the course list, show successful message
	 * */
	public static void testCoordAddCourseSuccessful() {
		System.out.println("testCoordAddCourseSuccessful");
		
		//course id: only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = scn.course.courseId.replace("-", "_$");
		//course name: any character
		String courseName = scn.course.courseName + "$%^&*()";
		
		//add course:
		bi.gotoCourses();
		bi.addCourse(courseID, courseName);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_ADDED);
		
		//verify course added:
		bi.clickCourseTab();
		bi.verifyAddedCourse(courseID, courseName);
		
		//delete course:
		TMAPI.cleanupCourse(courseID);
	}
	
	
	
	/**
	 * Test: CoordAddCourseWithInvalidInputsFailed
	 * Condition:
	 * Action: enter invalid courseID or course name
	 * Expectation: course not added, show error message
	 * */
	public void testCoordAddCourseWithInvalidInputsFailed() {
		System.out.println("testCoordAddCourseWithInvalidInputsFailed");

		// Trying adding course without ID
		bi.addCourse("", scn.course.courseName);
		bi.waitForElementText(bi.statusMessage, bi.ERROR_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(scn.course.courseId, "");
		bi.waitForElementText(bi.statusMessage, bi.ERROR_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(scn.course.courseId.replace("-", "!*}"), scn.course.courseName + " (!*})");
		bi.waitForElementText(bi.statusMessage, bi.ERROR_COURSE_INVALID_ID);
	}
	
	public static void testMaxLengthOfInputFields()
	{
		System.out.println("testMaxLengthOfInputFields");
		bi.gotoCourses();
		String shortCourseName = "This is a short name for course";
		String longCourseName = "This is a long name for course which exceeds " + COURSE_NAME_MAX_LENGTH+ "char limit";
		String shortCourseId = "CS2103-SEM1-AY11/12";
		String longCourseId = "CS2103-SOFTWAREENGINEERING-SEM1-AY2011/2012";
		
		assertEquals(shortCourseId, bi.fillInCourseID(shortCourseId));
		assertEquals(longCourseId.substring(0, COURSE_ID_MAX_LENGTH), bi.fillInCourseID(longCourseId));
		
		assertEquals(shortCourseName, bi.fillInCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, COURSE_NAME_MAX_LENGTH), bi.fillInCourseName(longCourseName));
		
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
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_EXISTS);
		
		//check course not added
		bi.clickCourseTab();
		bi.waitForElementPresent(bi.getCourseID(scn.course.courseId));
		assertFalse(bi.getElementText(bi.getCourseName(scn.course.courseId)).equals(COURSE_NAME));
	}
	
	public static void testCoordAddCourseWithDuplicateNameSuccessful() {
		System.out.println("testCoordAddCourseWithDuplicateNameSuccessful");

		bi.addCourse(COURSE_ID, scn.course.courseName);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_ADDED);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_ADDED);
		
		//check course added
		bi.clickCourseTab();
		bi.verifyAddedCourse(COURSE_ID, scn.course.courseName);
	}
}
