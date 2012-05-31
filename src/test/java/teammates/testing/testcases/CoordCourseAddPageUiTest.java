package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.exception.EntityDoesNotExistException;
import teammates.exception.NoAlertAppearException;
import teammates.jsp.Helper;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Coordinator;
import teammates.testing.object.Course;
import teammates.testing.script.ImportTestData;

import com.google.gson.Gson;

/**
 * Tests coordCourse.jsp from UI functionality and HTML test (exact and regex)
 * @author Aldrian Obaja
 *
 */
public class CoordCourseAddPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String localhostAddress = "localhost:8080/";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(ts.coordinator.username, ts.coordinator.password);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_COURSE);
	}

	@Test
	public void verifyAddCoursePage() throws EntityDoesNotExistException {
		TMAPI.deleteCoord(ts.coordinator.username);
		TMAPI.createCoord(ts.coordinator.username, ts.coordinator.name, ts.coordinator.email);
		
		bi.goToCourses();
		bi.verifyCurrentPageHTML("src/test/resources/pages/coordListCourseEmpty.html");

		ImportTestData.main(new String[]{});
		bi.goToCourses();
		
		bi.verifyCurrentPageHTML("src/test/resources/pages/coordListCourseByIDNew.html");
	
		bi.clickCoordCourseSortByNameButton();
		bi.verifyCurrentPageHTML("src/test/resources/pages/coordListCourseByNameNew.html");
		
		bi.clickCoordCourseSortByIdButton();
		bi.verifyCurrentPageHTML("src/test/resources/pages/coordListCourseByIDNew.html");
	}
	
	@Test
	public void testCoordAddUiPaths(){
		TMAPI.cleanupCourse(ts.validCourse.courseId);
		TMAPI.cleanupCourse(ts.courseWithSameNameDifferentId.courseId);
		
		// Course id only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.courseId;
		// Course name can be any character including special characters
		String courseName = ts.validCourse.courseName;
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseSuccessful");
		
		bi.addCourse(courseID, courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);
		
		//verify course is added
		bi.verifyCourseIsAdded(courseID, courseName);
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseWithInvalidInputsFailed");

		bi.addCourse("", courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(courseID, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(courseID+"!*}", courseName + " (!*})");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_INVALID_ID);

		////////////////////////////////////////////////////////////////
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

		////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseWithDuplicateIDFailed");
		
		bi.addCourse(courseID, "different course name");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_EXISTS);
		
		// Check that there is only one course with that ID and that it is still the old one
		assertTrue(bi.getCourseIDCount(courseID)==1);
		assertTrue(bi.getCourseName(courseID).equals(courseName));
		
		TMAPI.cleanupCourse(ts.validCourse.courseId);
		TMAPI.cleanupCourse(ts.courseWithSameNameDifferentId.courseId);
	}
	
	@Test
	public void testCoordCourseAddLinks(){
		TMAPI.cleanupCourse(ts.testCourse.courseId);
		
		String link;
		
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
		try{
			bi.clickCoordCourseDeleteAndCancel(ts.testCourse.courseId);
			bi.verifyCoordCoursesPage();
		} catch (NoAlertAppearException e){
			assertTrue("No alert box when clicking delete button at course page.",false);
		}
		
		TMAPI.cleanupCourse(ts.testCourse.courseId);
	}
	
	private static TestScenario loadTestScenario() throws JSONException {
		String testScenarioJsonFile = "src/test/resources/data/CoordCourseAddUITest.json";
		String jsonString = SharedLib.getFileContents(testScenarioJsonFile);
		TestScenario scn = (new Gson()).fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public Coordinator coordinator;
		public Course validCourse;
		public Course courseWithSameNameDifferentId;
		public Course testCourse;
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddUITest");
	}
}