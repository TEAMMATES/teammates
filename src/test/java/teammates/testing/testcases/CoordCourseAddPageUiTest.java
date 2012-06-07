package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.List;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.api.Common;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.exception.NoAlertAppearException;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.script.ImportTestData;

/**
 * Tests coordCourse.jsp from UI functionality and HTML test
 * @author Aldrian Obaja
 *
 */
public class CoordCourseAddPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		System.out.println("Recreating "+ts.coordinator.id);
		long start = System.currentTimeMillis();
		TMAPI.deleteCoord(ts.coordinator.id);
		TMAPI.createCoord(ts.coordinator);
		System.out.println("Finished recreating in "+(System.currentTimeMillis()-start)+" ms");
		
		bi.loginCoord(ts.coordinator.id, Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(appUrl+Common.JSP_COORD_COURSE);
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
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordListCourseEmpty.html");

		ImportTestData.main(new String[]{});
		bi.goToCourses();

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordListCourseByIdNew.html");

		bi.click(By.id("button_sortcoursename"));
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordListCourseByNameNew.html");
		
		bi.click(By.id("button_sortcourseid"));
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordListCourseByIdNew.html");
	}

	public void testCoordCourseAddUiPaths() throws Exception{
		TMAPI.deleteCourse(ts.validCourse.id);
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.id);
		
		// Course id only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseId = ts.validCourse.id;
		// Course name can be any character including special characters
		String courseName = ts.validCourse.name;
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseSuccessful");
		
		addCourse(courseId, courseName);

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseAddSuccessful.html");
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseWithInvalidInputsFailed");

		addCourse("", courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		addCourse(courseId, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		addCourse(courseId+"!*}", courseName + " (!*})");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_INVALID_ID);

		////////////////////////////////////////////////////////////////
		printTestCaseHeader("testMaxLengthOfInputFields");
		
		String shortCourseId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH);
		String longCourseId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH+1);
		
		String shortCourseName = Common.generateStringOfLength(Common.COURSE_NAME_MAX_LENGTH);
		String longCourseName = Common.generateStringOfLength(Common.COURSE_NAME_MAX_LENGTH+1);
		
		assertEquals(shortCourseId, fillCourseId(shortCourseId));
		assertEquals(longCourseId.substring(0, Common.COURSE_ID_MAX_LENGTH), fillCourseId(longCourseId));
		
		assertEquals(shortCourseName, fillCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, Common.COURSE_NAME_MAX_LENGTH), fillCourseName(longCourseName));

		////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseWithDuplicateIdFailed");
		
		addCourse(courseId, "different course name");
		
		long start = System.currentTimeMillis();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseAddDupIdFailed.html");
		System.out.println("Time to assert a page: "+(System.currentTimeMillis()-start)+" ms");
	}

	public void testCoordCourseAddLinks() throws Exception{
		TMAPI.deleteCourse(ts.testCourse.id);
		
//		String link;
		String courseId = ts.testCourse.id;
		String courseName = ts.testCourse.id;
		
		addCourse(courseId, courseName);
		int courseRowId = getCourseRowNumber(courseId);
		assertTrue(courseRowId!=-1);
		
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordCourseAddDeleteInit.html");
		
//		// Check enroll link
//		link = bi.getElementRelativeHref(getCoordCourseLinkLocator(courseRowId,"t_course_enroll"));
//		assertEquals(CoordCourseAddHelper.getCourseEnrollLink(courseId),link);
//		
//		// Check view details link
//		link = bi.getElementRelativeHref(getCoordCourseLinkLocator(courseRowId,"t_course_view"));
//		assertEquals(CoordCourseAddHelper.getCourseViewLink(courseId),link);
//		
//		// Check delete link
		By deleteLinkLocator = getCoordCourseLinkLocator(courseRowId,"t_course_delete");
//		link = bi.getElementRelativeHref(deleteLinkLocator);
//		assertEquals(CoordCourseAddHelper.getCourseDeleteLink(courseId, Common.JSP_COORD_COURSE),link);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordCourseAddDeleteInit.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course page.");
		}

		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordCourseAddDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course page.");
		}
	}
	
	private static TestScenario loadTestScenario() throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER + "/CoordCourseAddUiTest.json";
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public CoordData coordinator;
		public CourseData validCourse;
		public CourseData courseWithSameNameDifferentId;
		public CourseData testCourse;
	}
	
	private By getCoordCourseLinkLocator(int rowId, String linkClassName) {
		return By.xpath(String.format(
				"//div[@id='coordinatorCourseTable']"+
					"//table[@id='dataform']//tr[%d]//td[%d]"+
					"//a[@class='%s']",
				rowId+2,
				6,
				linkClassName)
			);
	}
	
	private String fillCourseId(String courseId){
		bi.fillString(By.id("courseid"),courseId);
		return bi.getElementValue(By.id("courseid"));
	}
	
	private String fillCourseName(String courseName){
		bi.fillString(By.id("coursename"),courseName);
		return bi.getElementValue(By.id("coursename"));
	}
	
	private void addCourse(String courseId, String courseName){
		fillCourseId(courseId);
		fillCourseName(courseName);
		bi.click(By.id("btnAddCourse"));
	}
	
	private List<WebElement> getCourses(){
		return bi.getDriver().findElements(By.className("courses_row"));
	}
	
	private int getCourseRowNumber(String courseId){
		List<WebElement> courses = getCourses();
		By courseIdLocator = By.xpath(".//td[1]");
		int id = 0;
		for(WebElement el: courses){
			if(el.findElement(courseIdLocator).getText().equals(courseId)) return id;
			id++;
		}
		return -1;
	}
	
//	private int getCourseIdCount(String courseId){
//		List<WebElement> courses = getCourses();
//		By courseIdLocator = By.xpath(".//td[1]");
//		int result = 0;
//		for(WebElement el: courses){
//			if(el.findElement(courseIdLocator).getText().equals(courseId)) result++;
//		}
//		return result;
//	}
//	
//	private String findCourseName(String courseId){
//		List<WebElement> courses = getCourses();
//		By courseIdLocator = By.xpath(".//td[1]");
//		for(WebElement el: courses){
//			String id = el.findElement(courseIdLocator).getText();
//			if(id.equals(courseId)){
//				return el.findElement(By.xpath(".//td[2]")).getText();
//			}
//		}
//		return "";
//	}
}