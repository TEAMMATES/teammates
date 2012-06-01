package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.Common;
import teammates.datatransfer.*;
import teammates.exception.NoAlertAppearException;
import teammates.jdo.Coordinator;
import teammates.jdo.Course;
import teammates.jsp.Helper;
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
	
	private static String localhostAddress = "localhost:8080/";
	
	private static TestScenario loadTestScenario() throws JSONException {
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER + "CoordCourseAddUITest.json";
		String jsonString = Common.getFileContents(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public CoordData coordinator;
		public Course validCourse;
		public Course courseWithSameNameDifferentId;
		public Course testCourse;
	}
	
	private By getCoordCourseLinkLocator(int rowID, String linkClassName) {
		return By.xpath(String.format(
				"//div[@id='coordinatorCourseTable']"+
					"//table[@id='dataform']//tr[%d]//td[%d]"+
					"//a[@class='%s']",
				rowID+2,
				6,
				linkClassName)
			);
	}
	
	private String fillCourseID(String courseID){
		bi.fillString(By.id("courseid"),courseID);
		return bi.getElementValue(By.id("courseid"));
	}
	
	private String fillCourseName(String courseName){
		bi.fillString(By.id("coursename"),courseName);
		return bi.getElementValue(By.id("coursename"));
	}
	
	private void addCourse(String courseID, String courseName){
		fillCourseID(courseID);
		fillCourseName(courseName);
		bi.click(By.id("btnAddCourse"));
	}
	
	private List<WebElement> getCourses(){
		return bi.getDriver().findElements(By.className("courses_row"));
	}
	
	private int getCourseRowNumber(String courseID){
		List<WebElement> courses = getCourses();
		By courseIDLocator = By.xpath(".//td[1]");
		int id = 0;
		for(WebElement el: courses){
			if(el.findElement(courseIDLocator).getText().equals(courseID)) return id;
			id++;
		}
		return -1;
	}
	
	private int getCourseIDCount(String courseID){
		List<WebElement> courses = getCourses();
		By courseIDLocator = By.xpath(".//td[1]");
		int result = 0;
		for(WebElement el: courses){
			if(el.findElement(courseIDLocator).getText().equals(courseID)) result++;
		}
		return result;
	}
	
	private String findCourseName(String courseID){
		List<WebElement> courses = getCourses();
		By courseIDLocator = By.xpath(".//td[1]");
		for(WebElement el: courses){
			String id = el.findElement(courseIDLocator).getText();
			if(id.equals(courseID)){
				return el.findElement(By.xpath(".//td[2]")).getText();
			}
		}
		return "";
	}
	
	@BeforeClass
	public static void classSetup() throws Exception {
		assertTrue(true);
		printTestClassHeader("CoordCourseAddUITest");
		ts = loadTestScenario();
		bi = BrowserInstancePool.getBrowserInstance();
		
		System.out.println("Recreating "+ts.coordinator.id);
		long start = System.currentTimeMillis();
		TMAPI.deleteCoord(ts.coordinator.id);
		TMAPI.createCoord(ts.coordinator);
		System.out.println("Finished recreating in "+(System.currentTimeMillis()-start)+" ms");
		
		bi.loginCoord(ts.coordinator.id, Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_COURSE);
	}

	@Test
	public void testCoordCourseAddHTML() {
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseEmpty.html");

		ImportTestData.main(new String[]{});
		bi.goToCourses();

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseByIDNew.html");
	
		bi.click(By.id("button_sortcoursename"));
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseByNameNew.html");
		
		bi.click(By.id("button_sortcourseid"));
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"coordListCourseByIDNew.html");
	}

	@Test
	public void testCoordCourseAddUiPaths(){
		TMAPI.deleteCourse(ts.validCourse.getID());
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.getID());
		
		// Course id only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseID = ts.validCourse.getID();
		// Course name can be any character including special characters
		String courseName = ts.validCourse.getName();
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseSuccessful");
		
		addCourse(courseID, courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);
		
		//verify course is added
		bi.verifyCourseIsAdded(courseID, courseName);
		
		/////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseWithInvalidInputsFailed");

		addCourse("", courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		// Adding course without name
		addCourse(courseID, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		addCourse(courseID+"!*}", courseName + " (!*})");
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
		
		assertEquals(shortCourseId, fillCourseID(shortCourseId));
		assertEquals(longCourseId.substring(0, Common.COURSE_ID_MAX_LENGTH), fillCourseID(longCourseId));
		
		assertEquals(shortCourseName, fillCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, Common.COURSE_NAME_MAX_LENGTH), fillCourseName(longCourseName));

		////////////////////////////////////////////////////////////////
		printTestCaseHeader("testCoordAddCourseWithDuplicateIDFailed");
		
		addCourse(courseID, "different course name");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_EXISTS);
		
		// Check that there is only one course with that ID and that it is still the old one
		long start = System.currentTimeMillis();
		assertEquals(1,getCourseIDCount(courseID));
		assertEquals(courseName,findCourseName(courseID));
		System.out.println("Time to assert a course: "+(System.currentTimeMillis()-start)+" ms");
		
		bi.verifyCoordCoursesPage();
		
		TMAPI.deleteCourse(courseID);
		TMAPI.deleteCourse(ts.courseWithSameNameDifferentId.getID());
	}

	@Test
	public void testCoordCourseAddLinks(){
		TMAPI.deleteCourse(ts.testCourse.getID());
		
		String link;
		String courseID = ts.testCourse.getID();
		String courseName = ts.testCourse.getID();
		
		addCourse(courseID, courseName);
		int courseRowID = getCourseRowNumber(courseID);
		assertTrue(courseRowID!=-1);
		
		// Check enroll link
		link = bi.getElementRelativeHref(getCoordCourseLinkLocator(courseRowID,"t_course_enroll"));
		assertEquals(Helper.getCourseEnrollLink(courseID),link);
		
		// Check view details link
		link = bi.getElementRelativeHref(getCoordCourseLinkLocator(courseRowID,"t_course_view"));
		assertEquals(Helper.getCourseViewLink(courseID),link);
		
		// Check delete link
		By deleteLinkLocator = getCoordCourseLinkLocator(courseRowID,"t_course_delete");
		link = bi.getElementRelativeHref(deleteLinkLocator);
		assertEquals(Helper.getCourseDeleteLink(courseID, Common.JSP_COORD_COURSE),link);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			bi.verifyCoordCoursesPage();
		} catch (NoAlertAppearException e){
			assertTrue("No alert box when clicking delete button at course page.",false);
		}
		
		TMAPI.deleteCourse(courseID);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddUITest");
	}
}