package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentCourseDetailsPage;

/**
 * Tests Student Course Details page
 */
public class StudentCourseDetailsPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static DataBundle testData;
	

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadTestData("/StudentCourseDetailsPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}
	
	@Test	
	public void testAll() throws Exception{
		
		______TS("content");
		
		//with teammates"
		
		verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.alice", "/studentCourseDetailsWithTeammatesHTML.html");

		//without teammates 
		
		verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.charlie", "/studentCourseDetailsWithoutTeammatesHTML.html");
		
		//no team
		
		verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.danny", "/studentCourseDetailsNoTeamHTML.html");
		
		______TS("links, inputValidation, actions");
		
		//nothing to test here.

	}

	private void verifyContent(String courseObjectId, String studentObjectId, String filePath) {
		
		Url detailsPageUrl = new Url(Common.PAGE_STUDENT_COURSE_DETAILS)
			.withUserId(testData.students.get(studentObjectId).googleId)
			.withCourseId(testData.courses.get(courseObjectId).id);
		
		loginAdminToPage(browser, detailsPageUrl, StudentCourseDetailsPage.class)
			.verifyHtml(filePath);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
}