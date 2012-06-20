package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;


public class CoordCourseAddUITest extends TestCase {
	private static BrowserInstance bi;
	private static TestScenario ts; 
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.deleteCourse(ts.validCourse.id);
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.id);
		
		bi.loginCoord(ts.coordinator.id, Config.inst().TEAMMATES_APP_PASSWORD);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.deleteCourse(ts.validCourse.id);
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.id);
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddUITest");
	}
	
	
	@Test
	public  void testCoordAddCourseSuccessful() {
		printTestCaseHeader("testCoordAddCourseSuccessful");
		
		//make sure the course does not exist already
		TMAPI.deleteCourse(ts.validCourse.id);
		
		//course id: only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.id;
		//course name: any character including special characters
		String courseName = ts.validCourse.name;
		
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
		bi.addCourse("", ts.validCourse.name);
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(ts.validCourse.id, "");
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(ts.validCourse.id+"!*}", ts.validCourse.name + " (!*})");
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
	
	@Test
	public  void testCoordAddCourseWithDuplicateIDFailed() {
		printTestCaseHeader("testCoordAddCourseWithDuplicateIDFailed");
		
		TMAPI.deleteCourse(ts.validCourse.id);
		TMAPI.createCourse(ts.validCourse);
		
		bi.clickCourseTab();
		bi.addCourse(ts.validCourse.id, "different course name");
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_EXISTS);
		
		//check course not added
		bi.clickCourseTab();
		bi.waitForElementPresent(bi.getCourseIDCell(ts.validCourse.id));
		
		// Check that there is only one course with that ID and that it is still the old one
		assertTrue(bi.getCourseIDCount(ts.validCourse.id)==1);
		assertEquals(ts.validCourse.name,bi.getCourseName(ts.validCourse.id));
	}
	
	@Test
	public  void testCoordAddCourseWithDuplicateNameSuccessful() throws Exception {
		printTestCaseHeader("testCoordAddCourseWithDuplicateNameSuccessful");
		
		TMAPI.deleteCourse(ts.validCourse.id);
		TMAPI.createCourse(ts.validCourse);
		
		bi.clickCourseTab();
		bi.addCourse(ts.courseWithSameNameDifferentId.id, ts.validCourse.name);
		bi.waitForTextInElement(bi.statusMessage, Common.MESSAGE_COURSE_ADDED);
		
		//check course added
		bi.clickCourseTab();
		bi.verifyCourseIsAdded(ts.courseWithSameNameDifferentId.id, ts.validCourse.name);

	}
	
	private static TestScenario loadTestScenario() throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER+"/CoordCourseAddUITest.json";
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public CoordData coordinator;
		public CourseData validCourse;
		public CourseData courseWithSameNameDifferentId;
	}
}


