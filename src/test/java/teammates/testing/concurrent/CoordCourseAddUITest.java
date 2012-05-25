package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Coordinator;
import teammates.testing.object.Course;

import com.google.gson.Gson;


public class CoordCourseAddUITest extends TestCase {
	private static BrowserInstance bi;
	private static TestScenario ts; 
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.deleteCourseByIdCascade(ts.courseWithSameNameDifferentId.courseId);
		
		bi.loginCoord(ts.coordinator.username, ts.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.deleteCourseByIdCascade(ts.courseWithSameNameDifferentId.courseId);
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddUITest");
	}
	
	
	@Test
	public  void testCoordAddCourseSuccessful() {
		printTestCaseHeader("testCoordAddCourseSuccessful");
		
		//make sure the course does not exist already
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		
		//course id: only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.courseId;
		//course name: any character including special characters
		String courseName = ts.validCourse.courseName;
		
		//add course:
		bi.clickCourseTab();
		bi.addCourse(courseID, courseName);
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_ADDED);
		
		//verify course is added:
		bi.clickCourseTab();
		bi.verifyCourseIsAdded(courseID, courseName);
		
	}
	
	
	
	@Test
	public void testCoordAddCourseWithInvalidInputsFailed() {
		printTestCaseHeader("testCoordAddCourseWithInvalidInputsFailed");

		// Trying to add a course without ID
		bi.clickCourseTab();
		bi.addCourse("", ts.validCourse.courseName);
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(ts.validCourse.courseId, "");
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(ts.validCourse.courseId+"!*}", ts.validCourse.courseName + " (!*})");
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_INVALID_ID);
	}
	
	@Test
	public  void testMaxLengthOfInputFields()	{
		printTestCaseHeader("testMaxLengthOfInputFields");
		
		bi.goToCourses();
		String shortCourseName = "This is a short name for course";
		assertTrue(shortCourseName.length()<Common.COURSE_NAME_MAX_LENGTH);
		
		String longCourseName = "This is a long name for course which exceeds " + Common.COURSE_NAME_MAX_LENGTH+ "char limit";
		assertTrue(longCourseName.length()>Common.COURSE_NAME_MAX_LENGTH);
		
		String shortCourseId = "CS2103-SEM1-AY11/12";
		assertTrue(shortCourseId.length()<Common.COURSE_ID_MAX_LENGTH);
		
		String longCourseId = "CS2103-SOFTWAREENGINEERING-SEM1-AY2011/2012";
		assertTrue(longCourseId.length()>Common.COURSE_ID_MAX_LENGTH);
		
		assertEquals(shortCourseId, bi.fillInCourseID(shortCourseId));
		assertEquals(longCourseId.substring(0, Common.COURSE_ID_MAX_LENGTH), bi.fillInCourseID(longCourseId));
		
		assertEquals(shortCourseName, bi.fillInCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, Common.COURSE_NAME_MAX_LENGTH), bi.fillInCourseName(longCourseName));
		
	}
	
	//TODO: 2 duplicate course under different coordinators
	@Test
	public  void testCoordAddCourseWithDuplicateIDFailed() {
		printTestCaseHeader("testCoordAddCourseWithDuplicateIDFailed");
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.createCourse(ts.validCourse, ts.coordinator.username);
		
		bi.clickCourseTab();
		bi.addCourse(ts.validCourse.courseId, "different course name");
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_EXISTS);
		
		//check course not added
		bi.clickCourseTab();
		bi.waitForElementPresent(bi.getCourseIDCell(ts.validCourse.courseId));
		assertTrue(bi.getCourseName(ts.validCourse.courseId).equals(ts.validCourse.courseName));
		//FIXME: this does not exclude the possibility that there are two courses in the list with same id
	
	}
	
	@Test
	public  void testCoordAddCourseWithDuplicateNameSuccessful() throws Exception {
		printTestCaseHeader("testCoordAddCourseWithDuplicateNameSuccessful");
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.createCourse(ts.validCourse, ts.coordinator.username);
		
		bi.clickCourseTab();
		bi.addCourse(ts.courseWithSameNameDifferentId.courseId, ts.validCourse.courseName);
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_ADDED);
		
		//check course added
		bi.clickCourseTab();
		bi.verifyCourseIsAdded(ts.courseWithSameNameDifferentId.courseId, ts.validCourse.courseName);

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


