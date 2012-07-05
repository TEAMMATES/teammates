package teammates.testdriver.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.CourseData;
import teammates.testdriver.lib.BackDoor;
import teammates.testdriver.lib.BrowserInstance;
import teammates.testdriver.lib.BrowserInstancePool;
import teammates.testdriver.lib.NoAlertAppearException;
import teammates.testdriver.lib.TestProperties;
import teammates.ui.Helper;

/**
 * Tests coordCourse.jsp from UI functionality and HTML test
 */
public class CoordCourseAddPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		ts = loadTestScenario(Common.TEST_DATA_FOLDER + "/coordCourseAddUiTest.json");
		BackDoor.deleteCoord(ts.coordinator.id);
		String backDoorOperationStatus = BackDoor.createCoord(ts.coordinator);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_COURSE;
		link = Helper.addParam(link,Common.PARAM_USER_ID,ts.coordinator.id);
		bi.goToUrl(link);
	}



	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testCoordCourseAddPage() throws Exception{
		testCoordCourseAddHTML();
		testCoordCourseAddUiPaths();
		testCoordCourseAddLinks();
	}

	public void testCoordCourseAddHTML() throws Exception{
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseEmpty.html");

		BackDoor.createCourse(ts.CS1101);
		BackDoor.createCourse(ts.CS2104);
		bi.clickCourseTab();

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseById.html");

		bi.click(bi.coordCourseSortByNameButton);
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
		
		______TS("testCoordaddCourseSuccessful");
		
		bi.addCourse(courseId, courseName);
		
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);

		bi.verifyCurrentPageHTMLRegexWithRetry(Common.TEST_PAGES_FOLDER+"/coordCourseAddSuccessful.html", link);
		
		______TS("testCoordaddCourseWithInvalidInputsFailed");

		bi.addCourse("", courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		bi.addCourse(courseId, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(courseId+"!*}", courseName + " (!*})");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_INVALID_ID);

		______TS("testMaxLengthOfInputFields");
		
		String shortCourseId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH);
		String longCourseId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH+1);
		
		String shortCourseName = Common.generateStringOfLength(Common.COURSE_NAME_MAX_LENGTH);
		String longCourseName = Common.generateStringOfLength(Common.COURSE_NAME_MAX_LENGTH+1);
		
		assertEquals(shortCourseId, bi.fillInCourseID(shortCourseId));
		assertEquals(longCourseId.substring(0, Common.COURSE_ID_MAX_LENGTH), bi.fillInCourseID(longCourseId));
		
		assertEquals(shortCourseName, bi.fillInCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, Common.COURSE_NAME_MAX_LENGTH), bi.fillInCourseName(longCourseName));

		______TS("testCoordaddCourseWithDuplicateIdFailed");
		
		bi.addCourse(courseId, "different course name");
		
		start = System.currentTimeMillis();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseAddDupIdFailed.html");
		print("Time to assert page: "+(System.currentTimeMillis()-start)+" ms");
	}

	public void testCoordCourseAddLinks() throws Exception{
		
		String courseId = ts.validCourse.id;
		
		int courseRowId = bi.getCourseRowID(courseId);
		assertTrue(courseRowId!=-1);
		
		// Check delete link
		By deleteLinkLocator = bi.getCoordCourseDeleteLinkLocator(courseRowId);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String course = BackDoor.getCourseAsJson(ts.validCourse.id);
			if(isNullJSON(course)) fail("Course was deleted when it's not supposed to be");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course page.");
		}

		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course page.");
		}
	}
	
	private static TestScenario loadTestScenario(String scenarioJsonFile) throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = scenarioJsonFile;
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