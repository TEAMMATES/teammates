package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.exception.NoAlertAppearException;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests coordCourse.jsp from UI functionality and HTML test
 * @author Aldrian Obaja
 *
 */
public class CoordCourseAddPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		
		System.out.println("Recreating "+ts.coordinator.id);
		long start = System.currentTimeMillis();
		BackDoor.deleteCoord(ts.coordinator.id);
		BackDoor.createCoord(ts.coordinator);
		System.out.println("Finished recreating in "+(System.currentTimeMillis()-start)+" ms");

		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(Config.inst().TEAMMATES_ADMIN_ACCOUNT, Config.inst().TEAMMATES_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_COURSE;
		link = Helper.addParam(link,Common.PARAM_USER_ID,ts.coordinator.id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddUITest");
	}

	@Test
	public void testCoordCourseAddPage() throws Exception{
		testCoordCourseAddHTML();
		testCoordCourseAddUiPaths();
		testCoordCourseAddLinks();
	}

	public void testCoordCourseAddHTML() throws Exception{
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseEmpty.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseEmpty.html");

		BackDoor.createCourse(ts.CS1101);
		BackDoor.createCourse(ts.CS2104);
		bi.goToCourses();

//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseById.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseById.html");

		bi.click(bi.coordCourseSortByNameButton);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseByName.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseByName.html");
		
		bi.click(bi.coordCourseSortByIdButton);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseById.html");
	}

	public void testCoordCourseAddUiPaths() throws Exception{
		String link = appUrl+Common.PAGE_COORD_COURSE;
		link = Helper.addParam(link,Common.PARAM_USER_ID,ts.coordinator.id);
		
		// Course id only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseId = ts.validCourse.id;
		// Course name can be any character including special characters
		String courseName = ts.validCourse.name;
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordaddCourseSuccessful");
		
		bi.addCourse(courseId, courseName);

//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseAddSuccessful.html");
		bi.verifyCurrentPageHTMLRegexWithRetry(Common.TEST_PAGES_FOLDER+"/coordCourseAddSuccessful.html", link);
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordaddCourseWithInvalidInputsFailed");

		bi.addCourse("", courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(courseId, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(courseId+"!*}", courseName + " (!*})");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_INVALID_ID);

		////////////////////////////////////////////////////////////////
		printTestCaseHeader("testMaxLengthOfInputFields");
		
		String shortCourseId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH);
		String longCourseId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH+1);
		
		String shortCourseName = Common.generateStringOfLength(Common.COURSE_NAME_MAX_LENGTH);
		String longCourseName = Common.generateStringOfLength(Common.COURSE_NAME_MAX_LENGTH+1);
		
		assertEquals(shortCourseId, bi.fillInCourseID(shortCourseId));
		assertEquals(longCourseId.substring(0, Common.COURSE_ID_MAX_LENGTH), bi.fillInCourseID(longCourseId));
		
		assertEquals(shortCourseName, bi.fillInCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, Common.COURSE_NAME_MAX_LENGTH), bi.fillInCourseName(longCourseName));

		////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordaddCourseWithDuplicateIdFailed");
		
		bi.addCourse(courseId, "different course name");
		
		long start = System.currentTimeMillis();
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseAddDupIdFailed.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseAddDupIdFailed.html");
		System.out.println("Time to assert a page: "+(System.currentTimeMillis()-start)+" ms");
	}

	public void testCoordCourseAddLinks() throws Exception{
		String courseId = ts.validCourse.id;
		
		int courseRowId = bi.getCourseRowID(courseId);
		assertTrue(courseRowId!=-1);
		
		// Check delete link
		By deleteLinkLocator = bi.getCoordCourseDeleteLinkLocator(courseRowId);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String course = BackDoor.getCourseAsJason(ts.validCourse.id);
			if(isNullJSON(course)) fail("Course was deleted when it's not supposed to be");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course page.");
		}

		try{
			bi.clickAndConfirm(deleteLinkLocator);
//			bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDeleteSuccessful.html");
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course page.");
		}
	}
	
	private static TestScenario loadTestScenario() throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER + "/coordCourseAddUiTest.json";
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public CoordData coordinator;
		public CourseData validCourse;
		public CourseData CS1101;
		public CourseData CS2104;
	}
}