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
import teammates.jdo.Coordinator;
import teammates.jdo.Course;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.script.ImportTestData;

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
		assertTrue(true);
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.deleteCoord(ts.coordinator.getGoogleID());
		TMAPI.createCoord(ts.coordinator.getGoogleID(), ts.coordinator.getName(), ts.coordinator.getEmail());
		
		bi.loginCoord(ts.coordinator.getGoogleID(), Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_COURSE);
	}

	@Test
	public void verifyAddCoursePage() throws EntityDoesNotExistException {
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseEmpty.html");

		ImportTestData.main(new String[]{});
		bi.goToCourses();
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseByIDNew.html");
	
		bi.clickCoordCourseSortByNameButton();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseByNameNew.html");
		
		bi.clickCoordCourseSortByIdButton();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseByIDNew.html");
	}
	
	@Test
	public void testCoordAddUiPaths(){
		TMAPI.deleteCourse(ts.validCourse.getID());
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.getID());
		
		// Course id only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.getID();
		// Course name can be any character including special characters
		String courseName = ts.validCourse.getName();
		
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
		
		TMAPI.deleteCourse(ts.validCourse.getID());
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.getID());
	}
	
	@Test
	public void testCoordCourseAddLinks(){
		TMAPI.deleteCourse(ts.testCourse.getID());
		
		String link;
		
		bi.addCourse(ts.testCourse.getID(), ts.testCourse.getName());
		
		// Check enroll link
		link = bi.getElementRelativeHref(bi.getCoordCourseEnrollLinkLocator(bi.getCourseRowID(ts.testCourse.getID())));
		assertTrue(link.equals(Helper.getCourseEnrollLink(ts.testCourse.getID())));
		
		// Check view details link
		link = bi.getElementRelativeHref(bi.getCoordCourseViewLinkLocator(bi.getCourseRowID(ts.testCourse.getID())));
		assertTrue(link.equals(Helper.getCourseViewLink(ts.testCourse.getID())));
		
		// Check delete link
		link = bi.getElementRelativeHref(bi.getCoordCourseDeleteLinkLocator(bi.getCourseRowID(ts.testCourse.getID())));
		assertTrue(link.equals(Helper.getCourseDeleteLink(ts.testCourse.getID(), Common.JSP_COORD_COURSE)));
		try{
			bi.clickCoordCourseDeleteAndCancel(ts.testCourse.getID());
			bi.verifyCoordCoursesPage();
		} catch (NoAlertAppearException e){
			assertTrue("No alert box when clicking delete button at course page.",false);
		}
		
		TMAPI.deleteCourse(ts.testCourse.getID());
	}
	
	private static TestScenario loadTestScenario() throws JSONException {
		String testScenarioJsonFile = "src/test/resources/data/CoordCourseAddUITest.json";
		String jsonString = SharedLib.getFileContents(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
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