package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.jsp.Helper;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Coordinator;
import teammates.testing.object.Course;

import com.google.gson.Gson;

public class CoordCourseAddPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String localhostAddress = "localhost:8080/";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.deleteCourseByIdCascade(ts.courseWithSameNameDifferentId.courseId);
		TMAPI.deleteCourseByIdCascade(ts.testCourse.courseId);
		
		bi.loginCoord(ts.coordinator.username, ts.coordinator.password);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_HOME);
	}
	
	//@Test
	public void testCoordAddUiFunctionality(){
		printTestCaseHeader("testCoordAddCourseSuccessful");
		
		//course id: only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.courseId;
		//course name: any character including special characters
		String courseName = ts.validCourse.courseName;
		
		//add course:
		bi.goToCourses();
		bi.addCourse(courseID, courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);
		
		//verify course is added:
		bi.goToCourses();
		bi.verifyCourseIsAdded(courseID, courseName);
		
		printTestCaseHeader("testCoordAddCourseWithInvalidInputsFailed");

		bi.addCourse("", ts.validCourse.courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(ts.validCourse.courseId, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(ts.validCourse.courseId+"!*}", ts.validCourse.courseName + " (!*})");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_INVALID_ID);

		//===================================================================================
		printTestCaseHeader("testMaxLengthOfInputFields");
		
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
		
		//===================================================================================
		
		printTestCaseHeader("testCoordAddCourseWithDuplicateIDFailed");
		
		bi.addCourse(ts.validCourse.courseId, "different course name");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_EXISTS);
		
		//check course not added
		bi.clickCourseTab();
		assertTrue(bi.getCourseName(ts.validCourse.courseId).equals(ts.validCourse.courseName));
		
		// Check that there is only one course with that ID
		assertTrue(bi.getCourseIDCount(ts.validCourse.courseId)==1);
	
		// Check that other coordinators cannot add the same courseID
		bi.logout();
		bi.loginCoord(ts.coordinatorDiff.username, ts.coordinatorDiff.password);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_HOME);
		
		bi.goToCourses();
		bi.addCourse(ts.validCourse.courseId, ts.validCourse.courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_EXISTS);
		
		bi.goToCourses();
		assertFalse(bi.isCoursePresent(ts.validCourse.courseId, ts.validCourse.courseName));
		
		// Go back to the initial coordinator
		bi.logout();
		bi.loginCoord(ts.coordinator.username, ts.coordinator.password);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_HOME);
		
		//===================================================================================
		
		printTestCaseHeader("testCoordAddCourseWithDuplicateNameSuccessful");
		
		bi.goToCourses();
		bi.addCourse(ts.courseWithSameNameDifferentId.courseId, ts.validCourse.courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);
		
		//check course added
		bi.verifyCourseIsAdded(ts.courseWithSameNameDifferentId.courseId, ts.validCourse.courseName);
	}
	
	@Test
	public void testCoordCourseAddLinks(){
		String link;
		bi.goToCourses();
		
		// Make sure the course is there
		bi.addCourse(ts.testCourse.courseId, ts.testCourse.courseName);
		
		// Check enroll link
		link = bi.getElementRelativeHref(bi.getCoordCourseEnrollLinkLocator(bi.getCourseRowID(ts.testCourse.courseId)));
		assertTrue(link.equals(Helper.getCourseEnrollLink(ts.testCourse.courseId)));
		
		// Check view details link
		link = bi.getElementRelativeHref(bi.getCoordCourseViewLinkLocator(bi.getCourseRowID(ts.testCourse.courseId)));
		assertTrue(link.equals(Helper.getCourseViewLink(ts.testCourse.courseId)));
		
		// Check delete link
		link = bi.getElementRelativeHref(bi.getCoordCourseDeleteLinkLocator(bi.getCourseRowID(ts.testCourse.courseId)));
		assertTrue(link.equals(Helper.getCourseDeleteLink(ts.testCourse.courseId, Common.JSP_COORD_COURSE)));
	}
	
	private static TestScenario loadTestScenario() throws JSONException {
		String testScenarioJsonFile = "src/test/resources/data/CoordCourseAddUITest.json";
		String jsonString = SharedLib.getFileContents(testScenarioJsonFile);
		TestScenario scn = (new Gson()).fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public Coordinator coordinator;
		public Coordinator coordinatorDiff;
		public Course validCourse;
		public Course courseWithSameNameDifferentId;
		public Course testCourse;
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.deleteCourseByIdCascade(ts.validCourse.courseId);
		TMAPI.deleteCourseByIdCascade(ts.courseWithSameNameDifferentId.courseId);
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddUITest");
	}
}