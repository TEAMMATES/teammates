package teammates.testing.concurrent;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Coordinator;
import teammates.testing.object.Course;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;
import teammates.testing.object.Team;
import teammates.testing.object.TeamFormingSession;


public class CoordCourseAddUITest extends TestCase {
	static BrowserInstance bi;
	static TestScenario ts; 
	
	private static int COURSE_NAME_MAX_LENGTH = 38;
	private static int COURSE_ID_MAX_LENGTH = 21;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseAddTest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.request();
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.deleteCourseByIdCascade(ts.courseWithSameNameDifferentId.courseId);
		
		bi.coordinatorLogin(ts.coordinator.username, ts.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.deleteCourseByIdCascade(ts.courseWithSameNameDifferentId.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordCourseAddTest ==========//");
	}
	
	
	/**
	 * Test: coordinator Add Course successfully
	 * Condition: 
	 * Action: enter valid courseID and course name
	 * Expectation: new course added in the course list, show successful message
	 * */
	@Test
	public  void testCoordAddCourseSuccessful() {
		System.out.println("testCoordAddCourseSuccessful");
		
		//course id: only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.courseId;
		//course name: any character including special characters
		String courseName = ts.validCourse.courseName;
		
		//add course:
		bi.gotoCourses();
		bi.addCourse(courseID, courseName);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_ADDED);
		
		//verify course added:
		bi.clickCourseTab();
		bi.verifyAddedCourse(courseID, courseName);
		
		//delete course:
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
	}
	
	
	
	/**
	 * Test: CoordAddCourseWithInvalidInputsFailed
	 * Condition:
	 * Action: enter invalid courseID or course name
	 * Expectation: course not added, show error message
	 * */
	@Test
	public void testCoordAddCourseWithInvalidInputsFailed() {
		System.out.println("testCoordAddCourseWithInvalidInputsFailed");

		// Trying adding course without ID
		bi.addCourse("", ts.validCourse.courseName);
		bi.waitForElementText(bi.statusMessage, bi.ERROR_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(ts.validCourse.courseId, "");
		bi.waitForElementText(bi.statusMessage, bi.ERROR_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(ts.validCourse.courseId+"!*}", ts.validCourse.courseName + " (!*})");
		bi.waitForElementText(bi.statusMessage, bi.ERROR_COURSE_INVALID_ID);
	}
	
	@Test
	public  void testMaxLengthOfInputFields()	{
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
	@Test
	public  void testCoordAddCourseWithDuplicateIDFailed() {
		System.out.println("testCoordAddCourseWithDuplicateIDFailed");
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.createCourse(ts.validCourse);
		bi.addCourse(ts.validCourse.courseId, "different course name");
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_EXISTS);
		
		//check course not added
		bi.clickCourseTab();
		bi.waitForElementPresent(bi.getCourseID(ts.validCourse.courseId));
		assertTrue(bi.getElementText(bi.getCourseName(ts.validCourse.courseId)).equals(ts.validCourse.courseName));
	}
	@Test
	public  void testCoordAddCourseWithDuplicateNameSuccessful() {
		System.out.println("testCoordAddCourseWithDuplicateNameSuccessful");
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.createCourse(ts.validCourse);
		
		bi.addCourse(ts.courseWithSameNameDifferentId.courseId, ts.validCourse.courseName);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_ADDED);
		
		//check course added
		bi.clickCourseTab();
		bi.verifyAddedCourse(ts.courseWithSameNameDifferentId.courseId, ts.validCourse.courseName);
	}
	
	private static TestScenario loadTestScenario() throws JSONException {
		String testScenarioJsonFile = "target/test-classes/data/CoordCourseAddUITest.json";
		String jsonString = SharedLib.getFileContents(testScenarioJsonFile);
		TestScenario scn = (new Gson()).fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public Coordinator coordinator;
		public Course validCourse;
		public Course courseWithSameNameDifferentId;
	}
}


